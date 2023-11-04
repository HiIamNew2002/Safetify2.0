package com.example.safetify_test.ui.guides


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safetify_test.Test1Activity
import com.example.safetify_test.databinding.FragmentGuidesBinding
import com.example.safetify_test.ui.guides.GuidesViewModel

class GuidesFragment : Fragment() {

    private var _binding: FragmentGuidesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(GuidesViewModel::class.java)

        _binding = FragmentGuidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvGuides
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        // Assuming you are in the first activity
        binding.demoButton.setOnClickListener{
            val intent = Intent(requireContext(), Test1Activity::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}