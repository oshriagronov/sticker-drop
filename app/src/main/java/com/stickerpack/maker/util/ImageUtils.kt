package com.stickerpack.maker.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

object ImageUtils {

    const val STICKER_SIZE = 512
    const val STICKER_CONTENT_SIZE = 480 // 16px margin on all sides per WhatsApp spec
    const val TRAY_SIZE = 96
    const val MAX_STICKER_BYTES = 100 * 1024 // 100 KB
    const val MAX_TRAY_BYTES = 50 * 1024    // 50 KB

    /**
     * Converts an image into a 512x512 WebP sticker file with transparent margin.
     */
    fun createStickerWebp(
        context: Context,
        inputUri: Uri,
        packIdentifier: String,
        outputFileName: String,
        cropCircle: Boolean = false
    ): File? {
        val originalBitmap = loadBitmap(context, inputUri) ?: return null

        var sourceBitmap = originalBitmap
        if (cropCircle) {
            sourceBitmap = getCircularBitmap(originalBitmap)
        }

        // 512x512 transparent canvas
        val canvasBitmap = Bitmap.createBitmap(STICKER_SIZE, STICKER_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val scale = min(
            STICKER_CONTENT_SIZE.toFloat() / sourceBitmap.width,
            STICKER_CONTENT_SIZE.toFloat() / sourceBitmap.height
        )
        val scaledWidth = sourceBitmap.width * scale
        val scaledHeight = sourceBitmap.height * scale
        val left = (STICKER_SIZE - scaledWidth) / 2f
        val top = (STICKER_SIZE - scaledHeight) / 2f

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(sourceBitmap, null, destRect, paint)

        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val outputFile = File(packDir, outputFileName)
        val success = compressToWebp(canvasBitmap, outputFile, MAX_STICKER_BYTES)

        if (sourceBitmap != originalBitmap) sourceBitmap.recycle()
        originalBitmap.recycle()
        canvasBitmap.recycle()

        return if (success) outputFile else null
    }

    /**
     * Converts an image into a 96x96 WebP tray icon file.
     */
    fun createTrayWebp(
        context: Context,
        inputUri: Uri,
        packIdentifier: String,
        outputFileName: String
    ): File? {
        val originalBitmap = loadBitmap(context, inputUri) ?: return null

        val canvasBitmap = Bitmap.createBitmap(TRAY_SIZE, TRAY_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val scale = min(
            TRAY_SIZE.toFloat() / originalBitmap.width,
            TRAY_SIZE.toFloat() / originalBitmap.height
        )
        val scaledWidth = originalBitmap.width * scale
        val scaledHeight = originalBitmap.height * scale
        val left = (TRAY_SIZE - scaledWidth) / 2f
        val top = (TRAY_SIZE - scaledHeight) / 2f

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, null, destRect, paint)

        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val outputFile = File(packDir, outputFileName)
        val success = compressToWebp(canvasBitmap, outputFile, MAX_TRAY_BYTES)

        originalBitmap.recycle()
        canvasBitmap.recycle()

        return if (success) outputFile else null
    }

    /**
     * Converts a local image file into a 96x96 WebP tray icon file.
     */
    fun createTrayFromFile(
        context: Context,
        sourceFile: File,
        packIdentifier: String,
        outputFileName: String
    ): File? {
        val originalBitmap = try {
            BitmapFactory.decodeFile(sourceFile.absolutePath)
        } catch (e: Exception) {
            null
        } ?: return null

        val canvasBitmap = Bitmap.createBitmap(TRAY_SIZE, TRAY_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val scale = min(
            TRAY_SIZE.toFloat() / originalBitmap.width,
            TRAY_SIZE.toFloat() / originalBitmap.height
        )
        val scaledWidth = originalBitmap.width * scale
        val scaledHeight = originalBitmap.height * scale
        val left = (TRAY_SIZE - scaledWidth) / 2f
        val top = (TRAY_SIZE - scaledHeight) / 2f

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, null, destRect, paint)

        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val outputFile = File(packDir, outputFileName)
        val success = compressToWebp(canvasBitmap, outputFile, MAX_TRAY_BYTES)

        originalBitmap.recycle()
        canvasBitmap.recycle()

        return if (success) outputFile else null
    }

    /**
     * Formats an image file into a 512x512 WebP sticker file compliant with WhatsApp specs.
     * Returns null if the image is small (e.g. 96x96 tray icon) or invalid.
     */
    fun formatStickerFromFile(
        context: Context,
        sourceFile: File,
        packIdentifier: String,
        outputFileName: String
    ): File? {
        val originalBitmap = try {
            BitmapFactory.decodeFile(sourceFile.absolutePath)
        } catch (e: Exception) {
            null
        } ?: return null

        // Reject tray icons / small images (width <= 128 and height <= 128)
        if (originalBitmap.width <= 128 && originalBitmap.height <= 128) {
            originalBitmap.recycle()
            return null
        }

        // 512x512 transparent canvas
        val canvasBitmap = Bitmap.createBitmap(STICKER_SIZE, STICKER_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val scale = min(
            STICKER_CONTENT_SIZE.toFloat() / originalBitmap.width,
            STICKER_CONTENT_SIZE.toFloat() / originalBitmap.height
        )
        val scaledWidth = originalBitmap.width * scale
        val scaledHeight = originalBitmap.height * scale
        val left = (STICKER_SIZE - scaledWidth) / 2f
        val top = (STICKER_SIZE - scaledHeight) / 2f

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, null, destRect, paint)

        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val outputFile = File(packDir, outputFileName)
        val success = compressToWebp(canvasBitmap, outputFile, MAX_STICKER_BYTES)

        originalBitmap.recycle()
        canvasBitmap.recycle()

        return if (success) outputFile else null
    }

    /**
     * Creates a default placeholder 96x96 WebP tray icon.
     */
    fun createDefaultTrayWebp(
        context: Context,
        packIdentifier: String,
        outputFileName: String
    ): File? {
        val bitmap = Bitmap.createBitmap(TRAY_SIZE, TRAY_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#25D366"))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 40f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText("S", canvas.width / 2f, yPos, paint)

        val packDir = File(context.filesDir, "stickers/$packIdentifier")
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val outputFile = File(packDir, outputFileName)
        val success = compressToWebp(bitmap, outputFile, MAX_TRAY_BYTES)
        bitmap.recycle()

        return if (success) outputFile else null
    }

    private fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        val radius = min(bitmap.width, bitmap.height) / 2f
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    @Suppress("DEPRECATION")
    private fun compressToWebp(bitmap: Bitmap, outputFile: File, maxSizeBytes: Int): Boolean {
        var stream = ByteArrayOutputStream()
        var quality = 90
        
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }

        bitmap.compress(format, quality, stream)

        while (stream.toByteArray().size > maxSizeBytes && quality > 10) {
            quality -= 10
            stream.reset()
            bitmap.compress(format, quality, stream)
        }

        return try {
            val fos = FileOutputStream(outputFile)
            fos.write(stream.toByteArray())
            fos.flush()
            fos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
