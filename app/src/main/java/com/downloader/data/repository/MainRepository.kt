package com.downloader.data.repository

import com.downloader.data.api.ApiHelper
import javax.inject.Inject

/**
 * Created by Rahul Abrol on 4/9/20.
 */
class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun getImage() = apiHelper.getImage()
}