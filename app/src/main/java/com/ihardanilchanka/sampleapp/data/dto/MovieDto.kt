package com.ihardanilchanka.sampleapp.data.dto

import com.squareup.moshi.Json
import java.util.*

data class MovieDto(
    val id: Int,
    @field:Json(name = "poster_path")
    val posterPath: String?,
    val overview: String,
    @field:Json(name = "release_date")
    val releaseDate: Date,
    val title: String,
    @field:Json(name = "backdrop_path")
    val backdropPath: String?,
    @field:Json(name = "vote_count")
    val voteCount: Int,
    @field:Json(name = "vote_average")
    val voteAverage: Double,
    @field:Json(name = "genre_ids")
    val genreIds: List<Int>
)