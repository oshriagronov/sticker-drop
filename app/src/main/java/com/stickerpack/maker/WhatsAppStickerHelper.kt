package com.stickerpack.maker

import android.content.Context
import android.content.Intent

object WhatsAppStickerHelper {

    const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
    const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
    const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"

    const val ACTION_ENABLE_STICKER_PACK = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"

    /**
     * Creates the exact pure implicit Intent expected by WhatsApp's StickerPackInstallerActivity.
     */
    fun createAddPackIntent(context: Context, packIdentifier: String, packName: String): Intent {
        val authority = "${context.packageName}.stickercontentprovider"
        return Intent(ACTION_ENABLE_STICKER_PACK).apply {
            putExtra(EXTRA_STICKER_PACK_ID, packIdentifier)
            putExtra(EXTRA_STICKER_PACK_AUTHORITY, authority)
            putExtra(EXTRA_STICKER_PACK_NAME, packName)
        }
    }
}
