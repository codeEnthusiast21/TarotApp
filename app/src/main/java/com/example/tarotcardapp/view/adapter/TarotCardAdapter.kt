package com.example.tarotcardapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotCard
import com.example.tarotcardapp.utils.ImageUtils

class TarotCardAdapter(
    private val showMeaning: Boolean = false,
    private val onCardClick: (TarotCard) -> Unit
) : RecyclerView.Adapter<TarotCardAdapter.CardViewHolder>() {

    private var cards: List<TarotCard> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarot_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card, showMeaning)
    }

    override fun getItemCount(): Int = cards.size

    fun updateCards(newCards: List<TarotCard>) {
        cards = newCards
        notifyDataSetChanged()
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardImage: ImageView = itemView.findViewById(R.id.imageCard)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(card: TarotCard, showMeaning: Boolean) {
            progressBar.visibility = View.GONE // Hide progress bar completely

            if (card.isSelected || showMeaning) {
                ImageUtils.loadCardImage(itemView.context, card, cardImage)
                cardImage.rotation = if (card.isReversed) 180f else 0f
            } else {
                ImageUtils.loadCardBackImage(itemView.context, cardImage)
                cardImage.rotation = 0f
            }

            val borderResource = if (card.isSelected) R.drawable.selected_card_border else R.drawable.card_border
            cardImage.setBackgroundResource(borderResource)

            itemView.setOnClickListener {
                onCardClick(card)
            }
        }    }
}