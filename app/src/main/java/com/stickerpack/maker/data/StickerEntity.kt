package com.stickerpack.maker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stickers",
    foreignKeys = [
        ForeignKey(
            entity = StickerPackEntity::class,
            parentColumns = ["identifier"],
            childColumns = ["packIdentifier"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packIdentifier")]
)
data class StickerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packIdentifier: String,
    val fileName: String,
    val emojis: String = "😀"
)
