package com.ihardanilchanka.sampleapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ihardanilchanka.sampleapp.data.database.entity.SimilarMovieEntity

@Dao
interface SimilarMovieDao {

    @Query("SELECT * FROM similar_movie WHERE similar_to = :movieId ORDER BY sort_order ASC")
    suspend fun getAll(movieId: Int): List<SimilarMovieEntity>

    @Insert
    suspend fun insertAll(vararg similarMovie: SimilarMovieEntity)

    @Delete
    suspend fun deleteAll(vararg similarMovie: SimilarMovieEntity)
}
