package com.example.mms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.adapter.Interface.OnItemClickListener
import com.example.mms.R
import com.example.mms.model.HourWeight

class HourWeightAdapter(
    private val context: Context,
    var hourWeightList: MutableList<HourWeight>
) : RecyclerView.Adapter<HourWeightAdapter.MyViewHolder>() {

    private var itemClickListenerTP: OnItemClickListener? = null

    fun setOnItemClickListener(listenerTimePicker: OnItemClickListener) {
        itemClickListenerTP = listenerTimePicker
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeure = itemView.findViewById<TextView>(R.id.text_timepicker)
        val npDosage = itemView.findViewById<NumberPicker>(R.id.np_dosage)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btn_delete)

        init {
            npDosage.minValue = 1
            npDosage.maxValue = 50
            npDosage.wrapSelectorWheel = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_heure_dosage, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.hourWeightList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvHeure.text = this.hourWeightList[position].hour
        holder.npDosage.value = this.hourWeightList[position].weight

        holder.npDosage.setOnValueChangedListener { picker, oldVal, newVal ->
            this.hourWeightList[position].weight = newVal
        }

        holder.tvHeure.setOnClickListener {
            this.itemClickListenerTP?.onItemClick(position)
        }

        holder.btnDelete.setOnClickListener {
            this.deleteItem(position)
        }
    }

    private fun deleteItem(position: Int) {
        this.hourWeightList.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyItemRangeChanged(position, this.hourWeightList.size)
    }
}
