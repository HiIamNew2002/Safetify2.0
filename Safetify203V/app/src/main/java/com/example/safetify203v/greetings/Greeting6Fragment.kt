package com.example.safetify203v.greetings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.safetify203v.R
import com.example.safetify203v.SignUpActivity
import com.example.safetify203v.databinding.FragmentGreeting6Binding


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
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {

    }
}