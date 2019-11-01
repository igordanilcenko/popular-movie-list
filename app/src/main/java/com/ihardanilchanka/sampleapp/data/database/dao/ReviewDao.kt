package com.ihardanilchanka.sampleapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ihardanilchanka.sampleapp.data.database.entity.ReviewEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE movie_id = :movieId ORDER BY sort_order ASC")
    suspend fun getAll(movieId: Int): List<ReviewEntity>

    @Insert
    suspend fun insertAll(vararg review: ReviewEntity)

    @Delete
    suspend fun deleteAll(vararg review: ReviewEntity)
}
