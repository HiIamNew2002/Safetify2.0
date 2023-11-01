package com.example.safetify201v.ui.greetings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.safetify201v.R
import com.example.safetify201v.databinding.FragmentGreeting1Binding
import com.google.android.material.bottomnavigation.BottomNavigationView


class Greeting1Fragment : Fragment(){
    private lateinit var binding : FragmentGreeting1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentGreeting1Binding.inflate(inflater, container, false)

        //button to next fragment
        binding.btnStart.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting1Fragment_to_greeting2Fragment2)
        }

        return binding.root


    }


    companion object {

    }
}