package com.example.safetify203v

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.safetify203v.databinding.FragmentVideocallBinding

class VideoCallFragment: Fragment() {

    private var _binding: FragmentVideocallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideocallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assuming you have a Button with the ID btnRecord in your fragment_call.xml layout
        binding.btnEndcall.setOnClickListener {
            // Replace CallFragment with RecordingFragment
            replaceFragment(CallFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // You can customize the transition animations if needed
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
        transaction.commit()
    }
}
