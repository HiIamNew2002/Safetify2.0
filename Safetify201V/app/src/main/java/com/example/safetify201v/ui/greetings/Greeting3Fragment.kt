package com.example.safetify201v.ui.greetings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.safetify201v.R
import com.example.safetify201v.databinding.FragmentGreeting2Binding
import com.example.safetify201v.databinding.FragmentGreeting3Binding

class Greeting3Fragment : Fragment() {
    private lateinit var binding : FragmentGreeting3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGreeting3Binding.inflate(inflater, container, false)

        //button to next fragment
        binding.ivNext.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting3Fragment_to_greeting4Fragment2)
        }

        //button to next fragment
        binding.ivPrevious.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting3Fragment_to_greeting2Fragment)
        }

        return binding.root
    }

    companion object {

    }
}