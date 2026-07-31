package com.stickerpack.maker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.stickerpack.maker.data.StickerPackWithStickers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object StickerPackExporter {

    private fun sanitizeFileName(name: String): String {
        val clean = name.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").trim()
        return if (clean.isBlank()) "sticker_pack" else clean
    }

    fun exportPackToWastickersFile(context: Context, packWithStickers: StickerPackWithStickers): File? {
        val pack = packWithStickers.pack
        val stickers = packWithStickers.stickers

        val exportsDir = File(context.cacheDir, "exports")
        if (!exportsDir.exists()) exportsDir.mkdirs()

        val cleanName = sanitizeFileName(pack.name)
        val outFile = File(exportsDir, "$cleanName.wastickers")
        if (outFile.exists()) outFile.delete()

        try {
            ZipOutputStream(FileOutputStream(outFile)).use { zos ->
                // 1. Write title.txt
                zos.putNextEntry(ZipEntry("title.txt"))
                zos.write(pack.name.toByteArray(Charsets.UTF_8))
                zos.closeEntry()

                // 2. Write author.txt
                zos.putNextEntry(ZipEntry("author.txt"))
                zos.write(pack.publisher.toByteArray(Charsets.UTF_8))
                zos.closeEntry()

                // 3. Write contents.json
                val jsonMap = mapOf(
                    "identifier" to pack.identifier,
                    "name" to pack.name,
                    "publisher" to pack.publisher,
                    "tray_image_file" to pack.trayImageFileName,
                    "stickers" to stickers.map { s ->
                        mapOf(
                            "image_file" to s.fileName,
                            "emojis" to listOf(if (s.emojis.isBlank()) "😀" else s.emojis)
                        )
                    }
                )
                val jsonString = Gson().toJson(jsonMap)
                zos.putNextEntry(ZipEntry("contents.json"))
                zos.write(jsonString.toByteArray(Charsets.UTF_8))
                zos.closeEntry()

                // 4. Write tray image
                val trayFile = File(context.filesDir, "stickers/${pack.identifier}/${pack.trayImageFileName}")
                if (trayFile.exists()) {
                    zos.putNextEntry(ZipEntry(pack.trayImageFileName))
                    FileInputStream(trayFile).use { input ->
                        input.copyTo(zos)
                    }
                    zos.closeEntry()
                }

                // 5. Write stickers
                for (sticker in stickers) {
                    val sFile = File(context.filesDir, "stickers/${pack.identifier}/${sticker.fileName}")
                    if (sFile.exists()) {
                        zos.putNextEntry(ZipEntry(sticker.fileName))
                        FileInputStream(sFile).use { input ->
                            input.copyTo(zos)
                        }
                        zos.closeEntry()
                    }
                }
            }
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun sharePack(context: Context, packWithStickers: StickerPackWithStickers) {
        val file = exportPackToWastickersFile(context, packWithStickers)
        if (file == null || !file.exists()) {
            Toast.makeText(context, "Failed to export pack", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/wastickers"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, packWithStickers.pack.name)
                putExtra(Intent.EXTRA_TEXT, "Check out my sticker pack: ${packWithStickers.pack.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Sticker Pack"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing pack: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareAllPacks(context: Context, packs: List<StickerPackWithStickers>) {
        if (packs.isEmpty()) {
            Toast.makeText(context, "No packs available to export", Toast.LENGTH_SHORT).show()
            return
        }

        val exportedUris = ArrayList<Uri>()
        for (packWithStickers in packs) {
            val file = exportPackToWastickersFile(context, packWithStickers)
            if (file != null && file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                exportedUris.add(uri)
            }
        }

        if (exportedUris.isEmpty()) {
            Toast.makeText(context, "Failed to export sticker packs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, exportedUris)
                putExtra(Intent.EXTRA_SUBJECT, "Exported Sticker Packs")
                putExtra(Intent.EXTRA_TEXT, "Here are my exported sticker packs from StickerDrop!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Export All Sticker Packs"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error exporting all packs: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
