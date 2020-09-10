package com.downloader.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import timber.log.Timber
import java.security.MessageDigest


/**
 * Created by Rahul Abrol on 10/9/20.
 */
class BlurTransformation(context: Context?) : BitmapTransformation() {
    private val rs: RenderScript = RenderScript.create(context)

    fun getId(): String {
        return "blur"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        Timber.e("BlurTransformation inside updateDiskCacheKey")
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true)

        // Allocate memory for Renderscript to work with
        val input = Allocation.createFromBitmap(
                rs,
                blurredBitmap,
                Allocation.MipmapControl.MIPMAP_FULL,
                Allocation.USAGE_SHARED
        )
        val output = Allocation.createTyped(rs, input.type)

        // Load up an instance of the specific script that we want to use.
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setInput(input)

        // Set the blur radius
        script.setRadius(10f)

        // Start the ScriptIntrinisicBlur
        script.forEach(output)

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap)
        toTransform.recycle()
        return blurredBitmap
    }

}