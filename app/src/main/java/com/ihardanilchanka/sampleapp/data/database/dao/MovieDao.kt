package com.ihardanilchanka.sampleapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihardanilchanka.sampleapp.data.database.entity.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY sort_order ASC ")
    suspend fun getAll(): List<MovieEntity>

    @Insert
    suspend fun insertAll(vararg movies: MovieEntity)

    @Query("DELETE FROM movie")
    suspend fun deleteAllItems()
}
