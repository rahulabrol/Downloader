package com.downloader.utils

import android.os.Environment
import timber.log.Timber
import java.io.File


/**
 * Created by Rahul Abrol on 10/9/20.
 */
object Utils {
    fun isFileExistsInLocal(it: String, appName: String): Boolean {
        val index = it.lastIndexOf("/")
        val split = it.substring(index.plus(1), it.length)
        val path = "${Environment.getExternalStorageDirectory()}${File.separator}${appName}${File.separator}image${File.separator}Media${File.separator}Images"
        val exactPath = "$path${File.separator}${split}.jpg"
        Timber.d("$index ---------> $exactPath")
        val file = File(exactPath)
        return file.exists()
    }
}