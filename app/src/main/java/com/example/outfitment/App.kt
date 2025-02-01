package com.example.outfitment

import android.app.Application

class App : Application() {
    lateinit var modelService: ModelService

    override fun onCreate() {
        super.onCreate()
        modelService = ModelService(this)
    }
}