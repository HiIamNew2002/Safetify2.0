package com.example.safetify_test.ui.home

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safetify_test.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.example.safetify_test.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        //retrieving message token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            Log.d(TAG,"FM working")
            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            val db = Firebase.firestore

            // creating new document
            val data = hashMapOf(
                "token" to token,
            )
            Log.d(TAG, "DB connected")
            db.collection("Device_tokens")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
        })
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