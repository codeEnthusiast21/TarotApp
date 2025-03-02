package com.example.tarotcardapp.model.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.data.TarotReading
import java.util.Date

@Database(entities = [TarotReading::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tarotReadingDao(): TarotReadingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tarot_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Type converters for Room
class Converters {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @androidx.room.TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @androidx.room.TypeConverter
    fun fromCardList(value: List<TarotCard>): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(value)
    }

    @androidx.room.TypeConverter
    fun toCardList(value: String): List<TarotCard> {
        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<TarotCard>>() {}.type
        return gson.fromJson(value, type)
    }
}