package com.example.tarotcardapp.model.repository


import android.content.Context
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.data.TarotReading
import com.example.tarotcardapp.model.local.AppDatabase
import com.example.tarotcardapp.utils.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random

class TarotRepository(private val context: Context) {

    private val readingDao = AppDatabase.getDatabase(context).tarotReadingDao()

    suspend fun getAllCards(): List<TarotCard> = withContext(Dispatchers.IO) {
        val jsonUtils = JsonUtils(context)
        val tarotResponse = jsonUtils.loadTarotCardsFromJson()
        shuffleCards(tarotResponse.cards)
    }

    private fun shuffleCards(cards: List<TarotCard>): List<TarotCard> {
        val random = Random()
        return cards.map { card ->
            // Random chance of the card being reversed (upside down)
            card.copy(isReversed = random.nextBoolean())
        }.shuffled()
    }

    suspend fun saveReading(reading: TarotReading) = withContext(Dispatchers.IO) {
        readingDao.insertReading(reading)
    }

    suspend fun getAllReadings(): List<TarotReading> = withContext(Dispatchers.IO) {
        readingDao.getAllReadings()
    }

    suspend fun getReadingById(id: Long): TarotReading = withContext(Dispatchers.IO) {
        readingDao.getReadingById(id)
    }
}