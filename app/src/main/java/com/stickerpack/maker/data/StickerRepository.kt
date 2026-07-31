package com.stickerpack.maker.data

import android.content.Context
import android.net.Uri
import com.stickerpack.maker.util.ImageUtils
import com.stickerpack.maker.util.StickerPackImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class StickerRepository(private val context: Context) {

    private val db = StickerDatabase.getDatabase(context)
    private val dao = db.stickerDao()

    val packsFlow: Flow<List<StickerPackWithStickers>> = dao.getAllPacksWithStickersFlow()

    suspend fun getPackById(identifier: String): StickerPackWithStickers? = withContext(Dispatchers.IO) {
        dao.getPackWithStickersById(identifier)
    }

    suspend fun createNewPack(
        name: String,
        publisher: String,
        trayImageUri: Uri?
    ): String? = withContext(Dispatchers.IO) {
        val packId = "pack_${System.currentTimeMillis()}"
        val trayFileName = "tray_${System.currentTimeMillis()}.webp"

        val actualTrayFileName = if (trayImageUri != null) {
            val trayFile = ImageUtils.createTrayWebp(context, trayImageUri, packId, trayFileName)
            trayFile?.name ?: trayFileName
        } else {
            val defaultTray = ImageUtils.createDefaultTrayWebp(context, packId, trayFileName)
            defaultTray?.name ?: trayFileName
        }

        val packEntity = StickerPackEntity(
            identifier = packId,
            name = name,
            publisher = publisher,
            trayImageFileName = actualTrayFileName,
            imageDataVersion = 1
        )

        dao.insertPack(packEntity)
        packId
    }

    suspend fun updatePackMetadata(
        packIdentifier: String,
        name: String,
        publisher: String
    ): Boolean = withContext(Dispatchers.IO) {
        val pack = dao.getPackById(packIdentifier) ?: return@withContext false
        val updatedPack = pack.copy(
            name = name.trim(),
            publisher = publisher.trim(),
            imageDataVersion = pack.imageDataVersion + 1
        )
        dao.updatePack(updatedPack)
        true
    }

    suspend fun addStickerToPack(
        packIdentifier: String,
        imageUri: Uri,
        emojis: String = "😀",
        cropCircle: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val fileName = "sticker_${System.currentTimeMillis()}.webp"
        val stickerFile = ImageUtils.createStickerWebp(context, imageUri, packIdentifier, fileName, cropCircle)
            ?: return@withContext false

        val stickerEntity = StickerEntity(
            packIdentifier = packIdentifier,
            fileName = stickerFile.name,
            emojis = if (emojis.isBlank()) "😀" else emojis
        )

        dao.insertSticker(stickerEntity)
        dao.incrementImageDataVersion(packIdentifier)

        val pack = dao.getPackById(packIdentifier)
        if (pack != null) {
            val trayFile = File(context.filesDir, "stickers/$packIdentifier/${pack.trayImageFileName}")
            if (!trayFile.exists() || pack.trayImageFileName.startsWith("tray_")) {
                val newTray = ImageUtils.createTrayWebp(context, imageUri, packIdentifier, pack.trayImageFileName)
                if (newTray != null) {
                    dao.updatePack(pack.copy(trayImageFileName = newTray.name))
                }
            }
        }

        true
    }

    suspend fun deleteSticker(sticker: StickerEntity) = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "stickers/${sticker.packIdentifier}/${sticker.fileName}")
        if (file.exists()) file.delete()

        dao.deleteSticker(sticker)
        dao.incrementImageDataVersion(sticker.packIdentifier)
    }

    suspend fun deletePack(packIdentifier: String) = withContext(Dispatchers.IO) {
        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (packDir.exists()) packDir.deleteRecursively()

        dao.deletePackById(packIdentifier)
    }

    suspend fun importPackFromJson(jsonUri: Uri): String? = withContext(Dispatchers.IO) {
        StickerPackImporter.importPackFromJson(context, jsonUri)
    }

    suspend fun importPackFromZip(zipUri: Uri): String? = withContext(Dispatchers.IO) {
        StickerPackImporter.importPackFromZip(context, zipUri)
    }
}
