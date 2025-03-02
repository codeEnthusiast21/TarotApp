package com.example.tarotcardapp.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tarotcardapp.databinding.ActivityReadingResultBinding
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.model.data.TarotReading
import com.example.tarotcardapp.model.repository.TarotRepository
import com.example.tarotcardapp.presenter.ReadingResultPresenter
import com.example.tarotcardapp.utils.JsonUtils
import com.example.tarotcardapp.view.adapter.TarotCardAdapter
import com.example.tarotcardapp.view.interfaces.ReadingResultView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadingResultActivity : AppCompatActivity(), ReadingResultView {

    private lateinit var presenter: ReadingResultPresenter
    private lateinit var binding: ActivityReadingResultBinding
    private lateinit var resultAdapter: TarotCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadingResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup RecyclerView
        binding.recyclerViewResults.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        resultAdapter = TarotCardAdapter(showMeaning = true) { /* No action needed on click */ }
        binding.recyclerViewResults.adapter = resultAdapter

        // Check if we have reading ID or card names
        val readingId = intent.getLongExtra("READING_ID", -1)

        if (readingId != -1L) {
            // Loading from history
            val repository = TarotRepository(applicationContext)
            presenter = ReadingResultPresenter(repository, this)
            presenter.loadReading(readingId)
        } else {
            // Coming from a new reading
            val question = intent.getStringExtra("QUESTION") ?: ""
            val cardNames = intent.getStringArrayExtra("CARD_NAMES") ?: emptyArray()
            val cardReversed = intent.getBooleanArrayExtra("CARD_REVERSED") ?: booleanArrayOf()

            binding.textQuestion.text = question

            // Load the selected cards
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                try {
                    val allCards = withContext(Dispatchers.IO) {
                        JsonUtils(applicationContext).loadTarotCardsFromJson().cards
                    }

                    val selectedCards = cardNames.mapIndexed { index, nameShort ->
                        allCards.first { it.name_short == nameShort }.copy(
                            isSelected = true,
                            isReversed = cardReversed.getOrElse(index) { false }
                        )
                    }

                    resultAdapter.updateCards(selectedCards)

                    // Generate interpretation
                    val interpretation = generateInterpretation(selectedCards)
                    binding.textInterpretation.text = interpretation

                    hideLoading()
                } catch (e: Exception) {
                    hideLoading()
                    showError("Error loading cards: ${e.message}")
                }
            }
        }
    }

    private fun generateInterpretation(cards: List<TarotCard>): String {
        val sb = StringBuilder()
        sb.append("Your Tarot Reading Interpretation:\n\n")

        cards.forEachIndexed { index, card ->
            val position = when (index) {
                0 -> "Past"
                1 -> "Present"
                2 -> "Future"
                else -> "Position ${index + 1}"
            }

            sb.append("$position: ${card.name}")
            if (card.isReversed) sb.append(" (Reversed)")
            sb.append("\n")
            sb.append(card.getMeaning())
            sb.append("\n\n")
        }

        sb.append("Overall Interpretation:\n")
        sb.append("The cards suggest that your journey is influenced by the energies represented in these three cards. ")
        sb.append("Consider how they connect and what message they collectively bring to your question.")

        return sb.toString()
    }

    override fun displayReadingResult(reading: TarotReading) {
        binding.textQuestion.text = reading.question
        resultAdapter.updateCards(reading.selectedCards)
        binding.textInterpretation.text = generateInterpretation(reading.selectedCards)
    }

    override fun showLoading() {
        binding.progressBarResult.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBarResult.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        if (::presenter.isInitialized) {
            presenter.detach()
        }
        super.onDestroy()
    }
}