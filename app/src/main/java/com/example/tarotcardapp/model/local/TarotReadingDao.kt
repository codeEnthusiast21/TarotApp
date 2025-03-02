package com.example.tarotcardapp.model.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tarotcardapp.model.data.TarotReading

@Dao
interface TarotReadingDao {
    @Insert
    suspend fun insertReading(reading: TarotReading): Long

    @Query("SELECT * FROM tarot_readings ORDER BY date DESC")
    suspend fun getAllReadings(): List<TarotReading>

    @Query("SELECT * FROM tarot_readings WHERE id = :id")
    suspend fun getReadingById(id: Long): TarotReading
}