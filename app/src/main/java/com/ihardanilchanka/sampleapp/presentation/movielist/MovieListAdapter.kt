package com.ihardanilchanka.sampleapp.presentation.movielist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.presentation.misc.StateLayout
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogMessage
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogTitle
import com.ihardanilchanka.sampleapp.presentation.util.toYear
import kotlinx.android.synthetic.main.item_movie_list.view.*
import kotlinx.android.synthetic.main.item_movie_list_loading.view.*

class MovieListAdapter(
    private val values: MutableList<Movie>,
    private var state: StatefulLiveData.State,
    private val listener: OnMovieItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_movie_list -> MovieItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie_list, parent, false)
            )
            R.layout.item_movie_list_loading -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie_list_loading, parent, false)
            )
            else -> throw IllegalStateException("Unknown view holder type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieItemViewHolder -> holder.bind(position + 1, values[position])
            is LoadingViewHolder -> holder.bind(state)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            R.layout.item_movie_list_loading
        } else {
            R.layout.item_movie_list
        }
    }

    override fun getItemCount(): Int = values.size + 1

    fun setData(
        movies: List<Movie>,
        state: StatefulLiveData.State
    ) {
        values.clear()
        values.addAll(movies)
        this.state = state
        notifyDataSetChanged()
    }

    inner class MovieItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(index: Int, movie: Movie) {
            view.movie_item_index.text = index.toString()
            view.movie_item_title.text = "${movie.title} (${movie.releaseDate.toYear()})"
            view.movie_item_genres.text = movie.genreNames.joinToString(prefix = "(", postfix = ")")
            // divided by 2 because of 5 total stars
            view.movie_item_rating_bar.rating = movie.voteAverage.toFloat() / 2
            view.movie_item_rating.text = movie.voteAverage.toString()
            view.movie_item_poster.load(movie.posterUrl)
            view.setOnClickListener { listener.onMovieItemClicked(movie) }
        }
    }

    inner class LoadingViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(state: StatefulLiveData.State) {
            when {
                state.type == StatefulLiveData.State.Type.LOADING ->
                    view.movie_list_loading_view_state_layout.setState(StateLayout.State.LOADING)
                state.type == StatefulLiveData.State.Type.READY ->
                    view.movie_list_loading_view_state_layout.setState(StateLayout.State.NORMAL)
                state is StatefulLiveData.ErrorState ->
                    view.movie_list_loading_view_state_layout.setErrorState(
                        state.error.getErrorDialogTitle(view.context),
                        state.error.getErrorDialogMessage(view.context)
                    ) { listener.onReloadClicked() }
            }
        }
    }

    interface OnMovieItemClickListener {
        fun onMovieItemClicked(item: Movie)
        fun onReloadClicked()
    }
}
