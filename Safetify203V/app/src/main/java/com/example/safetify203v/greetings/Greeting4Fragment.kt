package com.example.safetify203v.greetings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.safetify203v.R
import com.example.safetify203v.databinding.FragmentGreeting4Binding

class Greeting4Fragment : Fragment() {

    private lateinit var binding : FragmentGreeting4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGreeting4Binding.inflate(inflater, container, false)

        //button to next fragment
        binding.ivNext.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting4Fragment_to_greeting5Fragment)
        }

        //button to next fragment
        binding.ivPrevious.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting4Fragment_to_greeting3Fragment)
        }

        return binding.root
    }

    companion object {

    }
}