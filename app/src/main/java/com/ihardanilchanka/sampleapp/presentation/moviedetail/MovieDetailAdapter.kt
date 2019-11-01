package com.ihardanilchanka.sampleapp.presentation.moviedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.domain.model.Review
import com.ihardanilchanka.sampleapp.presentation.misc.SimpleViewHolder
import com.ihardanilchanka.sampleapp.presentation.util.toYear
import kotlinx.android.synthetic.main.item_movie_detail.view.*
import kotlinx.android.synthetic.main.item_movie_detail_review.view.*
import kotlinx.android.synthetic.main.item_movie_detail_similar.view.*

class MovieDetailAdapter(
    private val listener: MovieListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<DataWrapper> = mutableListOf()

    constructor(movieDetail: MovieDetail, listener: MovieListener) : this(listener) {
        setData(movieDetail)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.item_movie_detail -> return MovieDetailViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_movie_detail,
                    parent,
                    false
                )
            )
            R.layout.item_movie_detail_review -> return ReviewViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_movie_detail_review,
                    parent,
                    false
                )
            )
            R.layout.item_movie_detail_similar -> return SimilarMoviesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_movie_detail_similar,
                    parent,
                    false
                )
            )
            R.layout.item_movie_detail_reviews_title -> return SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_movie_detail_reviews_title,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unknown view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieDetailViewHolder -> holder.bind(items[position].movie!!)
            is ReviewViewHolder -> holder.bind(items[position].review!!)
            is SimilarMoviesViewHolder -> holder.bind(items[position].similarMovies!!)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int = items.size

    private fun setData(movieDetail: MovieDetail) {
        items.clear()
        items.add(DataWrapper(movieDetail.movie))
        if (movieDetail.similarMovies.isNotEmpty()) {
            items.add(DataWrapper(movieDetail.similarMovies))
        }
        if (movieDetail.reviews.isNotEmpty()) {
            items.add(DataWrapper(R.layout.item_movie_detail_reviews_title))
            for (review in movieDetail.reviews) {
                items.add(DataWrapper(review))
            }
        }
    }

    inner class MovieDetailViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie) {
            view.movie_detail_overview.text = movie.overview
            view.movie_detail_year.text = movie.releaseDate.toYear().toString()
            view.movie_detail_genres.text =
                movie.genreNames.joinToString()
            // divided by 2 because of 5 total stars
            view.movie_detail_rating_bar.rating = movie.voteAverage.toFloat() / 2
            view.movie_detail_rating.text = movie.voteAverage.toString()
        }
    }

    inner class ReviewViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(review: Review) {
            view.movie_detail_review_author.text = review.author
            view.movie_detail_review_content.text = review.content
        }
    }

    inner class SimilarMoviesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(similarMovies: List<Movie>) {
            with(view.recycler_view_similar_movies) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SimilarMovieListAdapter(similarMovies,
                    object : SimilarMovieListAdapter.OnMovieItemClickListener {
                        override fun onMovieItemClicked(item: Movie) {
                            listener.onSimilarMovieSelected(item)
                        }
                    })
            }
        }
    }

    interface MovieListener {
        fun onSimilarMovieSelected(movie: Movie)
    }

    private data class DataWrapper(@LayoutRes val type: Int) {

        var movie: Movie? = null
        var review: Review? = null
        var similarMovies: List<Movie>? = null

        constructor(movie: Movie) : this(R.layout.item_movie_detail) {
            this.movie = movie
        }

        constructor(review: Review) : this(R.layout.item_movie_detail_review) {
            this.review = review
        }

        constructor(similarMovies: List<Movie>) : this(R.layout.item_movie_detail_similar) {
            this.similarMovies = similarMovies
        }
    }
}
