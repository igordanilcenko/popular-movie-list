package com.ihardanilchanka.sampleapp.data.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class MovieTypeConverters : KoinComponent {

    private val moshi: Moshi by inject()

    @TypeConverter
    fun Long.toDate(): Date {
        return Date(this)
    }

    @TypeConverter
    fun Date.toTimestamp(): Long {
        return this.time
    }

    @TypeConverter
    fun String.toListInt(): List<Int>? {
        return moshi.adapter<List<Int>>(List::class.java).fromJson(this)
    }

    @TypeConverter
    fun List<Int>.toJson(): String {
        return moshi.adapter<List<Int>>(List::class.java).toJson(this)
    }
}