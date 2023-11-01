package com.example.safetify201v.ui.greetings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safetify201v.databinding.FragmentGreeting5Binding

class Greeting5Fragment : Fragment() {
    private lateinit var binding : FragmentGreeting5Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGreeting5Binding.inflate(inflater, container, false)

        return binding.root
    }
    companion object {

    }
}