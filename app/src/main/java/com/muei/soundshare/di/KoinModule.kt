package com.muei.soundshare.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.muei.soundshare.services.SpotifyClient
import com.muei.soundshare.util.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val soundShareModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
    }

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    single<FirebaseFirestore> {
        Firebase.firestore
    }

    single<FirebaseStorage> {
        FirebaseStorage.getInstance()
    }

    single<SpotifyClient> {
        SpotifyClient()
    }
}