package com.ihardanilchanka.sampleapp.data.response

import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.squareup.moshi.Json

data class MovieListResponse(
    @field:Json(name = "page") val page: Int,
    @field:Json(name = "results") val movies: List<MovieDto>,
    @field:Json(name = "total_results") val totalResults: Int,
    @field:Json(name = "total_pages") val totalPages: Int
)