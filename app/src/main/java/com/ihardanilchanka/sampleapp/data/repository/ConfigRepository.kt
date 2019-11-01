package com.ihardanilchanka.sampleapp.data.repository

import com.ihardanilchanka.sampleapp.ApiConfig
import com.ihardanilchanka.sampleapp.BuildConfig
import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SharedPreferencesHelper
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.dto.ImageConfigDto
import kotlinx.coroutines.delay
import java.net.UnknownHostException

class ConfigRepository(
    private val retrofit: RetrofitRestInterface,
    private val simpleCache: SimpleCache,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    suspend fun loadConfig(): ImageConfigDto {
        // if cached data available, return them immediately
        val cached = simpleCache.getConfig()
        if (cached != null) {
            return cached
        }

        // it adds a delay for show how the app handle "heavy" async loadings.
        // You can disable it in the gradle config
        if (BuildConfig.USE_LOADING_DELAY) {
            delay(3000L)
        }

        var imageConfigDto: ImageConfigDto
        try {
            imageConfigDto = retrofit.getConfiguration(ApiConfig.API_KEY).imageConfigDto

            // save to shared preferences
            sharedPreferencesHelper.saveImageConfig(imageConfigDto)
        } catch (e: UnknownHostException) {
            // on no internet try to return data saved in database, escalate error otherwise
            val stored = sharedPreferencesHelper.getImageConfig()
            if (stored != null) {
                imageConfigDto = stored
            } else {
                throw e
            }
        }

        // save loaded data to cache
        simpleCache.saveConfig(imageConfigDto)

        return imageConfigDto
    }
}