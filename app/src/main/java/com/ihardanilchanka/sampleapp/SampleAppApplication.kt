package com.ihardanilchanka.sampleapp

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SampleAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SampleAppApplication)
            modules(SampleAppKoinModule.init())
        }

        Coil.setDefaultImageLoader {
            ImageLoader(this@SampleAppApplication) {
                crossfade(true)
                okHttpClient {
                    OkHttpClient.Builder()
                        .cache(CoilUtils.createDefaultCache(this@SampleAppApplication))
                        .build()
                }
            }
        }
    }
}