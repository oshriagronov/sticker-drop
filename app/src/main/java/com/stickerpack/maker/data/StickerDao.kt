package com.stickerpack.maker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDao {
    @Transaction
    @Query("SELECT * FROM sticker_packs")
    fun getAllPacksWithStickersFlow(): Flow<List<StickerPackWithStickers>>

    @Transaction
    @Query("SELECT * FROM sticker_packs")
    suspend fun getAllPacksWithStickersDirect(): List<StickerPackWithStickers>

    @Transaction
    @Query("SELECT * FROM sticker_packs WHERE identifier = :identifier LIMIT 1")
    suspend fun getPackWithStickersById(identifier: String): StickerPackWithStickers?

    @Query("SELECT * FROM sticker_packs WHERE identifier = :identifier LIMIT 1")
    suspend fun getPackById(identifier: String): StickerPackEntity?

    @Query("SELECT * FROM stickers WHERE packIdentifier = :identifier")
    suspend fun getStickersForPack(identifier: String): List<StickerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(pack: StickerPackEntity)

    @Update
    suspend fun updatePack(pack: StickerPackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSticker(sticker: StickerEntity)

    @Delete
    suspend fun deleteSticker(sticker: StickerEntity)

    @Query("DELETE FROM stickers WHERE id = :stickerId")
    suspend fun deleteStickerById(stickerId: Long)

    @Query("DELETE FROM sticker_packs WHERE identifier = :identifier")
    suspend fun deletePackById(identifier: String)

    @Query("UPDATE sticker_packs SET imageDataVersion = imageDataVersion + 1 WHERE identifier = :identifier")
    suspend fun incrementImageDataVersion(identifier: String)
}
