package com.example.tarotcardapp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotCard


class ImageUtils {
    companion object {
        private const val BASE_URL = "https://raw.githubusercontent.com/krates98/tarotcardapi/main/images/"

        fun loadCardImage(context: Context, card: TarotCard, imageView: ImageView) {
            val imageFileName = card.getImageFileName()
            val imageUrl = BASE_URL + imageFileName

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView)
        }

        fun loadCardBackImage(context: Context, imageView: ImageView) {
            Glide.with(context)
                .load(R.drawable.placeholder)
                .into(imageView)
        }
    }
}