package com.example.safetify_test.ui.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Alarm"
    }
    val text: LiveData<String> = _text
}