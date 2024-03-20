package com.example.safetify203v

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safetify203v.R

class RecyclerViewAdapter (private var items: List<RecyclerViewData>) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewDataHolder>() {

    inner class RecyclerViewDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewDataHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_content, parent, false)
        return RecyclerViewDataHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewDataHolder, position: Int) {
        val currentItem: RecyclerViewData = items[position]

        val documentNumber: TextView = holder.itemView.findViewById(R.id.documentNumber)
        documentNumber.text = currentItem.text1

        val documentNumbersInText: TextView = holder.itemView.findViewById(R.id.documentNumbersInText)
        documentNumbersInText.text = currentItem.text2

        val contactInfo: TextView = holder.itemView.findViewById(R.id.contactInfo)
        contactInfo.text = currentItem.contact

        val ic: TextView = holder.itemView.findViewById(R.id.ic)
        ic.text = currentItem.ic

        val location: TextView = holder.itemView.findViewById(R.id.location)
        location.text = currentItem.location

        val description: TextView = holder.itemView.findViewById(R.id.description)
        description.text = currentItem.case

        val gender: TextView = holder.itemView.findViewById(R.id.gender)
        gender.text = currentItem.gender

        val ethnic: TextView = holder.itemView.findViewById(R.id.ethnic)
        ethnic.text = currentItem.ethnic

    }
    fun updateData(newData: List<RecyclerViewData>) {
        items = newData
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return items.size
    }
}