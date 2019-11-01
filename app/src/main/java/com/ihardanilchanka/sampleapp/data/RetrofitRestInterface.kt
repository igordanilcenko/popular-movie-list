package com.ihardanilchanka.sampleapp.data

import com.ihardanilchanka.sampleapp.data.response.ConfigurationResponse
import com.ihardanilchanka.sampleapp.data.response.GenreListResponse
import com.ihardanilchanka.sampleapp.data.response.MovieListResponse
import com.ihardanilchanka.sampleapp.data.response.ReviewListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitRestInterface {

    @GET("movie/popular")
    suspend fun getPopularMovieList(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): ReviewListResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovieList(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponse

    @GET("configuration")
    suspend fun getConfiguration(
        @Query("api_key") apiKey: String
    ): ConfigurationResponse

    @GET("genre/movie/list")
    suspend fun getGenreList(
        @Query("api_key") apiKey: String
    ): GenreListResponse
}