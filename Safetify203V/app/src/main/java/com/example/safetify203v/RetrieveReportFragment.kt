package com.example.safetify203v

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safetify203v.databinding.FragmentRetrieveReportBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class RetrieveReportFragment : Fragment() {

    private var _binding: FragmentRetrieveReportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // default code for setting up viewmodel, binding, and text for the textView
        val retrieveReportViewModel =
            ViewModelProvider(this).get(RetrieveReportViewModel::class.java)

        _binding = FragmentRetrieveReportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        retrieveReportViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Initialize the RecyclerView and its adapter with empty data
        val recyclerView: RecyclerView = binding.documentRecycler
        val recyclerViewAdapter = RecyclerViewAdapter(emptyList())

        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // stuff related to firebase
        // creating connection to firebase (this can store most data but images)
        val db = Firebase.firestore

        val collectionRef = db.collection("Case_Reports")
        collectionRef.addSnapshotListener{ value, e ->
            if (e != null){
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }



            // create a list of RecyclerViewData
            var recyclerViewData = mutableListOf<RecyclerViewData>()

            for (doc in value!!){

                val name = (doc.getString("first_name") ?: "") + " " + (doc.getString("last_name") ?: "")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val timestamp = doc.getTimestamp("date_time")?.toDate()
                val date = dateFormat.format(timestamp)
                val contact = "Contact: " + (doc.getString("contact_information")?:"")
                val ic = "IC No.: " + (doc.getString("ic")?:"")
                val location = "Location Description: " + (doc.getString("location_description")?:"")
                val case = "Case Description: " + (doc.getString("case_description")?:"")
                val gender = "Gender: " + (doc.getString("gender")?:"")
                val ethnic = "Ethnicity: " + (doc.getString("ethnicity")?:"")
                recyclerViewData = (recyclerViewData + RecyclerViewData(name, date, contact, ic, location, case, gender, ethnic)).toMutableList()

                recyclerViewAdapter.updateData(recyclerViewData)

            }

            // create a vertical layout manager
            val context = requireContext()
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            // create instance of RecyclerViewAdapter
            val recyclerViewAdapter = RecyclerViewAdapter(recyclerViewData)

            // attach the adapter to the recycler view
            recyclerView.adapter = recyclerViewAdapter

            // also attach the layout manager
            recyclerView.layoutManager = layoutManager

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    layoutManager.orientation
                )
            )

            // make the adapter the data set
            // changed for the recycler view
            recyclerViewAdapter.notifyDataSetChanged()


        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}