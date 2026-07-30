package com.stickerpack.maker.data

import androidx.room.Embedded
import androidx.room.Relation

data class StickerPackWithStickers(
    @Embedded val pack: StickerPackEntity,
    @Relation(
        parentColumn = "identifier",
        entityColumn = "packIdentifier"
    )
    val stickers: List<StickerEntity>
)
