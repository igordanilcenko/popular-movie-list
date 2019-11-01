package com.ihardanilchanka.sampleapp.domain.model

data class MovieDetail(
    val movie: Movie,
    val reviews: List<Review>,
    val similarMovies: List<Movie>
)