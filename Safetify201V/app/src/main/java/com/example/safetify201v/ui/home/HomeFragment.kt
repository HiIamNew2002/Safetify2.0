package com.example.safetify201v.ui.home

import android.R
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safetify201v.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var button: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvLogoName
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val button: Button = binding.btnPanic
        var isButtonHeld = false
        var notified = true
        var notifyingHandler: Handler? = null

        button.setOnClickListener {

            // Change the text to "Hold" when the button is clicked
            if(notified) {
                button.text = "Hold"
                notified = false
            }
        }

        button.setOnLongClickListener {
            // Button is long-pressed

                isButtonHeld = true
                notifyingHandler = Handler()
                notifyingHandler?.postDelayed({
                    if (isButtonHeld) {
                        // Change the text to "Notifying" after holding for a certain duration
                        button.text = "Notifying"
                        notified = false
                        // Add your code to notify contacts here
                    }
                }, 2000) // Adjust the duration as needed (e.g., 3000 milliseconds for 3 seconds)

            true
        }

        return root
    }

     override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}