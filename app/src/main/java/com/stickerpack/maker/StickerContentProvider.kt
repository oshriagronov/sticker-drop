package com.stickerpack.maker

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.content.res.AssetFileDescriptor
import android.util.Log
import com.stickerpack.maker.data.StickerDatabase
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileNotFoundException

class StickerContentProvider : ContentProvider() {

    companion object {
        private const val TAG = "StickerContentProvider"

        private const val METADATA = 1
        private const val METADATA_PACK = 2
        private const val STICKERS = 3
        private const val STICKERS_ASSET = 4

        // Official WhatsApp Cursor Column Names
        const val STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier"
        const val STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name"
        const val STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher"
        const val STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon"
        const val ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link"
        const val IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_store_link"
        const val PUBLISHER_EMAIL_IN_QUERY = "publisher_email"
        const val PUBLISHER_WEBSITE_IN_QUERY = "publisher_website"
        const val PRIVACY_POLICY_WEBSITE_IN_QUERY = "privacy_policy_website"
        const val LICENSE_AGREEMENT_WEBSITE_IN_QUERY = "license_agreement_website"
        const val IMAGE_DATA_VERSION_IN_QUERY = "image_data_version"
        const val AVOID_CACHE_IN_QUERY = "avoid_cache"
        const val ANIMATED_STICKER_PACK_IN_QUERY = "animated_sticker_pack"

        const val STICKER_FILE_NAME_IN_QUERY = "sticker_file_name"
        const val STICKER_EMOJI_IN_QUERY = "sticker_emoji"

        fun getDebugLogs(ctx: Context): String {
            return try {
                val file = File(ctx.filesDir, "provider_debug.log")
                if (file.exists()) file.readText() else "No logs recorded yet."
            } catch (e: Exception) {
                "Error reading logs: ${e.localizedMessage}"
            }
        }

        fun clearDebugLogs(ctx: Context) {
            try {
                File(ctx.filesDir, "provider_debug.log").delete()
            } catch (ignored: Exception) {}
        }
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private fun appendLog(msg: String) {
        val ctx = context ?: return
        try {
            Log.d(TAG, msg)
            val file = File(ctx.filesDir, "provider_debug.log")
            file.appendText("$msg\n")
        } catch (ignored: Exception) {}
    }

    override fun onCreate(): Boolean {
        val authority = (context?.packageName ?: "com.stickerpack.maker") + ".stickercontentprovider"
        uriMatcher.addURI(authority, "metadata", METADATA)
        uriMatcher.addURI(authority, "metadata/*", METADATA_PACK)
        uriMatcher.addURI(authority, "stickers/*", STICKERS)
        uriMatcher.addURI(authority, "stickers_asset/*/*", STICKERS_ASSET)
        uriMatcher.addURI(authority, "stickers_asset/*", STICKERS_ASSET)
        appendLog("StickerContentProvider onCreate authority=$authority")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val ctx = context ?: return null
        val db = StickerDatabase.getDatabase(ctx)
        val dao = db.stickerDao()

        var match = uriMatcher.match(uri)
        if (match == UriMatcher.NO_MATCH) {
            val path = uri.path ?: ""
            if (path.contains("metadata")) {
                match = METADATA
            } else if (path.contains("stickers")) {
                match = STICKERS
            }
        }

        appendLog("Query URI: $uri, Match: $match")

        return when (match) {
            METADATA, METADATA_PACK -> {
                val cursor = MatrixCursor(
                    arrayOf(
                        STICKER_PACK_IDENTIFIER_IN_QUERY,
                        STICKER_PACK_NAME_IN_QUERY,
                        STICKER_PACK_PUBLISHER_IN_QUERY,
                        STICKER_PACK_ICON_IN_QUERY,
                        ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                        IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                        PUBLISHER_EMAIL_IN_QUERY,
                        PUBLISHER_WEBSITE_IN_QUERY,
                        PRIVACY_POLICY_WEBSITE_IN_QUERY,
                        LICENSE_AGREEMENT_WEBSITE_IN_QUERY,
                        IMAGE_DATA_VERSION_IN_QUERY,
                        AVOID_CACHE_IN_QUERY,
                        ANIMATED_STICKER_PACK_IN_QUERY
                    )
                )

                val targetPackId = if (match == METADATA_PACK) uri.lastPathSegment else null

                runBlocking {
                    val packs = if (targetPackId != null) {
                        val singlePack = dao.getPackWithStickersById(targetPackId)
                        if (singlePack != null) listOf(singlePack) else emptyList()
                    } else {
                        dao.getAllPacksWithStickersDirect()
                    }

                    appendLog("Found ${packs.size} total packs in database for query target=$targetPackId")

                    for (packWithStickers in packs) {
                        appendLog("Pack ${packWithStickers.pack.identifier} has ${packWithStickers.stickers.size} stickers")
                        if (packWithStickers.stickers.size >= 3) {
                            val p = packWithStickers.pack
                            cursor.addRow(
                                arrayOf<Any?>(
                                    p.identifier,
                                    p.name,
                                    p.publisher,
                                    p.trayImageFileName,
                                    "",
                                    "",
                                    p.publisherEmail,
                                    p.publisherWebsite,
                                    p.privacyPolicyWebsite,
                                    p.licenseAgreementWebsite,
                                    p.imageDataVersion.toString(),
                                    if (p.avoidCache) 1 else 0,
                                    if (p.animatedStickerPack) 1 else 0
                                )
                            )
                            appendLog("Added metadata row for pack: ${p.identifier}, icon: ${p.trayImageFileName}")
                        }
                    }
                }
                cursor
            }
            STICKERS -> {
                val packId = uri.lastPathSegment ?: return null
                val cursor = MatrixCursor(
                    arrayOf(
                        STICKER_FILE_NAME_IN_QUERY,
                        STICKER_EMOJI_IN_QUERY
                    )
                )
                runBlocking {
                    val stickers = dao.getStickersForPack(packId)
                    appendLog("Stickers query for packId=$packId found ${stickers.size} stickers")
                    for (sticker in stickers) {
                        cursor.addRow(
                            arrayOf(
                                sticker.fileName,
                                if (sticker.emojis.isBlank()) "😀" else sticker.emojis
                            )
                        )
                        appendLog("Added sticker row: file=${sticker.fileName}, emoji=${sticker.emojis}")
                    }
                }
                cursor
            }
            else -> null
        }
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val ctx = context ?: return null
        val fileName = uri.lastPathSegment ?: throw FileNotFoundException("Invalid URI: $uri")

        appendLog("openAssetFile requested for URI: $uri, fileName: $fileName")

        val stickersDir = File(ctx.filesDir, "stickers")
        var foundFile: File? = null

        if (stickersDir.exists()) {
            foundFile = stickersDir.walkTopDown().find { it.isFile && it.name == fileName }
        }

        if (foundFile == null || !foundFile.exists()) {
            appendLog("ERROR: Sticker file not found on disk for fileName: $fileName")
            throw FileNotFoundException("Sticker file not found: $fileName")
        }

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(foundFile.absolutePath, options)
        appendLog("Opened asset file: ${foundFile.absolutePath}, size: ${foundFile.length()} bytes, dimensions: ${options.outWidth}x${options.outHeight}")

        val pfd = ParcelFileDescriptor.open(foundFile, ParcelFileDescriptor.MODE_READ_ONLY)
        return AssetFileDescriptor(pfd, 0, foundFile.length())
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val ctx = context ?: return null
        val fileName = uri.lastPathSegment ?: throw FileNotFoundException("Invalid URI: $uri")

        appendLog("openFile requested for URI: $uri, fileName: $fileName")

        val stickersDir = File(ctx.filesDir, "stickers")
        var foundFile: File? = null

        if (stickersDir.exists()) {
            foundFile = stickersDir.walkTopDown().find { it.isFile && it.name == fileName }
        }

        if (foundFile == null || !foundFile.exists()) {
            appendLog("ERROR: Sticker file not found on disk for fileName: $fileName")
            throw FileNotFoundException("Sticker file not found: $fileName")
        }

        return ParcelFileDescriptor.open(foundFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun getType(uri: Uri): String? {
        val authority = (context?.packageName ?: "com.stickerpack.maker") + ".stickercontentprovider"
        return when (uriMatcher.match(uri)) {
            METADATA -> "vnd.android.cursor.dir/vnd.$authority.metadata"
            METADATA_PACK -> "vnd.android.cursor.item/vnd.$authority.metadata"
            STICKERS -> "vnd.android.cursor.dir/vnd.$authority.stickers"
            STICKERS_ASSET -> "image/webp"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
