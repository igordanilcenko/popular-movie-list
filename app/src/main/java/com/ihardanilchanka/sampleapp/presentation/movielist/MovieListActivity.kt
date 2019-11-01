package com.ihardanilchanka.sampleapp.presentation.movielist

import android.os.Bundle
import com.ihardanilchanka.sampleapp.R
import com.ihardanilchanka.sampleapp.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_movie_list.*

class MovieListActivity : BaseActivity() {

    override fun getFragmentContainerId(): Int = R.id.fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            addFragment(MovieListFragment.newInstance())
        }
    }
}
