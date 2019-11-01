package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.database.dao.GenreDao
import com.ihardanilchanka.sampleapp.helper.mockGenreDto
import com.ihardanilchanka.sampleapp.helper.mockGenreEntity
import com.ihardanilchanka.sampleapp.helper.mockGenreListResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
class GenreRepositoryTest {

    @Mock lateinit var retrofit: RetrofitRestInterface
    @Mock lateinit var genreDao: GenreDao
    @Mock lateinit var simpleCache: SimpleCache

    private lateinit var genreRepository: GenreRepository

    @Before
    fun setUp() = runBlocking<Unit> {
        genreRepository = GenreRepository(retrofit, genreDao, simpleCache)

        whenever(simpleCache.getGenres()).thenReturn(immutableListOf(mockGenreDto))
        whenever(retrofit.getGenreList(any())).thenReturn(mockGenreListResponse)
        whenever(genreDao.getAll()).thenReturn(immutableListOf(mockGenreEntity))
    }

    @Test
    fun `return cached data if a cache isn't empty`() = runBlockingTest {
        val genres = genreRepository.loadGenreList()

        Assert.assertEquals(genres, immutableListOf(mockGenreDto))

        verify(simpleCache).getGenres()
        verify(retrofit, never()).getGenreList(any())
        verify(simpleCache, never()).saveGenres(any())
        verify(genreDao, never()).deleteAllItems()
        verify(genreDao, never()).insertAll(any())
        verify(genreDao, never()).getAll()
    }

    @Test
    fun `call an API if a cache is empty, save result to cache and DB`() = runBlockingTest {
        whenever(simpleCache.getGenres()).thenReturn(null)

        val genres = genreRepository.loadGenreList()

        Assert.assertEquals(genres, immutableListOf(mockGenreDto))

        verify(simpleCache).getGenres()
        verify(retrofit).getGenreList(any())
        verify(simpleCache).saveGenres(any())
        verify(genreDao).deleteAllItems()
        verify(genreDao).insertAll(*immutableListOf(mockGenreEntity).toTypedArray())
        verify(genreDao, never()).getAll()
    }

    @Test
    fun `load data from DB if no connection and cache is empty, refresh cache`() =
        runBlockingTest {
            whenever(simpleCache.getGenres()).thenReturn(null)
            whenever(retrofit.getGenreList(any())).thenAnswer { throw UnknownHostException() }

            val genres = genreRepository.loadGenreList()

            Assert.assertEquals(genres, immutableListOf(mockGenreDto))

            verify(simpleCache).getGenres()
            verify(retrofit).getGenreList(any())
            verify(genreDao).getAll()
            verify(genreDao, never()).deleteAllItems()
            verify(simpleCache).saveGenres(immutableListOf(mockGenreDto))
            verify(genreDao, never()).insertAll(any())
        }

    @Test(expected = UnknownHostException::class)
    fun `throw an error if no internet and no data stored in a cache neither a DB`() =
        runBlockingTest {
            whenever(simpleCache.getGenres()).thenReturn(null)
            whenever(genreDao.getAll()).thenReturn(immutableListOf())
            whenever(retrofit.getGenreList(any())).thenAnswer { throw UnknownHostException() }

            genreRepository.loadGenreList()
        }

    @Test(expected = MockitoException::class)
    fun `throw an error if no data cached and an API call fails (not a UnknownHostException)`() =
        runBlockingTest {
            whenever(simpleCache.getGenres()).thenReturn(null)
            whenever(retrofit.getGenreList(any())).thenAnswer { throw MockitoException("test") }

            genreRepository.loadGenreList()
        }
}
