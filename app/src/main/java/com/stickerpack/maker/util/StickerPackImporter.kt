package com.stickerpack.maker.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.stickerpack.maker.data.StickerDatabase
import com.stickerpack.maker.data.StickerEntity
import com.stickerpack.maker.data.StickerPackEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

data class ImportedStickerJson(
    @SerializedName("image_file") val imageFile: String,
    @SerializedName("emojis") val emojis: List<String>? = null
)

data class ImportedPackJson(
    @SerializedName("identifier") val identifier: String?,
    @SerializedName("name") val name: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("tray_image_file") val trayImageFile: String,
    @SerializedName("stickers") val stickers: List<ImportedStickerJson>
)

object StickerPackImporter {

    suspend fun importPackFromJson(context: Context, jsonUri: Uri): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(jsonUri) ?: return null
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val packJson = Gson().fromJson(jsonString, ImportedPackJson::class.java)

            val rawId = packJson.identifier ?: "imported_${System.currentTimeMillis()}"
            val packId = rawId.lowercase().replace(Regex("[^a-z0-9_-]"), "_")
            val db = StickerDatabase.getDatabase(context)
            val dao = db.stickerDao()

            val packEntity = StickerPackEntity(
                identifier = packId,
                name = packJson.name,
                publisher = packJson.publisher,
                trayImageFileName = packJson.trayImageFile,
                imageDataVersion = 1
            )
            dao.insertPack(packEntity)

            packJson.stickers.forEach { stickerJson ->
                val emojiList = stickerJson.emojis?.joinToString(",") ?: "😀"
                val stickerEntity = StickerEntity(
                    packIdentifier = packId,
                    fileName = stickerJson.imageFile,
                    emojis = if (emojiList.isBlank()) "😀" else emojiList
                )
                dao.insertSticker(stickerEntity)
            }

            packId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun importPackFromZip(context: Context, zipUri: Uri): String? {
        return try {
            val packId = "pack_${System.currentTimeMillis()}"
            val packDir = File(context.filesDir, "stickers/$packId")
            if (!packDir.exists()) packDir.mkdirs()

            var packName = "Imported Pack"
            var publisherName = "Sticker Maker"
            var trayFileName = "tray.webp"
            val stickerFiles = mutableListOf<String>()

            val inputStream: InputStream = context.contentResolver.openInputStream(zipUri) ?: return null
            val zipInput = ZipInputStream(inputStream)
            var entry = zipInput.nextEntry

            var metaJsonString: String? = null

            while (entry != null) {
                val fileName = File(entry.name).name
                if (!entry.isDirectory && fileName.isNotEmpty()) {
                    if (fileName.equals("title.txt", ignoreCase = true)) {
                        packName = zipInput.bufferedReader().readLine() ?: packName
                    } else if (fileName.equals("author.txt", ignoreCase = true)) {
                        publisherName = zipInput.bufferedReader().readLine() ?: publisherName
                    } else if (fileName.equals("contents.json", ignoreCase = true)) {
                        metaJsonString = zipInput.bufferedReader().readText()
                    } else if (fileName.endsWith(".webp", ignoreCase = true) || fileName.endsWith(".png", ignoreCase = true)) {
                        val outputFile = File(packDir, fileName)
                        FileOutputStream(outputFile).use { out ->
                            zipInput.copyTo(out)
                        }
                        if (fileName.startsWith("tray", ignoreCase = true) || fileName.contains("icon", ignoreCase = true)) {
                            trayFileName = fileName
                        } else {
                            stickerFiles.add(fileName)
                        }
                    }
                }
                zipInput.closeEntry()
                entry = zipInput.nextEntry
            }
            zipInput.close()

            if (metaJsonString != null) {
                try {
                    val packJson = Gson().fromJson(metaJsonString, ImportedPackJson::class.java)
                    if (packJson.name.isNotBlank()) packName = packJson.name
                    if (packJson.publisher.isNotBlank()) publisherName = packJson.publisher
                    if (packJson.trayImageFile.isNotBlank()) trayFileName = packJson.trayImageFile
                } catch (ignored: Exception) {}
            }

            if (!File(packDir, trayFileName).exists() && stickerFiles.isNotEmpty()) {
                trayFileName = stickerFiles.first()
            }

            val db = StickerDatabase.getDatabase(context)
            val dao = db.stickerDao()

            val packEntity = StickerPackEntity(
                identifier = packId,
                name = packName,
                publisher = publisherName,
                trayImageFileName = trayFileName,
                imageDataVersion = 1
            )
            dao.insertPack(packEntity)

            stickerFiles.forEach { file ->
                dao.insertSticker(
                    StickerEntity(
                        packIdentifier = packId,
                        fileName = file,
                        emojis = "😀"
                    )
                )
            }

            packId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
