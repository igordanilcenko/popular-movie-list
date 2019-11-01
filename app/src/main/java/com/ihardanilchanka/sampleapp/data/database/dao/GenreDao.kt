package com.ihardanilchanka.sampleapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ihardanilchanka.sampleapp.data.database.entity.GenreEntity

@Dao
interface GenreDao {

    @Query("SELECT * FROM genre")
    suspend fun getAll(): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg genre: GenreEntity)

    @Query("DELETE FROM genre")
    suspend fun deleteAllItems()
}