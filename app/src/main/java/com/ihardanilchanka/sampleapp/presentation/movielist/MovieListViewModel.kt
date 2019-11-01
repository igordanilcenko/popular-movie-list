package com.ihardanilchanka.sampleapp.presentation.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihardanilchanka.sampleapp.domain.MovieInteractor
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.presentation.misc.SingleLiveEvent
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData.State.Type
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val movieInteractor: MovieInteractor
) : ViewModel() {

    private var currentPage: Int = 1
    private val movies = mutableListOf<Movie>()

    private val movieList = StatefulLiveData<List<Movie>>()
    fun getMovieList(): LiveData<Pair<List<Movie>?, StatefulLiveData.State>> = movieList

    private val eventOpenMovieDetail = SingleLiveEvent<Movie>()
    fun getEventOpenMovieDetail(): LiveData<Movie> = eventOpenMovieDetail

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        movieList.postErrorState(throwable)
    }

    fun initViewModel() {
        if (movieList.value == null) {
            loadMoreMovies()
        }
    }

    fun onMovieSelected(item: Movie) {
        eventOpenMovieDetail.value = item
    }

    fun onReloadDataClicked() {
        loadMoreMovies()
    }

    fun onNeedLoadMore() {
        loadMoreMovies()
    }

    fun onSwipeToRefresh() {
        loadMoreMovies(refresh = true)
    }

    private fun loadMoreMovies(refresh: Boolean = false) {
        // prevent repeated start of loading
        if (canLoadMore() || refresh) {
            viewModelScope.launch(errorHandler) {
                movieList.postState(Type.LOADING)

                val newMovies = movieInteractor.loadMovieList(
                    page = if (refresh) 1 else currentPage,
                    refreshCache = refresh
                )

                // clear movie list only after successful loading new data
                if (refresh) {
                    movies.clear()
                    currentPage = 1
                }

                movies.addAll(newMovies)
                currentPage++

                if (movies.isNotEmpty()) {
                    movieList.postData(movies.toMutableList())
                } else {
                    movieList.postState(Type.EMPTY)
                }
            }
        }
    }

    private fun canLoadMore(): Boolean {
        val actualState = movieList.getState()?.type
        return actualState == null || actualState != Type.LOADING
    }
}