package com.example.photogallery.model

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: Long = 0L,
    @SerializedName("url_s") var url: String = ""
)