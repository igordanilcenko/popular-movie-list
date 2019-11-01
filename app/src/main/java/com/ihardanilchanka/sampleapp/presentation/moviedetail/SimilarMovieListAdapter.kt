package com.ihardanilchanka.sampleapp.presentation.moviedetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.presentation.misc.SimpleViewHolder
import com.ihardanilchanka.sampleapp.presentation.util.toYear
import kotlinx.android.synthetic.main.item_similar_movie.view.*

class SimilarMovieListAdapter(
    private val values: List<Movie>,
    private val listener: OnMovieItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        when (viewType) {
            R.layout.item_similar_movie -> return MovieItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_similar_movie,
                        parent,
                        false
                    )
            )
            R.layout.item_similar_movie_last_item_placeholder -> return SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_similar_movie_last_item_placeholder,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unknown view holder type.")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            R.layout.item_similar_movie_last_item_placeholder
        } else {
            R.layout.item_similar_movie
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {
            holder.bind(values[position])
        }
    }

    override fun getItemCount(): Int = values.size + 1

    inner class MovieItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(movie: Movie) {
            view.similar_movie_item_title.text = "${movie.title} (${movie.releaseDate.toYear()})"
            view.similar_movie_item_genres.text =
                movie.genreNames.joinToString(prefix = "(", postfix = ")")
            // divided by 2 because of 5 total stars
            view.similar_movie_item_rating_bar.rating = movie.voteAverage.toFloat() / 2
            view.similar_movie_item_poster.load(movie.posterUrl)

            with(view) {
                setOnClickListener { listener.onMovieItemClicked(movie) }
            }
        }
    }

    interface OnMovieItemClickListener {
        fun onMovieItemClicked(item: Movie)
    }
}
