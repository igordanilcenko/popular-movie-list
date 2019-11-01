package com.ihardanilchanka.sampleapp.domain

import com.ihardanilchanka.sampleapp.data.dto.GenreDto
import com.ihardanilchanka.sampleapp.data.repository.ConfigRepository
import com.ihardanilchanka.sampleapp.data.repository.GenreRepository
import com.ihardanilchanka.sampleapp.data.repository.MovieRepository
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.domain.util.toModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MovieInteractor(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    private val configRepository: ConfigRepository
) {

    suspend fun loadMovieList(page: Int, refreshCache: Boolean = false): List<Movie> =
        coroutineScope {
            val movieList = async { movieRepository.loadPopularMovieList(page, refreshCache) }
            val baseImageUrl = async { configRepository.loadConfig().secureBaseUrl }
            val genres = async { genreRepository.loadGenreList() }

            movieList.await().map {
                it.toModel(
                    baseImageUrl.await(),
                    mapGenresWithNames(it.genreIds, genres.await())
                )
            }
        }

    suspend fun loadMovieDetail(movie: Movie): MovieDetail = coroutineScope {
        val baseImageUrl = async { configRepository.loadConfig().secureBaseUrl }
        val reviews = async { movieRepository.loadReviewList(movie.id).map { it.toModel() } }
        val genres = async { genreRepository.loadGenreList() }
        val similarMovies = async { movieRepository.loadSimilarMovieList(movie.id) }

        MovieDetail(
            movie,
            reviews.await(),
            similarMovies.await().map {
                it.toModel(
                    baseImageUrl.await(),
                    mapGenresWithNames(it.genreIds, genres.await())
                )
            })
    }

    private fun mapGenresWithNames(genreIds: List<Int>, genres: List<GenreDto>): List<String> {
        val result = mutableListOf<String>()
        for (id in genreIds) {
            val genre = genres.find { it.id == id }
            if (genre != null) {
                result.add(genre.name)
            }
        }
        return result
    }
}