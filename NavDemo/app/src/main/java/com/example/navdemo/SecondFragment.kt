package com.example.navdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navdemo.databinding.FragmentHomeBinding
import com.example.navdemo.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MyTAG", "Second Create")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("MyTAG", "Second Create View")
        binding = FragmentSecondBinding.inflate(inflater,container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

}
