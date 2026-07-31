package com.stickerpack.maker.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.stickerpack.maker.StickerContentProvider
import com.stickerpack.maker.data.StickerDatabase
import com.stickerpack.maker.data.StickerEntity
import com.stickerpack.maker.data.StickerPackEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

data class ImportedStickerJson(
    @SerializedName("image_file") val imageFile: String? = null,
    @SerializedName("emojis") val emojis: List<String>? = null
)

data class ImportedPackJson(
    @SerializedName("identifier") val identifier: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("publisher") val publisher: String? = null,
    @SerializedName("tray_image_file") val trayImageFile: String? = null,
    @SerializedName("stickers") val stickers: List<ImportedStickerJson>? = null
)

object StickerPackImporter {

    private const val TAG = "StickerImport"

    private fun log(context: Context, message: String) {
        Log.d(TAG, message)
        StickerContentProvider.log(context, "[Import] $message")
    }

    private fun openStream(context: Context, uri: Uri): InputStream? {
        return try {
            context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            try {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.createInputStream()
            } catch (e2: Exception) {
                try {
                    val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                    if (pfd != null) FileInputStream(pfd.fileDescriptor) else null
                } catch (e3: Exception) {
                    Log.e(TAG, "All openStream attempts failed for $uri", e3)
                    null
                }
            }
        }
    }

    private fun readEntryText(zipInput: ZipInputStream): String {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (zipInput.read(buffer).also { bytesRead = it } != -1) {
            baos.write(buffer, 0, bytesRead)
        }
        return baos.toString(Charsets.UTF_8.name())
    }

    private fun copyEntryToFile(zipInput: ZipInputStream, outputFile: File) {
        FileOutputStream(outputFile).use { out ->
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (zipInput.read(buffer).also { bytesRead = it } != -1) {
                out.write(buffer, 0, bytesRead)
            }
        }
    }

