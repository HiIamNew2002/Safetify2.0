package com.example.safetify201v.ui.greetings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.safetify201v.R
import com.example.safetify201v.databinding.FragmentGreeting2Binding


class Greeting2Fragment : Fragment() {
    private lateinit var binding : FragmentGreeting2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGreeting2Binding.inflate(inflater, container, false)

        //button to next fragment
        binding.ivNext.setOnClickListener{
            it.findNavController().navigate(R.id.action_greeting2Fragment_to_greeting3Fragment2)
        }

        return binding.root
    }

    companion object {

    }
}