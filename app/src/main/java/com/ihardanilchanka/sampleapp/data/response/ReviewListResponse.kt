package com.ihardanilchanka.sampleapp.data.response

import com.ihardanilchanka.sampleapp.data.dto.ReviewDto
import com.squareup.moshi.Json

data class ReviewListResponse(
    val id: Int,
    @field:Json(name = "results") val reviews: List<ReviewDto>
)