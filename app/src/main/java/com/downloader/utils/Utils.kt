package com.downloader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever


/**
 * Created by Rahul Abrol on 10/9/20.
 */
object Utils {
    fun decodeSampledBitmap(pathName: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        val wRatio_inv = options.outWidth.toFloat() / maxWidth
        val hRatio_inv = options.outHeight.toFloat() / maxHeight // Working with inverse ratios is more comfortable
        val finalW: Int
        val finalH: Int
        val minRatio_inv /* = max{Ratio_inv} */: Int
        if (wRatio_inv > hRatio_inv) {
            minRatio_inv = wRatio_inv.toInt()
            finalW = maxWidth
            finalH = Math.round(options.outHeight / wRatio_inv)
        } else {
            minRatio_inv = hRatio_inv.toInt()
            finalH = maxHeight
            finalW = Math.round(options.outWidth / hRatio_inv)
        }
        options.inSampleSize = pow2Ceil(minRatio_inv) // pow2Ceil: A utility function that comes later
        options.inJustDecodeBounds = false // Decode bitmap with inSampleSize set
        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathName, options),
                finalW, finalH, true)
    }

    /**
     * @return the largest power of 2 that is smaller than or equal to number.
     * WARNING: return {0b1000000...000} for ZERO input.
     */
    private fun pow2Ceil(number: Int): Int {
        return 1 shl -(Integer.numberOfLeadingZeros(number) + 1) // is equivalent to:
        // return Integer.rotateRight(1, Integer.numberOfLeadingZeros(number) + 1);
    }

    fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }
}