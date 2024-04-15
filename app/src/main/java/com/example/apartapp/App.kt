package com.example.apartapp

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }

    companion object {
        const val MAPKIT_API_KEY = "5e7641c5-d637-4e63-9378-2bdf5e9502d6"
    }
}