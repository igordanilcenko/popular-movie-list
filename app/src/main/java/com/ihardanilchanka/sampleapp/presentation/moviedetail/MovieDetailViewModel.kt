package com.ihardanilchanka.sampleapp.presentation.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihardanilchanka.sampleapp.domain.MovieInteractor
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.presentation.misc.SingleLiveEvent
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieInteractor: MovieInteractor
) : ViewModel() {

    private lateinit var movie: Movie

    private val movieDetail = StatefulLiveData<MovieDetail>()
    fun getMovieDetail(): LiveData<Pair<MovieDetail?, StatefulLiveData.State>> = movieDetail

    private val eventOpenMovieDetail = SingleLiveEvent<Movie>()
    fun getEventOpenMovieDetail(): LiveData<Movie> = eventOpenMovieDetail

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        movieDetail.setErrorState(throwable)
    }

    fun initViewModel(movie: Movie) {
        if (movieDetail.value == null) {
            this.movie = movie
            loadMovieDetail(this.movie)
        }
    }

    private fun loadMovieDetail(movie: Movie) {
        viewModelScope.launch(errorHandler) {
            movieDetail.setState(StatefulLiveData.State.Type.LOADING)
            val detail = movieInteractor.loadMovieDetail(movie)
            movieDetail.setData(detail)
        }
    }

    fun onMovieSelected(item: Movie) {
        eventOpenMovieDetail.value = item
    }

    fun onReloadDataClicked() {
        loadMovieDetail(this.movie)
    }
}
