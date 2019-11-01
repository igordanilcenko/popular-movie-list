package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.database.dao.MovieDao
import com.ihardanilchanka.sampleapp.data.database.dao.ReviewDao
import com.ihardanilchanka.sampleapp.data.database.dao.SimilarMovieDao
import com.ihardanilchanka.sampleapp.data.dto.MovieDto
import com.ihardanilchanka.sampleapp.helper.*
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.internal.immutableListOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.exceptions.base.MockitoException
import org.mockito.junit.MockitoJUnitRunner
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    @Mock lateinit var retrofit: RetrofitRestInterface
    @Mock lateinit var similarMovieDao: SimilarMovieDao
    @Mock lateinit var movieDao: MovieDao
    @Mock lateinit var reviewDao: ReviewDao
    @Mock lateinit var simpleCache: SimpleCache

    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() = runBlocking<Unit> {
        movieRepository =
            MovieRepository(retrofit, similarMovieDao, movieDao, reviewDao, simpleCache)

        whenever(simpleCache.getPopularMovieList(any())).thenReturn(immutableListOf(mockMovieDto))
        whenever(retrofit.getPopularMovieList(any(), any())).thenReturn(mockMovieListResponse)
        whenever(movieDao.getAll()).thenReturn(immutableListOf(mockMovieEntity))

        whenever(simpleCache.getReviewList(any())).thenReturn(immutableListOf(mockReviewDto))
        whenever(retrofit.getMovieReviews(any(), any())).thenReturn(mockReviewListResponse)
        whenever(reviewDao.getAll(any())).thenReturn(immutableListOf(mockReviewEntity))

        whenever(simpleCache.getSimilarMovieList(any())).thenReturn(immutableListOf(mockMovieDto))
        whenever(retrofit.getSimilarMovieList(any(), any())).thenReturn(mockMovieListResponse)
        whenever(similarMovieDao.getAll(any())).thenReturn(immutableListOf(mockSimilarMovieEntity))
    }

    @Test
    fun `Movie list - return cached data if a cache isn't empty`() = runBlockingTest {
        val popularMovies = movieRepository.loadPopularMovieList(1)

        Assert.assertEquals(popularMovies, immutableListOf(mockMovieDto))

        verify(simpleCache).getPopularMovieList(1)
        verify(retrofit, never()).getPopularMovieList(any(), any())
        verify(movieDao, never()).insertAll(any())
        verify(movieDao, never()).deleteAllItems()
        verify(movieDao, never()).getAll()
        verify(simpleCache, never()).putPopularMovieList(any(), any())
        verify(simpleCache, never()).clearPopularMovieList()
    }

    @Test
    fun `Movie list - refresh cached data`() = runBlockingTest {
        val popularMovies = movieRepository.loadPopularMovieList(page = 1, refreshCache = true)

        Assert.assertEquals(popularMovies, immutableListOf(mockMovieDto))

        verify(simpleCache).getPopularMovieList(1)
        verify(retrofit).getPopularMovieList(any(), eq(1))
        verify(simpleCache).clearPopularMovieList()
        verify(movieDao, never()).getAll()
        verify(simpleCache).putPopularMovieList(any(), any())
    }

    @Test
    fun `Movie list - call an API if a cache is empty, refresh data in cache and DB`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)

            val popularMovies = movieRepository.loadPopularMovieList(1)

            Assert.assertEquals(popularMovies, immutableListOf(mockMovieDto))

            verify(simpleCache).getPopularMovieList(1)
            verify(retrofit).getPopularMovieList(any(), eq(1))
            verify(movieDao).deleteAllItems()
            verify(movieDao).insertAll(*immutableListOf(mockMovieEntity).toTypedArray())
            verify(movieDao, never()).getAll()
            verify(simpleCache).putPopularMovieList(1, immutableListOf(mockMovieDto))
            verify(simpleCache, never()).clearPopularMovieList()
        }

    @Test
    fun `Movie list - call an API if a cache is empty, but don't save to DB (not 1st page)`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)

            val popularMovies = movieRepository.loadPopularMovieList(2)

            Assert.assertEquals(popularMovies, immutableListOf(mockMovieDto))

            verify(simpleCache).getPopularMovieList(2)
            verify(retrofit).getPopularMovieList(any(), eq(2))
            verify(movieDao, never()).deleteAllItems()
            verify(movieDao, never()).insertAll()
            verify(movieDao, never()).getAll()
            verify(simpleCache).putPopularMovieList(2, immutableListOf(mockMovieDto))
            verify(simpleCache, never()).clearPopularMovieList()
        }

    @Test
    fun `Movie list - load data from DB if no connection and no cache, save result to cache`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)
            whenever(retrofit.getPopularMovieList(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            val popularMovies = movieRepository.loadPopularMovieList(1)

            Assert.assertEquals(popularMovies, immutableListOf(mockMovieDto))

            verify(simpleCache).getPopularMovieList(1)
            verify(retrofit).getPopularMovieList(any(), eq(1))
            verify(movieDao).getAll()
            verify(movieDao, never()).deleteAllItems()
            verify(movieDao, never()).insertAll(any())
            verify(simpleCache).putPopularMovieList(1, immutableListOf(mockMovieDto))
            verify(simpleCache, never()).clearPopularMovieList()
        }

    @Test(expected = UnknownHostException::class)
    fun `Movie list - throw error if no connection (not 1st page)`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)
            whenever(retrofit.getPopularMovieList(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            movieRepository.loadPopularMovieList(2)
        }

    @Test(expected = UnknownHostException::class)
    fun `Movie list - throw error if no internet and no data stored in a cache neither a DB`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)
            whenever(movieDao.getAll()).thenReturn(immutableListOf())
            whenever(retrofit.getPopularMovieList(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            movieRepository.loadPopularMovieList(1, false)
        }

    @Test(expected = MockitoException::class)
    fun `Movie list - throw error if no cache and API call fails (not UnknownHostException)`() =
        runBlockingTest {
            whenever(simpleCache.getPopularMovieList(any())).thenReturn(null)
            whenever(retrofit.getPopularMovieList(any(), any()))
                .thenAnswer { throw MockitoException("test") }

            movieRepository.loadPopularMovieList(1, false)
        }

    @Test
    fun `Reviews - return cached data if a cache isn't empty`() = runBlockingTest {
        val reviews = movieRepository.loadReviewList(0)

        Assert.assertEquals(reviews, immutableListOf(mockReviewDto))

        verify(simpleCache).getReviewList(0)
        verify(retrofit, never()).getMovieReviews(any(), any())
        verify(reviewDao, never()).getAll(any())
        verify(reviewDao, never()).deleteAll(any())
        verify(reviewDao, never()).insertAll(any())
    }

    @Test
    fun `Reviews - call an API if a cache is empty, refresh data in cache and DB`() =
        runBlockingTest {
            whenever(simpleCache.getReviewList(any())).thenReturn(null)

            val reviews = movieRepository.loadReviewList(0)

            Assert.assertEquals(reviews, immutableListOf(mockReviewDto))

            verify(simpleCache).getReviewList(0)
            verify(reviewDao).getAll(0)
            verify(retrofit).getMovieReviews(eq(0), any())
            verify(simpleCache).putReviewList(0, immutableListOf(mockReviewDto))
            verify(reviewDao)
                .deleteAll(*immutableListOf(mockReviewEntity).toTypedArray())
            verify(reviewDao)
                .insertAll(*immutableListOf(mockReviewEntity).toTypedArray())
        }

    @Test
    fun `Reviews - load data from DB if no connection and no cache, cache result`() =
        runBlockingTest {
            whenever(simpleCache.getReviewList(any())).thenReturn(null)
            whenever(retrofit.getMovieReviews(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            val reviews = movieRepository.loadReviewList(0)

            Assert.assertEquals(reviews, immutableListOf(mockReviewDto))

            verify(simpleCache).getReviewList(0)
            verify(retrofit).getMovieReviews(eq(0), any())
            verify(reviewDao).getAll(0)
            verify(simpleCache).putReviewList(0, immutableListOf(mockReviewDto))
            verify(reviewDao, never()).deleteAll(any())
            verify(reviewDao, never()).insertAll(any())
        }

    @Test
    fun `Reviews - return empty list if no internet and no data stored`() =
        runBlockingTest {
            whenever(simpleCache.getReviewList(any())).thenReturn(null)
            whenever(reviewDao.getAll(any())).thenReturn(immutableListOf())
            whenever(retrofit.getMovieReviews(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            val reviews = movieRepository.loadReviewList(0)

            Assert.assertEquals(reviews, immutableListOf<MovieDto>())

            verify(simpleCache).getReviewList(0)
            verify(reviewDao).getAll(0)
            verify(retrofit).getMovieReviews(eq(0), any())
            verify(simpleCache).putReviewList(0, immutableListOf())
            verify(reviewDao, never()).deleteAll(any())
            verify(reviewDao, never()).insertAll(any())
        }

    @Test
    fun `Similar movies - return cached data if a cache isn't empty`() = runBlockingTest {
        val similarMovies = movieRepository.loadSimilarMovieList(0)

        Assert.assertEquals(similarMovies, immutableListOf(mockMovieDto))

        verify(simpleCache).getSimilarMovieList(0)
        verify(retrofit, never()).getSimilarMovieList(any(), any())
        verify(similarMovieDao, never()).getAll(any())
        verify(similarMovieDao, never()).deleteAll(any())
        verify(similarMovieDao, never()).insertAll(any())
    }

    @Test
    fun `Similar movies - call an API if a cache is empty, refresh data in cache and DB`() =
        runBlockingTest {
            whenever(simpleCache.getSimilarMovieList(any())).thenReturn(null)

            val similarMovies = movieRepository.loadSimilarMovieList(0)

            Assert.assertEquals(similarMovies, immutableListOf(mockMovieDto))

            verify(simpleCache).getSimilarMovieList(0)
            verify(similarMovieDao).getAll(0)
            verify(retrofit).getSimilarMovieList(eq(0), any())
            verify(simpleCache).putSimilarMovieList(0, immutableListOf(mockMovieDto))
            verify(similarMovieDao)
                .deleteAll(*immutableListOf(mockSimilarMovieEntity).toTypedArray())
            verify(similarMovieDao)
                .insertAll(*immutableListOf(mockSimilarMovieEntity).toTypedArray())
        }

    @Test
    fun `Similar movies - load data from DB if no connection and no cache, save result to cache`() =
        runBlockingTest {
            whenever(simpleCache.getSimilarMovieList(any())).thenReturn(null)
            whenever(retrofit.getSimilarMovieList(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            val similarMovies = movieRepository.loadSimilarMovieList(0)

            Assert.assertEquals(similarMovies, immutableListOf(mockMovieDto))

            verify(simpleCache).getSimilarMovieList(0)
            verify(retrofit).getSimilarMovieList(eq(0), any())
            verify(similarMovieDao).getAll(0)
            verify(simpleCache).putSimilarMovieList(0, immutableListOf(mockMovieDto))
            verify(similarMovieDao, never()).deleteAll(any())
            verify(similarMovieDao, never()).insertAll(any())
        }

    @Test
    fun `Similar movies - return empty list if no internet and no data stored`() =
        runBlockingTest {
            whenever(simpleCache.getSimilarMovieList(any())).thenReturn(null)
            whenever(similarMovieDao.getAll(any())).thenReturn(immutableListOf())
            whenever(retrofit.getSimilarMovieList(any(), any()))
                .thenAnswer { throw UnknownHostException() }

            val similarMovies = movieRepository.loadSimilarMovieList(0)

            Assert.assertEquals(similarMovies, immutableListOf<MovieDto>())

            verify(simpleCache).getSimilarMovieList(0)
            verify(similarMovieDao).getAll(0)
            verify(retrofit).getSimilarMovieList(eq(0), any())
            verify(simpleCache).putSimilarMovieList(0, immutableListOf())
            verify(similarMovieDao, never()).deleteAll(any())
            verify(similarMovieDao, never()).insertAll(any())
        }
}