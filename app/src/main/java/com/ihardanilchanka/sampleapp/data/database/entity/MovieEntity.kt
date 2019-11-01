package com.ihardanilchanka.sampleapp.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ihardanilchanka.sampleapp.data.database.MovieTypeConverters
import java.util.*

@Entity(tableName = "movie")
@TypeConverters(MovieTypeConverters::class)
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    val overview: String,
    @ColumnInfo(name = "release_date")
    val releaseDate: Date,
    val title: String,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    @ColumnInfo(name = "genre_ids")
    val genreIds: List<Int>,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int
)