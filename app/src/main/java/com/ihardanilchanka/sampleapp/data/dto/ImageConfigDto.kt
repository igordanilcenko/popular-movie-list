package com.ihardanilchanka.sampleapp.data.dto

import com.squareup.moshi.Json

data class ImageConfigDto(
    @field:Json(name = "base_url")
    val baseUrl: String,
    @field:Json(name = "secure_base_url")
    val secureBaseUrl: String
)