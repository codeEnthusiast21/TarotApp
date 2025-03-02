package com.example.tarotcardapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotReading
import java.text.SimpleDateFormat
import java.util.Locale

class ReadingHistoryAdapter(
    private val onReadingClick: (TarotReading) -> Unit
) : RecyclerView.Adapter<ReadingHistoryAdapter.ReadingViewHolder>() {

    private var readings: List<TarotReading> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reading_history, parent, false)
        return ReadingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val reading = readings[position]
        holder.bind(reading)
    }

    override fun getItemCount(): Int = readings.size

    fun updateReadings(newReadings: List<TarotReading>) {
        readings = newReadings
        notifyDataSetChanged()
    }

    inner class ReadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.textViewQuestion)
        private val dateText: TextView = itemView.findViewById(R.id.textViewDate)
        private val cardsText: TextView = itemView.findViewById(R.id.chipGroupCards)

        fun bind(reading: TarotReading) {
            questionText.text = reading.question
            dateText.text = dateFormat.format(reading.date)

            // Format cards names
            val cardNames = reading.selectedCards.joinToString(", ") { it.name }
            cardsText.text = cardNames

            // Set click listener
            itemView.setOnClickListener {
                onReadingClick(reading)
            }
        }
    }
}