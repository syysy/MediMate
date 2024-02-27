package com.example.mms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.Utils.truncString
import com.example.mms.model.Doctor
import com.example.mms.model.Interaction


class InteractionsAdapter(
    private val items: List<Interaction>
) : RecyclerView.Adapter<InteractionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_interaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.interactionMedicineName.text = item.substanceName
        holder.interactionType.text = item.type
        holder.interactionMessage.text = item.message
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val interactionMedicineName: TextView = itemView.findViewById<TextView>(R.id.interaction_medicine_name)
        val interactionType: TextView = itemView.findViewById(R.id.interaction_type)
        val interactionMessage: TextView = itemView.findViewById(R.id.interaction_message)
    }

    override fun getItemCount() = items.size
}
