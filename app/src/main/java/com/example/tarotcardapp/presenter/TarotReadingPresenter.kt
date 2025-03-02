package com.example.tarotcardapp.presenter


import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.data.TarotReading
import com.example.tarotcardapp.model.repository.TarotRepository
import com.example.tarotcardapp.view.interfaces.ReadingHistoryView
import com.example.tarotcardapp.view.interfaces.ReadingResultView
import com.example.tarotcardapp.view.interfaces.TarotReadingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TarotReadingPresenter(
    private val repository: TarotRepository,
    private var view: TarotReadingView? = null
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadCards() {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val cards = repository.getAllCards()
                view?.hideLoading()
                view?.showCards(cards)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error loading cards: ${e.message}")
                }
            }
        }
    }

    fun onCardSelected(cards: List<TarotCard>, selectedCard: TarotCard, question: String) {
        val selectedCards = cards.filter { it.isSelected }.toMutableList()

        // Check if 3 cards are already selected and the current card is not among them
        if (selectedCards.size >= 3 && !selectedCard.isSelected) {
            view?.showError("You can only select 3 cards")
            return
        }

        // If 3 cards are selected, proceed to reading result
        if (selectedCards.size == 3) {
            view?.navigateToResults(selectedCards, question)
        }
    }

    fun saveReading(question: String, selectedCards: List<TarotCard>) {
        coroutineScope.launch {
            try {
                val reading = TarotReading(
                    question = question,
                    selectedCards = selectedCards
                )
                repository.saveReading(reading)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Error saving reading: ${e.message}")
                }
            }
        }
    }

    fun detach() {
        view = null
    }
}

class ReadingHistoryPresenter(
    private val repository: TarotRepository,
    private var view: ReadingHistoryView? = null
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadReadingHistory() {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val readings = repository.getAllReadings()
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showReadingHistory(readings)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error loading reading history: ${e.message}")
                }
            }
        }
    }

    fun detach() {
        view = null
    }
}

class ReadingResultPresenter(
    private val repository: TarotRepository,
    private var view: ReadingResultView? = null
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadReading(id: Long) {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val reading = repository.getReadingById(id)
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.displayReadingResult(reading)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error loading reading: ${e.message}")
                }
            }
        }
    }

    fun detach() {
        view = null
    }
}