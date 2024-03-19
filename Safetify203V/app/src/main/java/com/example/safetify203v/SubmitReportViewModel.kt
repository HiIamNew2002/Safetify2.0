package com.example.safetify203v

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SubmitReportViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Create New Report Below:"
    }
    val text: LiveData<String> = _text
}