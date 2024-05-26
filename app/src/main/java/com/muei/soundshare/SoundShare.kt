package com.muei.soundshare

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.muei.soundshare.di.soundShareModule
import com.muei.soundshare.util.Constants
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SoundShare : Application() {

    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SoundShare)
            modules(soundShareModule)
        }

        val isDarkModeEnabled = sharedPreferences.getBoolean(Constants.DARK_MODE, false)

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }

}