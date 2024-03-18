package com.example.safetify203v

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.safetify203v.databinding.FragmentAudioPlayerBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlayerFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: FragmentAudioPlayerBinding

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private var delay = 1000L
    private var jumpValue = 1000

    private var playbackSpeed = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve arguments
        val filePath = arguments?.getString("filepath")
        val filename = arguments?.getString("filename")

        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Intercept back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mediaPlayer.stop()
            mediaPlayer.release()
            handler.removeCallbacks(runnable)

        }

        binding.tvFilename.text = filename

        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(filePath)
            prepare()
        }

        binding.tvTrackDuration.text = dateFormat(mediaPlayer.duration)


        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            binding.seekBar.progress = mediaPlayer.currentPosition
            binding.tvTrackProgress.text = dateFormat(mediaPlayer.currentPosition)
            handler.postDelayed(runnable, delay)
        }

        binding.btnPlay.setOnClickListener {
            playPausePlayer()
        }

        playPausePlayer()
        binding.seekBar.max = mediaPlayer.duration

        // Change the button to play when finish
        mediaPlayer.setOnCompletionListener {
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.audio_play_circle, requireContext().theme)
        }

        binding.btnForward.setOnClickListener{
            mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpValue)
            binding.seekBar.progress += jumpValue
        }

        binding.btnBackward.setOnClickListener{
            mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpValue)
            binding.seekBar.progress -= jumpValue
        }

        binding.chip.setOnClickListener{
            if(playbackSpeed != 2f)
                playbackSpeed +=0.5f
            else
                playbackSpeed = 0.5f

            mediaPlayer.playbackParams = PlaybackParams().setSpeed(playbackSpeed)
            binding.chip.text = "x $playbackSpeed"
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                    mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?)  {}

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun playPausePlayer(){
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.audio_pause_circle, requireContext().theme)
            handler.postDelayed(runnable,delay)
        }else{
            mediaPlayer.pause()
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.audio_play_circle, requireContext().theme)
            handler.removeCallbacks(runnable)
        }
    }

    private fun dateFormat(duration: Int): String{
        var d = duration/1000
        var s = d%60
        var m = (d/60 % 60)
        var h = ((d - m*60)/360).toInt()

        val f: NumberFormat = DecimalFormat("00")
        var str = "$m:${f.format(s)}"

        if(h>0)
            str = "$h:$str"
        return str
    }

}
