package com.example.safetify203v

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.safetify203v.databinding.FragmentThirdpartyBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class ThirdpartyFragment : Fragment() {

    private var _binding: FragmentThirdpartyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // default code for setting up viewmodel, binding, and content of text view
        val submitReportViewModel = ViewModelProvider(this).get(SubmitReportViewModel::class.java)

        _binding = FragmentThirdpartyBinding.inflate(inflater, container, false)

        binding.btnBack2.setOnClickListener {
            // Replace CallFragment with RecordingFragment
            replaceFragment(ReportFragment())
        }

        // gathering input of the user
        //reference to the confirmation button
        val confirmButton = binding.btnSubmit

        confirmButton.setOnClickListener {
            // Find the views by their IDs
            val reportTypeText = binding.reportType
            val phoneNumber = binding.phone
            val datePicker = binding.datePicker
            val timePicker = binding.timePicker
            val locationText = binding.locationInput
            val descriptionEditText = binding.descriptionInput
            val evidenceText = binding.evidenceInput

            // Retrieve user inputs from text fields
            val reportType = reportTypeText.text.toString()
            val contact = phoneNumber.text.toString()
            val location = locationText.text.toString()
            val description = descriptionEditText.text.toString()
            val evidence = evidenceText.text.toString()
            val status = if(binding.contactCheckbox.isChecked) "Wants to be contacted" else "Don't want to be contacted"

            // Get the date and time from DatePicker and TimePicker
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val hour = timePicker.hour
            val minute = timePicker.minute


            val date_time = Timestamp(year - 1900, month, day, hour, minute, 0, 0)


            // connecting to firebase
            val db = Firebase.firestore

            // creating new document
            val data = hashMapOf(
                "report_type" to reportType,
                "contact_information" to contact,
                "location_description" to location,
                "case_description" to description,
                "date_time" to date_time,
                "evidence" to evidence,
                "status" to status,

            )

            db.collection("Third_Party_Reports")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            // Show submission confirmation
            val dialog = SubmissionConfirmation()
            dialog.show(parentFragmentManager, "SubmissionConfirmationDialog")

            //clear all text after submission to the firestore
            reportTypeText.text?.clear()
            phoneNumber.text?.clear()
            locationText.text?.clear()
            descriptionEditText.text?.clear()
            evidenceText.text?.clear()

        }
        //end of onClickListener

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // You can customize the transition animations if needed
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}