package com.ihardanilchanka.sampleapp.data.util

import com.ihardanilchanka.sampleapp.data.database.entity.GenreEntity
import com.ihardanilchanka.sampleapp.data.database.entity.MovieEntity
import com.ihardanilchanka.sampleapp.data.database.entity.ReviewEntity
import com.ihardanilchanka.sampleapp.data.database.entity.SimilarMovieEntity
import com.ihardanilchanka.sampleapp.data.dto.GenreDto
import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.data.dto.ReviewDto

fun GenreDto.toEntity(): GenreEntity {
    return GenreEntity(id = id, name = name)
}

fun GenreEntity.toDto(): GenreDto {
    return GenreDto(id = id, name = name)
}

fun MovieDto.toSimilarMovieEntity(movieId: Int, order: Int): SimilarMovieEntity {
    return SimilarMovieEntity(
        similarTo = movieId,
        id = id,
        backdropPath = backdropPath,
        genreIds = genreIds,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage,
        voteCount = voteCount,
        sortOrder = order
    )
}

fun SimilarMovieEntity.toDto(): MovieDto {
    return MovieDto(
        id = id,
        backdropPath = backdropPath,
        genreIds = genreIds,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun ReviewDto.toEntity(movieId: Int, index: Int): ReviewEntity {
    return ReviewEntity(
        movieId = movieId,
        id = id,
        author = author,
        content = content,
        sortOrder = index
    )
}

fun ReviewEntity.toDto(): ReviewDto {
    return ReviewDto(
        id = id,
        author = author,
        content = content
    )
}

fun MovieDto.toEntity(order: Int): MovieEntity {
    return MovieEntity(
        id = id,
        backdropPath = backdropPath,
        genreIds = genreIds,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage,
        voteCount = voteCount,
        sortOrder = order
    )
}

fun MovieEntity.toDto(): MovieDto {
    return MovieDto(
        id = id,
        backdropPath = backdropPath,
        genreIds = genreIds,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}
