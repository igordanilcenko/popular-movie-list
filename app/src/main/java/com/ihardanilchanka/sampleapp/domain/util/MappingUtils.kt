package com.ihardanilchanka.sampleapp.domain.util

import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.data.dto.ReviewDto
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.Review

fun MovieDto.toModel(
    imageBaseUrl: String,
    genres: List<String>
): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        posterUrl = if (posterPath != null)
            getImageUrl(imageBaseUrl, posterPath) else null,
        backdropUrl = if (backdropPath != null)
            getImageUrl(imageBaseUrl, backdropPath) else null,
        genreNames = genres
    )
}

fun ReviewDto.toModel(): Review {
    return Review(
        id = id,
        author = author,
        content = content
    )
}
