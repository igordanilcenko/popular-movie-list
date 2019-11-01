package com.ihardanilchanka.sampleapp.presentation.moviedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import coil.api.load
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.view_movie_detail_image.*

class MovieDetailActivity : BaseActivity() {

    companion object {
        private const val ARG_MOVIE_DETAIL = "ARG_MOVIE_DETAIL"

        fun newIntent(
            context: Context,
            movie: Movie
        ): Intent {
            val i = Intent(context, MovieDetailActivity::class.java)
            i.putExtra(ARG_MOVIE_DETAIL, movie)
            return i
        }
    }

    override fun getFragmentContainerId(): Int {
        return R.id.fragment_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val movie = intent.getParcelableExtra<Movie>(ARG_MOVIE_DETAIL)

        if (movie != null) {
            setUpActionBar(movie)

            if (savedInstanceState == null) {
                addFragment(MovieDetailFragment.newInstance(movie))
            }
        } else {
            throw IllegalStateException("Movie must not be null")
        }
    }

    private fun setUpActionBar(movie: Movie) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsing_toolbar.title = movie.title
        movie_detail_backdrop.load(movie.backdropUrl)
        movie_detail_title.text = movie.title
    }
}
