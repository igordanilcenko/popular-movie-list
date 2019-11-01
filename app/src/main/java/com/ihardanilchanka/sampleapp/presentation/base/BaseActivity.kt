package com.ihardanilchanka.sampleapp.presentation.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ihardanilchanka.sampleapp.R

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        private const val DEFAULT_STACK = "DEFAULT_STACK"
    }

    fun addFragment(
        fragment: Fragment,
        backStack: String = DEFAULT_STACK,
        addToBackStack: Boolean = false,
        useTransition: Boolean = false
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (useTransition) {
            transaction.setCustomAnimations(
                R.anim.af_slide_in_left,
                R.anim.af_slide_out_right,
                R.anim.af_slide_in_right,
                R.anim.af_slide_out_left
            )
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
        transaction.add(getFragmentContainerId(), fragment, fragment.javaClass.name)
        if (addToBackStack) {
            transaction.addToBackStack(backStack)
        }
        transaction.commitAllowingStateLoss()
    }

    /**
     * method is abstract because a developer must not forget add a fragment container layout to
     * the activity layout
     *
     * @return id of a fragment container view
     */
    protected abstract fun getFragmentContainerId(): Int

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}