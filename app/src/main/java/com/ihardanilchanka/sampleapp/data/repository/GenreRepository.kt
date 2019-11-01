package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.ApiConfig
import com.ihardanilchanka.sampleapp.BuildConfig
import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.database.dao.GenreDao
import com.ihardanilchanka.sampleapp.data.dto.GenreDto
import com.ihardanilchanka.sampleapp.data.util.toDto
import com.ihardanilchanka.sampleapp.data.util.toEntity
import kotlinx.coroutines.delay
import java.net.UnknownHostException

class GenreRepository(
    private val retrofit: RetrofitRestInterface,
    private val genreDao: GenreDao,
    private val simpleCache: SimpleCache
) {

    suspend fun loadGenreList(): List<GenreDto> {
        // if cached data available, return them immediately
        val cached = simpleCache.getGenres()
        if (cached != null) {
            return cached
        }

        // it adds delay for show how the app handle "heavy" async loadings.
        // You can disable it in gradle config
        if (BuildConfig.USE_LOADING_DELAY) {
            delay(3000L)
        }

        var genres: List<GenreDto>
        try {
            // try to load data from server
            genres = retrofit.getGenreList(ApiConfig.API_KEY).genres
            // refresh data in database
            genreDao.deleteAllItems()
            genreDao.insertAll(*genres.map { it.toEntity() }.toTypedArray())
        } catch (e: UnknownHostException) {
            // on no internet try to return data saved in database, escalate error else
            val stored = genreDao.getAll()
            if (stored.isNullOrEmpty()) {
                throw e
            } else {
                genres = stored.map { it.toDto() }
            }
        }

        // put loaded data to cache
        simpleCache.saveGenres(genres)
        return genres
    }
}
