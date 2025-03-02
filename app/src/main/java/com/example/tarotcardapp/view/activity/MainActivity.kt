package com.example.tarotcardapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tarotcardapp.R
import com.example.tarotcardapp.databinding.ActivityMainBinding
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.repository.TarotRepository
import com.example.tarotcardapp.presenter.TarotReadingPresenter
import com.example.tarotcardapp.view.adapter.TarotCardAdapter
import com.example.tarotcardapp.view.interfaces.TarotReadingView

class MainActivity : AppCompatActivity(), TarotReadingView {

    private lateinit var presenter: TarotReadingPresenter
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardAdapter: TarotCardAdapter
    private var cards: List<TarotCard> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        // Setup RecyclerView
        binding.recyclerViewCards.layoutManager = GridLayoutManager(this, 3)
        cardAdapter = TarotCardAdapter { card ->
            onCardClick(card)
        }
        binding.recyclerViewCards.adapter = cardAdapter

        // Initialize presenter
        val repository = TarotRepository(applicationContext)
        presenter = TarotReadingPresenter(repository, this)

        // Set click listener
        binding.btnStartReading.setOnClickListener {
            startReading()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                startActivity(Intent(this, ReadingHistoryActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startReading() {
        val question = binding.editTextQuestion.text.toString()
        if (question.isBlank()) {
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            return
        }

        binding.recyclerViewCards.visibility = View.VISIBLE
        binding.textInstructions.visibility = View.VISIBLE
        binding.btnStartReading.visibility = View.GONE
        presenter.loadCards()
    }

    private fun onCardClick(card: TarotCard) {
        val updatedCards = cards.map {
            if (it.name_short == card.name_short) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }

        cards = updatedCards
        cardAdapter.updateCards(updatedCards)

        val selectedCards = updatedCards.filter { it.isSelected }
        if (selectedCards.size == 3) {
            presenter.onCardSelected(
                updatedCards,
                card,
                binding.editTextQuestion.text.toString()
            )
        }
    }

    override fun showCards(cards: List<TarotCard>) {
        this.cards = cards
        cardAdapter.updateCards(cards)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun navigateToResults(selectedCards: List<TarotCard>, question: String) {
        presenter.saveReading(question, selectedCards)

        val intent = Intent(this, ReadingResultActivity::class.java).apply {
            putExtra("QUESTION", question)
            putExtra("CARD_NAMES", selectedCards.map { it.name_short }.toTypedArray())
            putExtra("CARD_REVERSED", selectedCards.map { it.isReversed }.toBooleanArray())
        }
        startActivity(intent)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }
}