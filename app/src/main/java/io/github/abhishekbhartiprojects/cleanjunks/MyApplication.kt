package io.github.abhishekbhartiprojects.cleanjunks

import android.app.Application

class MyApplication: Application() {

    companion object {
        private var sInstance: MyApplication? = null

        fun getInstance(): Application? {
            return sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }
}