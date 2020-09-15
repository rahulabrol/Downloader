package com.downloader.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.IOException


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

    fun getRealPathFromURI(context: Context, contentUri: Uri): Array<String>? {
        val proj = arrayOf(MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID)
        val cursor: Cursor? = context.contentResolver.query(contentUri,
                proj, null, null, null)
        cursor?.let {

            val path_index: Int = cursor.getColumnIndexOrThrow(proj[0])
            val id_index: Int = cursor.getColumnIndexOrThrow(proj[1])
            cursor.moveToFirst()
            return arrayOf(cursor.getString(path_index), cursor.getLong(id_index).toString() + "")
        }
        return arrayOf()
    }

    fun getThumbnail(contentResolver: ContentResolver, id: Long): Bitmap? {
        val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DATA),  // Which columns
                // to return
                MediaStore.Images.Media._ID + "=?", arrayOf(id.toString()),  // Selection arguments
                null) // order
        return if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            val filePath = cursor.getString(0)
            cursor.close()
            var rotation = 0
            try {
                val exifInterface = ExifInterface(filePath)
                val exifRotation: Int = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)
                if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
                    when (exifRotation) {
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    }
                }
            } catch (e: IOException) {
                Log.e("getThumbnail", e.toString())
            }
            var bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null)
            if (rotation != 0) {
                val matrix = Matrix()
                matrix.setRotate(rotation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                        bitmap.height, matrix, true)
            }
            bitmap
        } else null
    }
}