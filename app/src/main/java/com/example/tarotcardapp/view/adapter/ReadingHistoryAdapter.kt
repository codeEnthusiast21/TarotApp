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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.core.content.ContextCompat

class ReadingHistoryAdapter(private val onReadingClick: (TarotReading) -> Unit) :
    RecyclerView.Adapter<ReadingHistoryAdapter.ReadingViewHolder>() {

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
        private val chipGroup: ChipGroup = itemView.findViewById(R.id.chipGroupCards)

        fun bind(reading: TarotReading) {
            questionText.text = reading.question
            dateText.text = dateFormat.format(reading.date)

            // Clear existing chips and add new ones
            chipGroup.removeAllViews()
            reading.selectedCards.forEach { card ->
                val chip = Chip(chipGroup.context).apply {
                    text = card.name
                    setTextColor(ContextCompat.getColor(context, R.color.mystical_light))
                    setChipBackgroundColorResource(R.color.dark_background)
                }
                chipGroup.addView(chip)
            }

            itemView.setOnClickListener {
                onReadingClick(reading)
            }
        }
    }
}