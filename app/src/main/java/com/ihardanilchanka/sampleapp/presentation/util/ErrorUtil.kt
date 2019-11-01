package com.ihardanilchanka.sampleapp.presentation.util

import android.content.Context
import com.ihardanilchanka.sampleapp.R
import retrofit2.HttpException
import java.net.UnknownHostException

fun Throwable.getErrorDialogTitle(context: Context): String {
    return when {
        this is HttpException && this.code() == 401 ->
            context.getString(R.string.state_view_error_401_title)
        this is HttpException && this.code() == 404 ->
            context.getString(R.string.state_view_error_404_title)
        this is UnknownHostException ->
            context.getString(R.string.state_view_error_no_internet_title)
        else -> context.getString(R.string.state_view_error_default_title)
    }
}

fun Throwable.getErrorDialogMessage(context: Context): String {
    return when {
        this is HttpException && this.code() == 401 -> ""
        this is HttpException && this.code() == 404 -> ""
        this is UnknownHostException ->
            context.getString(R.string.state_view_error_no_internet_message)
        else -> context.getString(R.string.state_view_error_default_message)
    }
}
