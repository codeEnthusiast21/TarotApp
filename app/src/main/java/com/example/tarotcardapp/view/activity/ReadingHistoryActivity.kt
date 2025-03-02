package com.example.tarotcardapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tarotcardapp.databinding.ActivityReadingHistoryBinding
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotReading
import com.example.tarotcardapp.model.repository.TarotRepository
import com.example.tarotcardapp.presenter.ReadingHistoryPresenter
import com.example.tarotcardapp.view.adapter.ReadingHistoryAdapter
import com.example.tarotcardapp.view.interfaces.ReadingHistoryView

class ReadingHistoryActivity : AppCompatActivity(), ReadingHistoryView {

    private lateinit var presenter: ReadingHistoryPresenter
    private lateinit var binding: ActivityReadingHistoryBinding
    private lateinit var adapter: ReadingHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup RecyclerView
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        adapter = ReadingHistoryAdapter { reading ->
            val intent = Intent(this, ReadingResultActivity::class.java).apply {
                putExtra("READING_ID", reading.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewHistory.adapter = adapter

        // Initialize presenter
        val repository = TarotRepository(applicationContext)
        presenter = ReadingHistoryPresenter(repository, this)

        // Load reading history
        presenter.loadReadingHistory()
    }

    override fun showReadingHistory(readings: List<TarotReading>) {
        if (readings.isEmpty()) {
            binding.textEmptyHistory.visibility = View.VISIBLE
            binding.recyclerViewHistory.visibility = View.GONE
        } else {
            binding.textEmptyHistory.visibility = View.GONE
            binding.recyclerViewHistory.visibility = View.VISIBLE
            adapter.updateReadings(readings)
        }
    }

    override fun showLoading() {
        binding.progressBarHistory.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBarHistory.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }
}