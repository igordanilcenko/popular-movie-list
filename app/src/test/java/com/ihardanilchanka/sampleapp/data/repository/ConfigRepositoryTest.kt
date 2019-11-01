package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SharedPreferencesHelper
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.helper.mockImageConfigDto
import com.ihardanilchanka.sampleapp.helper.mockImageConfigResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
class ConfigRepositoryTest {

    @Mock lateinit var retrofit: RetrofitRestInterface
    @Mock lateinit var simpleCache: SimpleCache
    @Mock lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var configRepository: ConfigRepository

    @Before
    fun setUp() = runBlockingTest {
        configRepository = ConfigRepository(retrofit, simpleCache, sharedPreferencesHelper)

        whenever(simpleCache.getConfig()).thenReturn(mockImageConfigDto)
        whenever(retrofit.getConfiguration(any())).thenReturn(mockImageConfigResponse)
        whenever(sharedPreferencesHelper.getImageConfig()).thenReturn(mockImageConfigDto)
    }

    @Test
    fun `return cached data if a cache isn't empty`() = runBlocking<Unit> {
        val config = configRepository.loadConfig()

        Assert.assertEquals(config, mockImageConfigDto)

        verify(simpleCache).getConfig()
        verify(retrofit, never()).getConfiguration(any())
        verify(sharedPreferencesHelper, never()).getImageConfig()
    }

    @Test
    fun `call an API if a cache is empty, save result to cache and DB`() = runBlockingTest {
        whenever(simpleCache.getConfig()).thenReturn(null)

        val config = configRepository.loadConfig()

        Assert.assertEquals(config, mockImageConfigDto)

        verify(simpleCache).getConfig()
        verify(retrofit).getConfiguration(any())
        verify(simpleCache).saveConfig(mockImageConfigDto)
        verify(sharedPreferencesHelper).saveImageConfig(mockImageConfigDto)
        verify(sharedPreferencesHelper, never()).getImageConfig()
    }

    @Test
    fun `load data from DB if no connection and cache is empty, save result to cache`() =
        runBlockingTest {
            whenever(simpleCache.getConfig()).thenReturn(null)
            whenever(retrofit.getConfiguration(any())).thenAnswer { throw UnknownHostException() }

            val config = configRepository.loadConfig()

            Assert.assertEquals(config, mockImageConfigDto)

            verify(simpleCache).getConfig()
            verify(retrofit).getConfiguration(any())
            verify(sharedPreferencesHelper).getImageConfig()
            verify(simpleCache).saveConfig(mockImageConfigDto)
        }

    @Test(expected = UnknownHostException::class)
    fun `throw an error if no internet and no data stored in a cache neither a DB`() =
        runBlockingTest {
            whenever(simpleCache.getConfig()).thenReturn(null)
            whenever(sharedPreferencesHelper.getImageConfig()).thenReturn(null)
            whenever(retrofit.getConfiguration(any())).thenAnswer { throw UnknownHostException() }

            configRepository.loadConfig()
        }

    @Test(expected = MockitoException::class)
    fun `throw an error if no data cached and an API call fails (not a UnknownHostException)`() =
        runBlockingTest {
            whenever(simpleCache.getConfig()).thenReturn(null)
            whenever(retrofit.getConfiguration(any())).thenAnswer { throw MockitoException("test") }

            configRepository.loadConfig()
        }
}
