package com.ihardanilchanka.sampleapp.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: Date,
    val voteAverage: Double,
    val posterUrl: String?,
    val backdropUrl: String?,
    val genreNames: List<String>
) : Parcelable {

    override fun toString(): String {
        return "$id - $title"
    }
}