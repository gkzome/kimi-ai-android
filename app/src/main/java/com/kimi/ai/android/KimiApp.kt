package com.kimi.ai.android

import android.app.Application

class KimiApp : Application() {
    companion object {
        lateinit var instance: KimiApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
