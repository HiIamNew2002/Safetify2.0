package com.example.safetify_test.ui.call

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CallViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Call Fragment"
    }
    val text: LiveData<String> = _text
}