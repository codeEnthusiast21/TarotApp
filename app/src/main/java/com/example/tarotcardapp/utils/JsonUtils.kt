package com.example.tarotcardapp.utils

import android.content.Context
import com.example.tarotcardapp.model.data.TarotCardResponse
import com.google.gson.Gson
import java.io.IOException

class JsonUtils(private val context: Context) {

    fun loadTarotCardsFromJson(): TarotCardResponse {
        val jsonString = readJsonFromRaw()
        return Gson().fromJson(jsonString, TarotCardResponse::class.java)
    }

    private fun readJsonFromRaw(): String {
        try {
            val inputStream = context.resources.openRawResource(com.example.tarotcardapp.R.raw.tarot_cards)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            return "{\"nhits\": 0, \"cards\": []}"
        }
    }
}