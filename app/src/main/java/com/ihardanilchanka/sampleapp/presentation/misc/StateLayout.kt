package com.ihardanilchanka.sampleapp.presentation.misc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.ihardanilchanka.sampleapp.R
import kotlinx.android.synthetic.main.layout_error.view.*

class StateLayout : FrameLayout {

    private var lastState: State? = null
    private lateinit var view: View
    private lateinit var progressView: View
    private lateinit var errorView: View
    private lateinit var emptyView: View

    @LayoutRes private var emptyLayout = R.layout.layout_empty
    @LayoutRes private var errorLayout = R.layout.layout_error
    @LayoutRes private var progressLayout = R.layout.layout_progress

    private var isInitNeeded: Boolean = true

    constructor(context: Context) : super(context)

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) :
            super(context, attrs, defStyle) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0)

        try {
            emptyLayout =
                a.getResourceId(R.styleable.StateLayout_emptyLayout, R.layout.layout_empty)
            errorLayout =
                a.getResourceId(R.styleable.StateLayout_errorLayout, R.layout.layout_error)
            progressLayout =
                a.getResourceId(R.styleable.StateLayout_progressLayout, R.layout.layout_progress)
        } finally {
            a.recycle()
        }
    }

    fun setState(state: State) {
        if (state == lastState) {
            return
        }
        if (isInitNeeded) {
            init()
            isInitNeeded = false
        }
        val newShowingView: View? = when (state) {
            State.LOADING -> progressView
            State.ERROR -> errorView
            State.NORMAL -> view
            State.EMPTY -> emptyView
        }
        val oldShowingView = getCurrentShowingView()
        // if no cross-fading needed, just show a new view and hide all others
        if ((oldShowingView == null || state == State.LOADING) && newShowingView != null) {
            newShowingView.visibility = View.VISIBLE
            setViewsShownExcept(
                arrayOf(newShowingView),
                progressView,
                errorView,
                view,
                emptyView
            )
        } else {
            crossFadeViews(oldShowingView, newShowingView)
            setViewsShownExcept(
                arrayOf(oldShowingView!!, newShowingView!!),
                progressView,
                errorView,
                view,
                emptyView
            )
        }
        lastState = state
    }

    fun setErrorState(
        title: String = context.getString(R.string.state_view_error_default_title),
        message: String = context.getString(R.string.state_view_error_default_message),
        onReloadClick: () -> Unit
    ) {
        setState(State.ERROR)
        errorView.error_title.text = title
        errorView.error_message.text = message
        errorView.error_button_reload.setOnClickListener { onReloadClick() }
    }

    private fun init() {
        view = getChildAt(0)
        progressView = LayoutInflater.from(context).inflate(progressLayout, this, false)
        errorView = LayoutInflater.from(context).inflate(errorLayout, this, false)
        emptyView = LayoutInflater.from(context).inflate(emptyLayout, this, false)

        val lp = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(progressView, lp)
        addView(errorView, lp)
        addView(emptyView, lp)
    }

    private fun getCurrentShowingView(): View? {
        return when (lastState) {
            null -> null
            State.LOADING -> progressView
            State.ERROR -> errorView
            State.NORMAL -> view
            State.EMPTY -> emptyView
        }
    }

    private fun setViewsShownExcept(
        ignoredViews: Array<View>,
        vararg views: View
    ) {
        val ignoredList = listOf(*ignoredViews)
        for (view in views) {
            if (!ignoredList.contains(view)) {
                view.visibility = View.INVISIBLE
            }
        }
    }

    private fun crossFadeViews(viewToHide: View?, viewToShow: View?) {
        if (viewToHide != null) {
            viewToHide.startAnimation(
                AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
            )
            viewToHide.visibility = View.INVISIBLE
        }
        if (viewToShow != null) {
            viewToShow.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            viewToShow.visibility = View.VISIBLE
        }
    }

    enum class State {
        NORMAL, LOADING, ERROR, EMPTY
    }
}