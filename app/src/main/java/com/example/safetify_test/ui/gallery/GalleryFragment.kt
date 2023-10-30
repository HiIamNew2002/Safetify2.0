package com.example.safetify_test.ui.gallery

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safetify_test.databinding.FragmentGalleryBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // default code for setting up viewmodel, binding, and content of text view
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

// gathering input of the user
        //reference to the confirmation button
        val confirmButton = binding.confirm

        confirmButton.setOnClickListener {
            // Find the views by their IDs
            val firstNameEditText = binding.firstNameInput
            val lastNameEditText = binding.lastNameInput
            val datePicker = binding.datePicker
            val timePicker = binding.timePicker
            val contactEditText = binding.contactInput
            val icEditText = binding.icInput
            val locationEditText = binding.locationInput
            val descriptionEditText = binding.descriptionInput

            // Retrieve user inputs from text fields
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val contact = contactEditText.text.toString()
            val ic = icEditText.text.toString()
            val location = locationEditText.text.toString()
            val description = descriptionEditText.text.toString()

            // Get the date and time from DatePicker and TimePicker
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val hour = timePicker.hour
            val minute = timePicker.minute


            val date_time = Timestamp(year-1900, month, day, hour, minute, 0, 0)

            // connecting to firebase
            val db = Firebase.firestore

            // creating new document
            val data = hashMapOf(
                "first_name" to firstName,
                "last_name" to lastName,
                "contact_information" to contact,
                "ic" to ic,
                "location_description" to location,
                "case_description" to description,
                "date_time" to date_time,
            )

            db.collection("Case_Reports")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }




        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}