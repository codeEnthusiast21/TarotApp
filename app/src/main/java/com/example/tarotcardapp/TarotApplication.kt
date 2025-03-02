package com.example.tarotcardapp


import android.app.Application
import com.example.tarotcardapp.model.local.AppDatabase

class TarotApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize database
        AppDatabase.getDatabase(this)
    }
}