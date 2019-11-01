package com.ihardanilchanka.sampleapp.data

import com.ihardanilchanka.sampleapp.data.dto.GenreDto
import com.ihardanilchanka.sampleapp.data.dto.ImageConfigDto
import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.data.dto.ReviewDto

/**
 * Simple in-memory cache needed for store data while the app is running.
 */
class SimpleCache {

    private var imageConfig: ImageConfigDto? = null
    private var genres: List<GenreDto>? = null
    private var popularMoviesMap = mutableMapOf<Int, List<MovieDto>>()
    private val similarMoviesMap = mutableMapOf<Int, List<MovieDto>>()
    private val reviewsMap = mutableMapOf<Int, List<ReviewDto>>()

    fun saveConfig(imageConfigDto: ImageConfigDto) {
        this.imageConfig = imageConfigDto
    }

    fun getConfig(): ImageConfigDto? = imageConfig

    fun saveGenres(genres: List<GenreDto>) {
        this.genres = genres
    }

    fun getGenres(): List<GenreDto>? = genres

    fun putPopularMovieList(page: Int, movies: List<MovieDto>) {
        this.popularMoviesMap[page] = movies
    }

    fun getPopularMovieList(page: Int): List<MovieDto>? = popularMoviesMap[page]

    fun clearPopularMovieList() {
        popularMoviesMap.clear()
    }

    fun putSimilarMovieList(movieId: Int, similarMovies: List<MovieDto>) {
        this.similarMoviesMap[movieId] = similarMovies
    }

    fun getSimilarMovieList(movieId: Int): List<MovieDto>? = similarMoviesMap[movieId]

    fun putReviewList(movieId: Int, reviews: List<ReviewDto>) {
        this.reviewsMap[movieId] = reviews
    }

    fun getReviewList(movieId: Int): List<ReviewDto>? = reviewsMap[movieId]
}