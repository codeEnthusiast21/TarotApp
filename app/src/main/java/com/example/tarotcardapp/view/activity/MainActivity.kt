package com.example.tarotcardapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tarotcardapp.R
import com.example.tarotcardapp.databinding.ActivityMainBinding
import android.content.Intent
import android.graphics.Typeface
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.repository.TarotRepository
import com.example.tarotcardapp.presenter.TarotReadingPresenter
import com.example.tarotcardapp.view.adapter.TarotCardAdapter
import com.example.tarotcardapp.view.interfaces.TarotReadingView
import com.google.android.material.snackbar.Snackbar

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
            showSnackbar("Please enter a question")
            return
        }

        binding.recyclerViewCards.visibility = View.VISIBLE
        binding.textInstructions.visibility = View.VISIBLE
        binding.btnStartReading.visibility = View.GONE
        presenter.loadCards()
    }

    private fun onCardClick(card: TarotCard) {
        val selectedCount = cards.count { it.isSelected }

        // If card is not selected and we already have 3 cards, return
        if (!card.isSelected && selectedCount >= 3) {
            showSnackbar("You can only select 3 cards")
            return
        }

        val question = binding.editTextQuestion.text.toString()
        if (question.isBlank()) {
            showSnackbar("Please enter a question")
            return
        }

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
            // Save reading
            presenter.saveReading(question, selectedCards)

            // Navigate directly to results
            val intent = Intent(this, ReadingResultActivity::class.java).apply {
                putExtra("QUESTION", question)
                putExtra("CARD_NAMES", selectedCards.map { it.name_short }.toTypedArray())
                putExtra("CARD_REVERSED", selectedCards.map { it.isReversed }.toBooleanArray())
            }
            startActivity(intent)
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
        showSnackbar(message)
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }
    override fun onResume() {
        super.onResume()
        // Reset the UI to initial state
        binding.editTextQuestion.setText("")
        binding.recyclerViewCards.visibility = View.GONE
        binding.textInstructions.visibility = View.GONE
        binding.btnStartReading.visibility = View.VISIBLE

        // Reset cards selection
        cards = cards.map { it.copy(isSelected = false) }
        cardAdapter.updateCards(cards)
    }
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.topMargin = resources.getDimensionPixelOffset(R.dimen.snackbar_margin_top)
        snackbarView.layoutParams = params

        snackbarView.setBackgroundResource(R.drawable.snackbar_background)
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.apply {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(context, R.color.mystical_gold))
            typeface = Typeface.create("Playfair Display", Typeface.NORMAL)
            textSize = 16f
            setPadding(32, 16, 32, 16)
        }

        snackbar.show()
    }
}