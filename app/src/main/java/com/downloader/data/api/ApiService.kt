package com.downloader.data.api

import com.downloader.data.model.Example
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by Rahul Abrol on 4/9/20.
 */
interface ApiService {
    @GET("v2/list/")
    suspend fun getImage(): Response<List<Example>>
}