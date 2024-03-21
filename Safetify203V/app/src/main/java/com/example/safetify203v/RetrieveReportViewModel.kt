package com.example.safetify203v
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RetrieveReportViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Submitted Reports:"

    }
    val text: LiveData<String> = _text

}