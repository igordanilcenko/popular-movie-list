package com.ihardanilchanka.sampleapp.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review")
data class ReviewEntity(
    @ColumnInfo(name = "movie_id")
    val movieId: Int,
    @PrimaryKey
    val id: String,
    val author: String,
    val content: String,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int
)
