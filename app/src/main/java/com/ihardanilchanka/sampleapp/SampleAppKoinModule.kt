package com.ihardanilchanka.sampleapp

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.ihardanilchanka.sampleapp.data.RetrofitRestInterface
import com.ihardanilchanka.sampleapp.data.SharedPreferencesHelper
import com.ihardanilchanka.sampleapp.data.SimpleCache
import com.ihardanilchanka.sampleapp.data.database.AppDatabase
import com.ihardanilchanka.sampleapp.data.repository.ConfigRepository
import com.ihardanilchanka.sampleapp.data.repository.GenreRepository
import com.ihardanilchanka.sampleapp.data.repository.MovieRepository
import com.ihardanilchanka.sampleapp.domain.MovieInteractor
import com.ihardanilchanka.sampleapp.presentation.moviedetail.MovieDetailViewModel
import com.ihardanilchanka.sampleapp.presentation.movielist.MovieListViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

object SampleAppKoinModule {
    fun init() = module {
        single { MovieRepository(get(), get(), get(), get(), get()) }
        single { GenreRepository(get(), get(), get()) }
        single { ConfigRepository(get(), get(), get()) }
        single { SimpleCache() }
        single { SharedPreferencesHelper(get(), get()) }

        viewModel { MovieListViewModel(get()) }
        viewModel { MovieDetailViewModel(get()) }

        factory { MovieInteractor(get(), get(), get()) }

        single { get<AppDatabase>().genreDao() }
        single { get<AppDatabase>().movieDao() }
        single { get<AppDatabase>().similarMovieDao() }
        single { get<AppDatabase>().reviewDao() }

        single { provideDatabase(get()) }

        single { provideMoshi() }
        single { provideRetrofit(get()) }
        single { provideSharedPreferences(get()) }
    }

    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()
    }

    private fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()
    }

    private fun provideRetrofit(moshi: Moshi): RetrofitRestInterface {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.LOGS) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(ApiConfig.ENDPOINT)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient.build())
            .build()
            .create(RetrofitRestInterface::class.java)
    }

    private fun provideSharedPreferences(androidContext: Context): SharedPreferences {
        return androidContext.getSharedPreferences("SampleApp", Context.MODE_PRIVATE)
    }
}