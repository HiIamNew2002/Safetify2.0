package com.example.viewmodelpractise

import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    var count = 0

    fun updateCount(){
        ++count
    }
}