package com.example.tarotcardapp.view.interfaces

import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.data.TarotReading


interface TarotReadingView {
    fun showCards(cards: List<TarotCard>)
    fun showLoading()
    fun hideLoading()
    fun navigateToResults(selectedCards: List<TarotCard>, question: String)
    fun showError(message: String)
}

interface ReadingHistoryView {
    fun showReadingHistory(readings: List<TarotReading>)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}

interface ReadingResultView {
    fun displayReadingResult(reading: TarotReading)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}