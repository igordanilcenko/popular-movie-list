package com.ihardanilchanka.sampleapp.presentation.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.presentation.misc.StateLayout
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogMessage
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogTitle
import kotlinx.android.synthetic.main.fragment_movie_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailFragment : Fragment() {

    private val viewModel: MovieDetailViewModel by viewModel()
    companion object {

        const val ARG_MOVIE = "ARG_MOVIE"
        @JvmStatic fun newInstance(movie: Movie): MovieDetailFragment {
            val f = MovieDetailFragment()
            f.arguments = bundleOf(ARG_MOVIE to movie)
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initViewModel(arguments!!.getParcelable(ARG_MOVIE)!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMovieDetail().observe(
            viewLifecycleOwner,
            Observer { (movieDetail, state) ->
                when {
                    state.type == StatefulLiveData.State.Type.LOADING -> showProgress()
                    state.type == StatefulLiveData.State.Type.READY -> showContent(movieDetail!!)
                    state is StatefulLiveData.ErrorState -> showError(state.error)
                }
            })
        viewModel.getEventOpenMovieDetail().observe(viewLifecycleOwner,
            Observer { navigateToMovieDetail(it) })
    }

    private fun showProgress() {
        view?.movie_detail_state_view?.setState(StateLayout.State.LOADING)
    }

    private fun showError(error: Throwable) {
        error.printStackTrace()
        view?.movie_detail_state_view?.setErrorState(
            error.getErrorDialogTitle(context!!),
            error.getErrorDialogMessage(context!!)
        ) { viewModel.onReloadDataClicked() }
    }

    private fun showContent(movieDetail: MovieDetail) {
        view?.let {
            it.movie_detail_recycler_view.adapter = MovieDetailAdapter(movieDetail,
                object : MovieDetailAdapter.MovieListener {
                    override fun onSimilarMovieSelected(movie: Movie) {
                        viewModel.onMovieSelected(movie)
                    }
                })
            it.movie_detail_state_view.setState(StateLayout.State.NORMAL)
        }
    }

    private fun navigateToMovieDetail(movie: Movie) {
        activity?.let { it.startActivity(MovieDetailActivity.newIntent(it, movie)) }
    }
}
