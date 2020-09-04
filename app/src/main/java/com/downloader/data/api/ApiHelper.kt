package com.downloader.data.api

import javax.inject.Inject

/**
 * Created by Rahul Abrol on 4/9/20.
 */
class ApiHelper @Inject constructor(private val apiService: ApiService) {

    suspend fun getImage() = apiService.getImage()
}