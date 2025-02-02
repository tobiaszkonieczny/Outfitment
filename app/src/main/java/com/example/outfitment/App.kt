package com.example.outfitment

import android.app.Application
import android.util.Log

class App : Application() {
    lateinit var modelService: ModelService

    override fun onCreate() {
        super.onCreate()
        modelService = ModelService(this)
    }
}