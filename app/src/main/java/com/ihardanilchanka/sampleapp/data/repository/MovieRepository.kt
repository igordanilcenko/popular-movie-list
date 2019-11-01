package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.ApiConfig
import com.ihardanilchanka.sampleapp.BuildConfig
import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.database.dao.MovieDao
import com.ihardanilchanka.sampleapp.data.database.dao.ReviewDao
import com.ihardanilchanka.sampleapp.data.database.dao.SimilarMovieDao
import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.data.dto.ReviewDto
import com.ihardanilchanka.sampleapp.data.util.toDto
import com.ihardanilchanka.sampleapp.data.util.toEntity
import com.ihardanilchanka.sampleapp.data.util.toSimilarMovieEntity
import kotlinx.coroutines.delay
import java.net.UnknownHostException

class MovieRepository(
    private val retrofit: RetrofitRestInterface,
    private val similarMovieDao: SimilarMovieDao,
    private val movieDao: MovieDao,
    private val reviewDao: ReviewDao,
    private val simpleCache: SimpleCache
) {

    suspend fun loadPopularMovieList(page: Int, refreshCache: Boolean = false): List<MovieDto> {
        // if cached data available, but it doesn't need to refresh, return them immediately
        val cached = simpleCache.getPopularMovieList(page)
        if (!refreshCache && cached != null) {
            return cached
        }

        // it adds delay for show how the app handle "heavy" async loadings.
        // You can disable it in gradle config
        if (BuildConfig.USE_LOADING_DELAY) {
            delay(3000L)
        }

        var movies: List<MovieDto>
        try {
            movies = retrofit.getPopularMovieList(ApiConfig.API_KEY, page).movies
            // clear cache before saving new data if it needs to refresh cache
            if (refreshCache) {
                simpleCache.clearPopularMovieList()
            }
            // save movies only for 1st page. The app is online-first, so offline work has
            // retained only part of online functionality
            if (page == 1) {
                movieDao.deleteAllItems()
                movieDao.insertAll(*movies.mapIndexed { index, movie -> movie.toEntity(index) }
                    .toTypedArray())
            }
        } catch (e: UnknownHostException) {
            // on no internet try to return data saved in database, escalate error else
            val stored = movieDao.getAll()
            if (!stored.isNullOrEmpty() && page == 1) {
                movies = stored.map { it.toDto() }
            } else {
                throw e
            }
        }

        // save new data to cache
        simpleCache.putPopularMovieList(page, movies)
        return movies
    }

    suspend fun loadReviewList(movieId: Int): List<ReviewDto> {
        // if cached data available, return them immediately
        val cached = simpleCache.getReviewList(movieId)
        if (cached != null) {
            return cached
        }

        // it adds delay for show how the app handle "heavy" async loadings.
        // You can disable it in gradle config
        if (BuildConfig.USE_LOADING_DELAY) {
            delay(3000L)
        }

        var reviews: List<ReviewDto>
        val stored = reviewDao.getAll(movieId)
        try {
            reviews = retrofit.getMovieReviews(movieId, ApiConfig.API_KEY).reviews
            // delete all reviews related to that movie before inserting new ones
            reviewDao.deleteAll(*stored.toTypedArray())
            reviewDao.insertAll(
                *reviews.mapIndexed { index, review -> review.toEntity(movieId, index) }
                    .toTypedArray()
            )
        } catch (e: Exception) {
            // if any exception happens, return stored list of similar movies, even if it is empty.
            // Error happened while reviews are loading must not fail movie detail loading.
            reviews = stored.map { it.toDto() }
        }

        // save new data to cache, but only successfully loaded ones
        if (reviews.isNotEmpty()) {
            simpleCache.putReviewList(movieId, reviews)
        }
        return reviews
    }

    suspend fun loadSimilarMovieList(movieId: Int): List<MovieDto> {
        // if cached data available, return them immediately
        val cached = simpleCache.getSimilarMovieList(movieId)
        if (cached != null) {
            return cached
        }

        // it adds delay for show how the app handle "heavy" async loadings.
        // You can disable it in gradle config
        if (BuildConfig.USE_LOADING_DELAY) {
            delay(3000L)
        }

        var similarMovies: List<MovieDto>
        val stored = similarMovieDao.getAll(movieId)
        try {
            similarMovies = retrofit.getSimilarMovieList(movieId, ApiConfig.API_KEY).movies
            // delete all similar movies related to that movie before inserting new ones
            similarMovieDao.deleteAll(*stored.toTypedArray())
            similarMovieDao.insertAll(
                *similarMovies.mapIndexed { index, movie ->
                    movie.toSimilarMovieEntity(movieId, index)
                }.toTypedArray()
            )
        } catch (e: Exception) {
            // if any exception happens, return stored list of similar movies, even if it is empty.
            // Error happened while similar movies loading must not fail movie detail loading.
            similarMovies = stored.map { it.toDto() }
        }

        // save new data to cache, but only successfully loaded ones
        if (similarMovies.isNotEmpty()) {
            simpleCache.putSimilarMovieList(movieId, similarMovies)
        }
        return similarMovies
    }
}
