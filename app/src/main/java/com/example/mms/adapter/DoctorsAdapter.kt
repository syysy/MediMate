package com.example.mms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.Utils.truncString
import com.example.mms.model.Doctor


class DoctorsAdapter(
    private val items: List<Doctor>,
    private val onItemClicked: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        holder.doctorName.text = truncString(currentItem.getDisplayName(), 30)
        holder.doctorAdress.text = truncString(currentItem.getDisplayAdress(), 70)

        holder.itemView.setOnClickListener {
            this.onItemClicked(currentItem)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.findViewById(R.id.doctors_name)
        val doctorAdress: TextView = itemView.findViewById(R.id.doctors_adress)
    }

    override fun getItemCount() = items.size
}
