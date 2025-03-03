package com.example.tarotcardapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tarotcardapp.R
import com.example.tarotcardapp.model.data.TarotCard

object ImageUtils {
    private const val BASE_URL = "https://raw.githubusercontent.com/krates98/tarotcardapi/main/images/"

    fun loadCardImage(context: Context, card: TarotCard, imageView: ImageView) {
        val imageUrl = BASE_URL + card.getImageFileName()

        Glide.with(context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(imageView)
    }

    fun loadCardBackImage(context: Context, imageView: ImageView) {
        Glide.with(context)
            .load(R.drawable.placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}