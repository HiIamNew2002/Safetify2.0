package com.example.safetify_test.ui.home

import android.content.ContentValues
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.safetify_test.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Submitted Reports:"

    }
    val text: LiveData<String> = _text

}