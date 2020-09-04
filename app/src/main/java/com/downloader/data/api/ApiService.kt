package com.downloader.data.api

import com.downloader.data.model.Example
import retrofit2.http.GET

/**
 * Created by Rahul Abrol on 4/9/20.
 */
interface ApiService {
    @GET("200/300?random=20")
    suspend fun getImage(): List<Example>
}