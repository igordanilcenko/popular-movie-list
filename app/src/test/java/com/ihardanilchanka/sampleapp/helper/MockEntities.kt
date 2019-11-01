package com.ihardanilchanka.sampleapp.helper

import com.ihardanilchanka.sampleapp.data.database.entity.GenreEntity
import com.ihardanilchanka.sampleapp.data.database.entity.MovieEntity
import com.ihardanilchanka.sampleapp.data.database.entity.ReviewEntity
import com.ihardanilchanka.sampleapp.data.database.entity.SimilarMovieEntity
import com.ihardanilchanka.sampleapp.data.dto.GenreDto
import com.ihardanilchanka.sampleapp.data.dto.ImageConfigDto
import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.data.dto.ReviewDto
import com.ihardanilchanka.sampleapp.data.response.ConfigurationResponse
import com.ihardanilchanka.sampleapp.data.response.GenreListResponse
import com.ihardanilchanka.sampleapp.data.response.MovieListResponse
import com.ihardanilchanka.sampleapp.data.response.ReviewListResponse
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.domain.model.Review
import okhttp3.internal.immutableListOf
import java.util.*

private val mockDate = Date()

val mockMovie = Movie(
    id = 0,
    posterUrl = "posterPath",
    overview = "overview",
    releaseDate = mockDate,
    title = "title",
    backdropUrl = "backdropPath",
    voteAverage = 10.0,
    genreNames = immutableListOf("genreName")
)
val mockReview = Review(id = "id", author = "author", content = "content")
val mockMovieDetail = MovieDetail(
    movie = mockMovie.copy(),
    reviews = immutableListOf(mockReview.copy()),
    similarMovies = immutableListOf(mockMovie.copy())
)

val mockMovieDto = MovieDto(
    id = 0,
    posterPath = "posterPath",
    overview = "overview",
    releaseDate = mockDate,
    title = "title",
    backdropPath = "backdropPath",
    voteCount = 1,
    voteAverage = 10.0,
    genreIds = immutableListOf(1)
)
val mockGenreDto = GenreDto(id = 1, name = "genreName")
val mockImageConfigDto = ImageConfigDto(baseUrl = "testUrl", secureBaseUrl = "testUrl")
val mockReviewDto = ReviewDto(id = "id", author = "author", content = "content")

val mockImageConfigResponse = ConfigurationResponse(mockImageConfigDto.copy())
val mockGenreListResponse = GenreListResponse(immutableListOf(mockGenreDto.copy()))
val mockReviewListResponse = ReviewListResponse(
    id = 1,
    reviews = immutableListOf(mockReviewDto.copy())
)
val mockMovieListResponse = MovieListResponse(
    page = 1,
    movies = immutableListOf(mockMovieDto.copy()),
    totalPages = 1,
    totalResults = 1
)

val mockGenreEntity = GenreEntity(id = 1, name = "genreName")
val mockReviewEntity = ReviewEntity(
    id = "id",
    author = "author",
    content = "content",
    sortOrder = 0,
    movieId = 0
)
val mockMovieEntity = MovieEntity(
    id = 0,
    posterPath = "posterPath",
    overview = "overview",
    releaseDate = mockDate,
    title = "title",
    backdropPath = "backdropPath",
    voteCount = 1,
    voteAverage = 10.0,
    genreIds = immutableListOf(1),
    sortOrder = 0
)
val mockSimilarMovieEntity = SimilarMovieEntity(
    id = 0,
    posterPath = "posterPath",
    overview = "overview",
    releaseDate = mockDate,
    title = "title",
    backdropPath = "backdropPath",
    voteCount = 1,
    voteAverage = 10.0,
    genreIds = immutableListOf(1),
    sortOrder = 0,
    similarTo = 0
)

fun generateMockMovieList(pages: Int): List<Movie> {
    val result = mutableListOf<Movie>()
    for (page in 0 until pages) {
        for (id in 1..10) {
            result.add(
                mockMovie.copy(id = id)
            )
        }
    }
    return result
}