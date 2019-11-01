package com.ihardanilchanka.sampleapp.presentation.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.presentation.misc.EndlessRecyclerOnScrollListener
import com.ihardanilchanka.sampleapp.presentation.misc.StateLayout
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData.State.Type
import com.ihardanilchanka.sampleapp.presentation.moviedetail.MovieDetailActivity
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogMessage
import com.ihardanilchanka.sampleapp.presentation.util.getErrorDialogTitle
import kotlinx.android.synthetic.main.fragment_movie_list.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by viewModel()

    companion object {
        @JvmStatic fun newInstance() = MovieListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)

        with(view.movie_list_recycler_view) {
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object :
                EndlessRecyclerOnScrollListener(layoutManager as LinearLayoutManager) {
                override fun onLoadMore() {
                    viewModel.onNeedLoadMore()
                }
            })
        }

        view.movie_list_swipe_to_refresh.setOnRefreshListener { viewModel.onSwipeToRefresh() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMovieList().observe(
            viewLifecycleOwner,
            Observer { (movies, state) ->
                if (state.type != Type.LOADING) {
                    view.movie_list_swipe_to_refresh.isRefreshing = false
                }
                if (!movies.isNullOrEmpty()) {
                    showContent(movies, state)
                } else {
                    when {
                        state.type == Type.LOADING -> showProgress()
                        state.type == Type.EMPTY -> showEmptyList()
                        state is StatefulLiveData.ErrorState -> showError(state.error)
                    }
                }
            })

        viewModel.getEventOpenMovieDetail().observe(
            viewLifecycleOwner,
            Observer { navigateToMovieDetail(it) }
        )
    }

    private fun showProgress() {
        requireView().movie_list_state_view.setState(StateLayout.State.LOADING)
    }

    private fun showEmptyList() {
        requireView().movie_list_state_view.setState(StateLayout.State.EMPTY)
    }

    private fun showError(error: Throwable) {
        requireView().movie_list_state_view.setErrorState(
            error.getErrorDialogTitle(requireContext()),
            error.getErrorDialogMessage(requireContext())
        ) { viewModel.onReloadDataClicked() }
    }

    private fun showContent(movies: List<Movie>, state: StatefulLiveData.State) {
        if (requireView().movie_list_recycler_view.adapter == null) {
            requireView().movie_list_recycler_view.adapter = MovieListAdapter(
                movies.toMutableList(),
                state,
                object : MovieListAdapter.OnMovieItemClickListener {
                    override fun onReloadClicked() {
                        viewModel.onReloadDataClicked()
                    }

                    override fun onMovieItemClicked(item: Movie) {
                        viewModel.onMovieSelected(item)
                    }
                }
            )
        } else {
            (requireView().movie_list_recycler_view.adapter as MovieListAdapter)
                .setData(movies, state)
        }
        requireView().movie_list_state_view.setState(StateLayout.State.NORMAL)
    }

    private fun navigateToMovieDetail(movie: Movie) {
        requireActivity().startActivity(MovieDetailActivity.newIntent(requireContext(), movie))
    }
}
