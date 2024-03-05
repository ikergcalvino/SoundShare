package com.muei.soundshare.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is signup Fragment"
    }
    val text: LiveData<String> = _text
}