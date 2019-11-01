package com.ihardanilchanka.sampleapp.data.response

import com.ihardanilchanka.sampleapp.data.dto.ImageConfigDto
import com.squareup.moshi.Json

data class ConfigurationResponse(
    @field:Json(name = "images") val imageConfigDto: ImageConfigDto
)