package com.ihardanilchanka.sampleapp.domain

import com.ihardanilchanka.sampleapp.data.repository.ConfigRepository
import com.ihardanilchanka.sampleapp.data.repository.GenreRepository
import com.ihardanilchanka.sampleapp.data.repository.MovieRepository
import com.ihardanilchanka.sampleapp.helper.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import okhttp3.internal.immutableListOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieInteractorTest {

    @Mock lateinit var movieRepository: MovieRepository
    @Mock lateinit var genreRepository: GenreRepository
    @Mock lateinit var configRepository: ConfigRepository

    private lateinit var movieInteractor: MovieInteractor

    @Before
    fun setUp() = runBlocking<Unit> {
        movieInteractor = MovieInteractor(movieRepository, genreRepository, configRepository)

        whenever(movieRepository.loadPopularMovieList(any(), any()))
            .thenReturn(immutableListOf(mockMovieDto))
        whenever(movieRepository.loadSimilarMovieList(any()))
            .thenReturn(immutableListOf(mockMovieDto))
        whenever(movieRepository.loadReviewList(any()))
            .thenReturn(immutableListOf(mockReviewDto))
        whenever(configRepository.loadConfig()).thenReturn(mockImageConfigDto)
        whenever(genreRepository.loadGenreList()).thenReturn(immutableListOf(mockGenreDto))
    }

    @Test
    fun `Load popular movie list - return valid response on all data available`() = runBlocking {
        val list = movieInteractor.loadMovieList(1)

        assert(list.size == 1)
    }

    @Test
    fun `Load movie detail - return valid response on all data available`() = runBlocking {
        val detail = movieInteractor.loadMovieDetail(mockMovie)

        assert(detail.similarMovies.size == 1)
        assert(detail.movie == mockMovie)
        assert(detail.reviews.size == 1)
    }

    @Test
    fun `Load movie detail - return correct value on similar movie list is empty`() = runBlocking {
        whenever(movieRepository.loadSimilarMovieList(any()))
            .thenReturn(immutableListOf())

        val detail = movieInteractor.loadMovieDetail(mockMovie)

        assert(detail.similarMovies.isEmpty())
        assert(detail.movie == mockMovie)
        assert(detail.reviews.size == 1)
    }

    @Test
    fun `Load movie detail - return correct value on review list is empty`() = runBlocking {
        whenever(movieRepository.loadReviewList(any())).thenReturn(immutableListOf())

        val detail = movieInteractor.loadMovieDetail(mockMovie)

        assert(detail.similarMovies.size == 1)
        assert(detail.movie == mockMovie)
        assert(detail.reviews.isEmpty())
    }
}