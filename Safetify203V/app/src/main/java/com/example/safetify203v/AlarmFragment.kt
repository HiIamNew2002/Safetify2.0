package com.example.safetify203v

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



class AlarmFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        val intent = Intent(requireContext(), Switcher::class.java)
        startActivity(intent)
        //val switcher = Switcher()
        //switcher.setForSwitching()

        return view
    } // code that meant to call switcher
}




