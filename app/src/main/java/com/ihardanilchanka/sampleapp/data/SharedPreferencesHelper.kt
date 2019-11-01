package com.ihardanilchanka.sampleapp.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ihardanilchanka.sampleapp.data.dto.ImageConfigDto
import com.squareup.moshi.Moshi

class SharedPreferencesHelper(
    private val sharedPreferences: SharedPreferences,
    private val moshi: Moshi
) {

    fun saveImageConfig(config: ImageConfigDto) {
        sharedPreferences.edit {
            putString(
                KEY_CONFIG,
                moshi.adapter<ImageConfigDto>(ImageConfigDto::class.java).toJson(config)
            )
        }
    }

    fun getImageConfig(): ImageConfigDto? {
        val stored = sharedPreferences.getString(KEY_CONFIG, null)
        return if (stored != null) {
            moshi.adapter<ImageConfigDto>(ImageConfigDto::class.java).fromJson(stored)
        } else {
            null
        }
    }

    companion object {
        const val KEY_CONFIG = "KEY_CONFIG"
    }
}