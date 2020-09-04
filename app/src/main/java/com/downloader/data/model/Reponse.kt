package com.downloader.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Rahul Abrol on 4/9/20.
 */
data class Example(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("author")
        var author: String? = null,
        @SerializedName("width")
        var width: Int? = null,
        @SerializedName("height")
        var height: Int? = null,
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("download_url")
        var downloadUrl: String? = null
)