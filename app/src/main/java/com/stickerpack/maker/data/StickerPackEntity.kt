package com.stickerpack.maker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticker_packs")
data class StickerPackEntity(
    @PrimaryKey
    val identifier: String,
    val name: String,
    val publisher: String,
    val trayImageFileName: String,
    val publisherEmail: String = "",
    val publisherWebsite: String = "",
    val privacyPolicyWebsite: String = "",
    val licenseAgreementWebsite: String = "",
    val imageDataVersion: Int = 1,
    val avoidCache: Boolean = false,
    val animatedStickerPack: Boolean = false
)
