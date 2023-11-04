package com.example.safetify_test.ui.alarm

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safetify_test.R
import com.example.safetify_test.databinding.FragmentAlarmBinding

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private var mediaPlayer: MediaPlayer? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[AlarmViewModel::class.java]

        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAlarm
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm)
        mediaPlayer?.setVolume(1.0f, 1.0f) // Max volume

        binding.alarmButton.setOnClickListener {
            Log.d(TAG, "button working: "+mediaPlayer.toString())
            mediaPlayer?.start()
        }

        binding.haltButton.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}