    private fun isImageFileByHeader(file: File): Boolean {
        if (!file.exists() || file.length() < 12) return false
        return try {
            val bytes = ByteArray(12)
            FileInputStream(file).use { input ->
                input.read(bytes, 0, 12)
            }
            // Check PNG
            if (bytes[0] == 0x89.toByte() && bytes[1] == 'P'.code.toByte() && bytes[2] == 'N'.code.toByte() && bytes[3] == 'G'.code.toByte()) return true
            // Check JPEG
            if (bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && bytes[2] == 0xFF.toByte()) return true
            // Check WEBP (RIFF...WEBP)
            if (bytes[0] == 'R'.code.toByte() && bytes[1] == 'I'.code.toByte() && bytes[2] == 'F'.code.toByte() && bytes[3] == 'F'.code.toByte() &&
                bytes[8] == 'W'.code.toByte() && bytes[9] == 'E'.code.toByte() && bytes[10] == 'B'.code.toByte() && bytes[11] == 'P'.code.toByte()) return true
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun importPackFromJson(context: Context, jsonUri: Uri): String? {
        log(context, "importPackFromJson starting for URI: $jsonUri")
        return try {
            val inputStream: InputStream = openStream(context, jsonUri) ?: run {
                log(context, "ERROR: Could not open InputStream for JSON URI")
                return null
            }
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            importPackFromJsonContent(context, jsonString)
        } catch (e: Exception) {
            log(context, "ERROR importing pack from JSON Uri: ${e.localizedMessage}")
            null
        }
    }

    private suspend fun importPackFromJsonContent(context: Context, jsonString: String): String? {
        return try {
            val packJson = Gson().fromJson(jsonString, ImportedPackJson::class.java) ?: run {
                log(context, "ERROR: Gson returned null for JSON content")
                return null
            }
            val name = packJson.name ?: "Imported Pack"
            val publisher = packJson.publisher ?: "Sticker Maker"
            val trayFile = packJson.trayImageFile ?: "tray.webp"

            val rawId = packJson.identifier ?: "imported_${System.currentTimeMillis()}"
            val packId = rawId.lowercase().replace(Regex("[^a-z0-9_-]"), "_")
            val db = StickerDatabase.getDatabase(context)
            val dao = db.stickerDao()

            val packEntity = StickerPackEntity(
                identifier = packId,
                name = name,
                publisher = publisher,
                trayImageFileName = trayFile,
                imageDataVersion = 1
            )
            dao.insertPack(packEntity)

            var count = 0
            packJson.stickers?.forEach { stickerJson ->
                val fileName = stickerJson.imageFile ?: return@forEach
                val emojiList = stickerJson.emojis?.joinToString(",") ?: "😀"
                val stickerEntity = StickerEntity(
                    packIdentifier = packId,
                    fileName = fileName,
                    emojis = if (emojiList.isBlank()) "😀" else emojiList
                )
                dao.insertSticker(stickerEntity)
                count++
            }

            log(context, "SUCCESS: Imported JSON pack '$name' (ID: $packId) with $count stickers")
            packId
        } catch (e: Exception) {
            log(context, "ERROR parsing JSON pack content: ${e.localizedMessage}")
            null
        }
    }

    private fun disableZipPathValidation(context: Context) {
        try {
            val clazz = Class.forName("dalvik.system.ZipPathValidator")
            val callbackClazz = Class.forName("dalvik.system.ZipPathValidator\$Callback")
            val setCallbackMethod = clazz.getMethod("setCallback", callbackClazz)
            val lenientField = callbackClazz.getField("LENIENT")
            val lenientValue = lenientField.get(null)
            setCallbackMethod.invoke(null, lenientValue)
            log(context, "ZipPathValidator set to LENIENT via reflection")
        } catch (t: Throwable) {
            log(context, "Notice: Could not set ZipPathValidator to LENIENT: ${t.localizedMessage}")
        }
    }

    private fun sanitizeZipFile(context: Context, file: File) {
        try {
            val bytes = file.readBytes()
            var modifiedCount = 0
            val len = bytes.size
            var i = 0
            while (i < len - 30) {
                // Check Local File Header PK\x03\x04
                if (bytes[i] == 0x50.toByte() && bytes[i + 1] == 0x4B.toByte() && bytes[i + 2] == 0x03.toByte() && bytes[i + 3] == 0x04.toByte()) {
                    val fileNameLen = (bytes[i + 26].toInt() and 0xFF) or ((bytes[i + 27].toInt() and 0xFF) shl 8)
                    val nameOffset = i + 30
                    if (fileNameLen > 0 && nameOffset < len && bytes[nameOffset] == '/'.code.toByte()) {
                        bytes[nameOffset] = '_'.code.toByte()
                        modifiedCount++
                    }
                }
                // Check Central Directory Header PK\x01\x02
                else if (bytes[i] == 0x50.toByte() && bytes[i + 1] == 0x4B.toByte() && bytes[i + 2] == 0x01.toByte() && bytes[i + 3] == 0x02.toByte()) {
                    val fileNameLen = (bytes[i + 28].toInt() and 0xFF) or ((bytes[i + 29].toInt() and 0xFF) shl 8)
                    val nameOffset = i + 46
                    if (fileNameLen > 0 && nameOffset < len && bytes[nameOffset] == '/'.code.toByte()) {
                        bytes[nameOffset] = '_'.code.toByte()
                        modifiedCount++
                    }
                }
                i++
            }
            if (modifiedCount > 0) {
                file.writeBytes(bytes)
                log(context, "Sanitized $modifiedCount zip entries with leading slashes in temporary zip file")
            } else {
                log(context, "No zip entries with leading slashes found to sanitize")
            }
        } catch (e: Exception) {
            log(context, "Notice: sanitizeZipFile error: ${e.localizedMessage}")
        }
    }

    suspend fun importPackFromZip(context: Context, zipUri: Uri): String? {
        log(context, "importPackFromZip starting for URI: $zipUri")
        val tempFile = File(context.cacheDir, "import_temp_${System.currentTimeMillis()}.tmp")
        return try {
            val inputStream = openStream(context, zipUri)
            if (inputStream == null) {
                log(context, "ERROR: Unable to open input stream for URI: $zipUri")
                return null
            }

            inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            log(context, "Copied URI stream to temp file: ${tempFile.absolutePath} (${tempFile.length()} bytes)")

            if (tempFile.length() == 0L) {
                log(context, "ERROR: Imported temp file is 0 bytes")
                return null
            }

            // Fix Android 14+ ZipPathValidator leading slash restriction
            disableZipPathValidation(context)
            sanitizeZipFile(context, tempFile)

            val packId = "pack_${System.currentTimeMillis()}"
            val packDir = File(context.filesDir, "stickers/$packId")
            if (!packDir.exists()) packDir.mkdirs()

            var fallbackName = "Imported Pack"
            try {
                context.contentResolver.query(zipUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        val displayName = cursor.getString(nameIndex)
                        if (!displayName.isNullOrBlank()) {
                            val cleanName = displayName.replace(Regex("\\.wastickers|\\.zip|\\.json", RegexOption.IGNORE_CASE), "")
                                .replace("_", " ").trim()
                            if (cleanName.isNotBlank()) {
                                fallbackName = cleanName
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                log(context, "Notice: Could not query DISPLAY_NAME from cursor: ${e.localizedMessage}")
            }

            var packName = fallbackName
            var publisherName = "Sticker Maker"
            var trayFileName = ""
            val stickerFiles = mutableListOf<String>()
            val stickerEmojis = mutableMapOf<String, String>()
            var metaJsonString: String? = null

            val zipInput = ZipInputStream(FileInputStream(tempFile))
            var entry = zipInput.nextEntry
            var entryCount = 0

            while (entry != null) {
                entryCount++
                val entryPath = entry.name
                val fileName = File(entryPath).name

                if (!entry.isDirectory && fileName.isNotEmpty() && !fileName.startsWith(".") && !entryPath.contains("__MACOSX")) {
                    val lowerName = fileName.lowercase()
                    val cleanLowerName = lowerName.removePrefix("/").removePrefix("_").trim()
                    log(context, "Zip Entry #$entryCount: '$entryPath' (name: '$fileName', clean: '$cleanLowerName')")

                    if (cleanLowerName == "title.txt") {
                        val text = readEntryText(zipInput)
                        if (text.isNotBlank()) {
                            packName = text.trim()
                            log(context, "Parsed title.txt: '$packName'")
                        }
                    } else if (cleanLowerName == "author.txt") {
                        val text = readEntryText(zipInput)
                        if (text.isNotBlank()) {
                            publisherName = text.trim()
                            log(context, "Parsed author.txt: '$publisherName'")
                        }
                    } else if (cleanLowerName == "contents.json") {
                        metaJsonString = readEntryText(zipInput)
                        log(context, "Parsed contents.json (${metaJsonString.length} chars)")
                    } else {
                        var targetFileName = fileName
                        if (!targetFileName.contains(".")) {
                            targetFileName = "$fileName.webp"
                        }
                        val outputFile = File(packDir, targetFileName)
                        copyEntryToFile(zipInput, outputFile)

                        val isKnownExt = lowerName.endsWith(".webp") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")
                        val isImage = isKnownExt || isImageFileByHeader(outputFile)

                        if (isImage) {
                            val lowerTarget = targetFileName.lowercase()
                            val cleanTarget = lowerTarget.removePrefix("/").removePrefix("_")
                            val isExplicitTray = cleanTarget.startsWith("tray") || cleanTarget.contains("icon") || cleanTarget == "0.webp" || cleanTarget == "0.png"

                            if (isExplicitTray) {
                                if (trayFileName.isEmpty()) {
                                    trayFileName = targetFileName
                                    log(context, "Identified explicit tray icon candidate: '$targetFileName'")
                                }
                            } else {
                                // Format image to 512x512 WebP sticker compliant with WhatsApp
                                val formattedSticker = ImageUtils.formatStickerFromFile(context, outputFile, packId, targetFileName)
                                if (formattedSticker != null) {
                                    stickerFiles.add(targetFileName)
                                    log(context, "Formatted & added 512x512 WebP sticker: '$targetFileName'")
                                } else {
                                    // Image is small (<=128px) - candidate for tray icon, not 512x512 sticker
                                    if (trayFileName.isEmpty()) {
                                        trayFileName = targetFileName
                                        log(context, "Identified small image (<=128px) as tray icon candidate: '$targetFileName'")
                                    } else {
                                        log(context, "Skipped small image (<=128px): '$targetFileName'")
                                    }
                                }
                            }
                        } else {
                            log(context, "Skipping non-image entry: '$targetFileName'")
                            if (outputFile.exists()) outputFile.delete()
                        }
                    }
                }
                zipInput.closeEntry()
                entry = zipInput.nextEntry
            }
            zipInput.close()

            log(context, "Finished scanning zip. Total entries=$entryCount, extracted stickers=${stickerFiles.size}")

            if (metaJsonString != null) {
                try {
                    val packJson = Gson().fromJson(metaJsonString, ImportedPackJson::class.java)
                    if (!packJson.name.isNullOrBlank()) packName = packJson.name
                    if (!packJson.publisher.isNullOrBlank()) publisherName = packJson.publisher
                    if (!packJson.trayImageFile.isNullOrBlank()) trayFileName = packJson.trayImageFile

                    packJson.stickers?.forEach { sJson ->
                        val fName = sJson.imageFile
                        if (fName != null && !sJson.emojis.isNullOrEmpty()) {
                            stickerEmojis[fName] = sJson.emojis.joinToString(",")
                        }
                    }
                } catch (e: Exception) {
                    log(context, "Warning: Could not parse contents.json: ${e.localizedMessage}")
                }
            }

            if (stickerFiles.isEmpty() && metaJsonString == null) {
                log(context, "No stickers found in ZIP. Checking if file is raw JSON...")
                try {
                    val rawText = tempFile.readText()
                    if (rawText.trim().startsWith("{")) {
                        log(context, "Attempting raw JSON import fallback...")
                        val jsonResult = importPackFromJsonContent(context, rawText)
                        if (jsonResult != null) return jsonResult
                    }
                } catch (e: Exception) {
                    log(context, "Raw JSON check exception: ${e.localizedMessage}")
                }
            }

            if (stickerFiles.isEmpty()) {
                log(context, "ERROR: No sticker image files (.webp, .png, .jpg) were found in the package!")
                return null
            }

            // Always format a compliant 96x96 WebP tray icon for WhatsApp compatibility
            val rawTrayFile = if (trayFileName.isNotBlank() && File(packDir, trayFileName).exists()) {
                File(packDir, trayFileName)
            } else {
                File(packDir, stickerFiles.first())
            }

            val finalTrayFileName = "tray_icon.webp"
            val generatedTray = ImageUtils.createTrayFromFile(context, rawTrayFile, packId, finalTrayFileName)
            val actualTrayFileName = if (generatedTray != null) {
                log(context, "Successfully generated 96x96 WebP tray icon from '${rawTrayFile.name}'")
                finalTrayFileName
            } else {
                val defaultTray = ImageUtils.createDefaultTrayWebp(context, packId, finalTrayFileName)
                log(context, "Generated default green 96x96 WebP tray icon")
                defaultTray?.name ?: finalTrayFileName
            }

            val db = StickerDatabase.getDatabase(context)
            val dao = db.stickerDao()

            val packEntity = StickerPackEntity(
                identifier = packId,
                name = packName,
                publisher = publisherName,
                trayImageFileName = actualTrayFileName,
                imageDataVersion = 1
            )
            dao.insertPack(packEntity)

            stickerFiles.forEach { file ->
                val emojiStr = stickerEmojis[file] ?: "😀"
                dao.insertSticker(
                    StickerEntity(
                        packIdentifier = packId,
                        fileName = file,
                        emojis = emojiStr
                    )
                )
            }

            log(context, "SUCCESS: Created pack ID '$packId' ('$packName') with ${stickerFiles.size} stickers!")
            packId
        } catch (e: Exception) {
            log(context, "ERROR in importPackFromZip: ${e.localizedMessage}\n${Log.getStackTraceString(e)}")
            null
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
}
