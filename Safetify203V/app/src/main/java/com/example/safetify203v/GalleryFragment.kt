package com.example.safetify203v

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.safetify203v.databinding.FragmentGalleryBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryFragment : Fragment(), OnItemClickListener {

    //binding
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var records : ArrayList<AudioRecord>
    private lateinit var myAdapter : Adapter
    private lateinit var db : AppDatabase

    private var allChecked = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view = binding.root

        records = ArrayList()

        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "audioRecords").build()

        myAdapter = Adapter(records, this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.recyclerview.apply{
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }

        fetchAll()

        binding.searchInput.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var query = s.toString()
                searchDatabase(query)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        //Close in the Edit mode
        binding.btnClose.setOnClickListener{
           leaveEditMode()
        }

        //Select All the checkbox
        binding.btnSelectAll.setOnClickListener{
            allChecked = !allChecked
            records.map { it.isChecked = allChecked }
            myAdapter.notifyDataSetChanged()

            if(allChecked){
                disableRename()
                enableDelete()
            }else{
                disableRename()
                disableDelete()
            }
        }

        binding.btnDelete.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            builder.setTitle("Delete record?")
            val nbRecords = records.count{it.isChecked}
            builder.setMessage("Are you sure you want to delete $nbRecords record(s) ?")

            builder.setPositiveButton("Delete"){_, _ ->
                val toDelete = records.filter{it.isChecked }.toTypedArray()
                GlobalScope.launch{
                    db.audioRecordDao().delete(toDelete)
                    requireActivity().runOnUiThread{
                        records.removeAll(toDelete)
                        myAdapter.notifyDataSetChanged()
                        leaveEditMode()
                    }
                }
            }

            builder.setNegativeButton("Cancel"){_, _ ->
                //it does nothing
            }

            val dialog = builder.create()
            dialog.show()
        }

        binding.btnEdit.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = this.layoutInflater.inflate(R.layout.rename_layout, null)
            builder.setView(dialogView)
            val dialog = builder.create()

            val record = records.filter {  it.isChecked }.get(0)
            val textInput = dialogView.findViewById<TextInputEditText>(R.id.filenameInput)
            textInput.setText(record.filename)

            dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener{
                val input = textInput.text.toString()
                if(input.isEmpty()){
                    Toast.makeText(requireContext(), "A name is required", Toast.LENGTH_LONG).show()
                }else{
                    record.filename = input
                    GlobalScope.launch{
                        db.audioRecordDao().update(record)
                        requireActivity().runOnUiThread{
                            myAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                            leaveEditMode()
                        }
                    }
                }
            }

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                dialog.dismiss()
            }


            dialog.show()
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun leaveEditMode(){
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.editBar.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        records.map { it.isChecked = false }
        myAdapter.setEditMode(false)
    }

    private fun disableRename(){
        binding.btnEdit.isClickable = false
        binding.btnEdit.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.greyDarkDisabled, requireContext().theme)
        binding.tvEdit.setTextColor(ResourcesCompat.getColorStateList(resources, R.color.greyDarkDisabled, requireContext().theme))
    }

    private fun disableDelete(){
        binding.btnDelete.isClickable = false
        binding.btnDelete.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.greyDarkDisabled, requireContext().theme)
        binding.tvDelete.setTextColor(ResourcesCompat.getColorStateList(resources, R.color.greyDarkDisabled, requireContext().theme))
    }

    private fun enableRename(){
        binding.btnEdit.isClickable = true
        binding.btnEdit.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.greyDark, requireContext().theme)
        binding.tvEdit.setTextColor(ResourcesCompat.getColorStateList(resources, R.color.greyDark, requireContext().theme))
    }

    private fun enableDelete(){
        binding.btnDelete.isClickable = true
        binding.btnDelete.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.greyDark, requireContext().theme)
        binding.tvDelete.setTextColor(ResourcesCompat.getColorStateList(resources, R.color.greyDark, requireContext().theme))
    }

    private fun searchDatabase(query:String){
        GlobalScope.launch(Dispatchers.IO) {
            records.clear()
            val queryResult = db.audioRecordDao().searchDatabase("%$query%")
            records.addAll(queryResult)

            withContext(Dispatchers.Main) {
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchAll() {
        GlobalScope.launch(Dispatchers.IO) {
            records.clear()
            val queryResult = db.audioRecordDao().getAll()
            records.addAll(queryResult)

            withContext(Dispatchers.Main) {
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClickListener(position: Int) {
        var audioRecord = records[position]
        val audioPlayerFragment = AudioPlayerFragment()

        if(myAdapter.isEditMode()){
            records[position].isChecked = !records[position].isChecked
            myAdapter.notifyItemChanged(position)

            var nbSelected = records.count{it.isChecked}
            when(nbSelected){
                0 -> {
                    disableRename()
                    disableDelete()
                }
                1 -> {
                    enableDelete()
                    enableRename()
                }
                else -> {
                    disableRename()
                    enableDelete()
                }
            }
        }else{
            // Pass data to the fragment using arguments
            val args = Bundle()
            args.putString("filepath", audioRecord.filePath)
            args.putString("filename", audioRecord.filename)
            audioPlayerFragment.arguments = args

            // Start the fragment transaction
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new one
            transaction.replace(R.id.fragment_container, audioPlayerFragment)
            transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
            transaction.commit()
        }



    }

    override fun onItemLongClickListener(position: Int) {
        myAdapter.setEditMode(true)
        records[position].isChecked = !records[position].isChecked
        myAdapter.notifyItemChanged(position)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        if(myAdapter.isEditMode() && binding.editBar.visibility == View.GONE){
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)

            binding.editBar.visibility = View.VISIBLE

            enableDelete()
            enableRename()
        }
    }


}