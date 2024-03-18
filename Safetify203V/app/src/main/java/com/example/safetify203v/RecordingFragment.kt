package com.example.safetify203v

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import com.example.safetify203v.databinding.FragmentRecordingBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date


const val REQUEST_CODE = 200

class RecordingFragment : Fragment(), Timer.OnTimerTickListener {

    //binding
    private var _binding: FragmentRecordingBinding? = null
    private val binding get() = _binding!!

    //recorder
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false

    //Vibrator
    private lateinit var vibrator: Vibrator

    //Timer
    private lateinit var timer:Timer

    //Bottom sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    //waveform
    private lateinit var amplitudes: ArrayList<Float>
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    //Database
    private lateinit var db : AppDatabase
    private var duration = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordingBinding.inflate(inflater, container, false)

        permissionGranted = ActivityCompat.checkSelfPermission(requireContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
        }

        //initialise database
        db = Room.databaseBuilder(requireContext(),AppDatabase::class.java,"audioRecords").build()

        //Bottom Sheet (Saving the file)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetInclude.bottomSheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        //Timer
        timer = Timer(this)
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        binding.btnRecording.setOnClickListener{
            when{
                isPaused -> resumeRecorder()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }

            vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE))
        }

        binding.btnList.setOnClickListener{
            replaceFragment(GalleryFragment())
        }

        binding.btnDone.setOnClickListener{
            stopRecorder()
            Toast.makeText(requireContext(), "Record saved", Toast.LENGTH_SHORT).show()

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.bottomSheetBG.visibility = View.VISIBLE
            binding.bottomSheetInclude.filenameInput.setText(filename)

        }

        binding.bottomSheetInclude.btnCancel.setOnClickListener{
            File("$dirPath$filename.mp3").delete()
            dismiss()
        }

        binding.bottomSheetInclude.btnOk.setOnClickListener{
            dismiss()
            save()
        }


        binding.bottomSheetBG.setOnClickListener{
            File("$dirPath$filename.mp3").delete()
            dismiss()
        }

        binding.btnDelete.setOnClickListener{
            stopRecorder()
            File("$dirPath$filename.mp3").delete()
            Toast.makeText(requireContext(), "Record deleted", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.isClickable = false

        return binding.root
    }

    private fun save(){
        val newFilename = binding.bottomSheetInclude.filenameInput.text.toString()
        if(newFilename != filename){
            var newFile = File("$dirPath$newFilename.mp3")
            File("$dirPath$filename.mp3").renameTo(newFile)
        }

        var filePath = "$dirPath$newFilename.mp3"
        var timestamp = Date().time
        var ampsPath = "$dirPath$newFilename"

        try{
            var fos = FileOutputStream(ampsPath)
            var out = ObjectOutputStream(fos)
            out.writeObject(amplitudes)
            fos.close()
            out.close()
        }catch (e :IOException){}

        var record = AudioRecord(newFilename, filePath, timestamp, duration, ampsPath)

        GlobalScope.launch {
            db.audioRecordDao().insert(record)
        }

    }

    private fun dismiss(){
        binding.bottomSheetBG.visibility = View.GONE
        hideKeyboard(binding.bottomSheetInclude.filenameInput)

        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
    }

    private fun hideKeyboard(view: View){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun pauseRecorder(){
        recorder.pause()
        isPaused = true
        binding.btnRecording.setImageResource(R.drawable.ic_record)

        timer.pause()
    }

    private fun resumeRecorder(){
        recorder.resume()
        isPaused = false
        binding.btnRecording.setImageResource(R.drawable.record_pause_24)

        timer.start()
    }

    private fun startRecording(){
        if(!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
            return
        }
        //start recording

        recorder = MediaRecorder()
        dirPath = "${requireContext().externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")

            try {
                prepare()
            }catch (e: IOException){

            }

            start()
        }

        binding.btnRecording.setImageResource(R.drawable.record_pause_24)
        isRecording = true
        isPaused = false

        timer.start()

        binding.btnDelete.isClickable = true
        binding.btnDelete.setImageResource(R.drawable.record_delete_24)

        binding.btnList.visibility = View.GONE
        binding.btnDone.visibility = View.VISIBLE
    }

    private fun stopRecorder(){
        timer.stop()

        recorder.apply {
            stop()
            release()
        }

        isPaused = false
        isRecording = false

        binding.btnList.visibility = View.VISIBLE
        binding.btnDone.visibility = View.GONE

        binding.btnDelete.isClickable = false
        binding.btnDelete.setImageResource(R.drawable.record_delete_disabled_24)

        binding.btnRecording.setImageResource(R.drawable.ic_record)

        binding.tvTimer.text = "00.00.00"
        amplitudes = binding.waveformView.clear()


    }

    override fun onTimerTick(duration: String) {
        binding.tvTimer.text = duration
        this.duration = duration.dropLast(3)
        binding.waveformView.addAmplitude(recorder.maxAmplitude.toFloat())
    }

    //Link to Gallery fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // You can customize the transition animations if needed
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
        transaction.commit()
    }

}