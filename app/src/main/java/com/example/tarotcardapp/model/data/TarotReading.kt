package com.example.tarotcardapp.model.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tarotcardapp.model.local.Converters
import java.util.Date

@Entity(tableName = "tarot_readings")
@TypeConverters(Converters::class)
data class TarotReading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val selectedCards: List<TarotCard>,
    val date: Date = Date()
)