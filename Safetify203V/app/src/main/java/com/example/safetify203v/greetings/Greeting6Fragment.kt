package com.example.safetify203v.greetings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.safetify203v.R
import com.example.safetify203v.SecondActivity
import com.example.safetify203v.databinding.FragmentGreeting6Binding
import android.content.Context
import android.widget.Button


class Greeting6Fragment : Fragment() {
    private lateinit var binding : FragmentGreeting6Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGreeting6Binding.inflate(inflater, container, false)

        //button to Home fragment
        //binding.btnGo.setOnClickListener{
        //    val intent = Intent(context, SecondActivity::class.java)
        //    startActivity(intent)

        //}

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val btn: Button = requireActivity().findViewById(R.id.btnGo)

        btn.setOnClickListener {
            val intent = Intent(requireContext(), SecondActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {

    }
}