package com.example.mms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.model.SpecificDaysHourWeight
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

class RecapSpecificDaysAdapter(
    private val context: Context,
    private val specificDaysHourWeightList: List<SpecificDaysHourWeight>
) :
    RecyclerView.Adapter<RecapSpecificDaysAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDay: TextView = itemView.findViewById(R.id.recap_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recap_specificdays, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = specificDaysHourWeightList[position]

        val dayOfWeek = DayOfWeek.of(item.day + 1)
        holder.textDay.text = "${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${item.hourWeight!!.hour} - ${item.hourWeight!!.weight}"
    }

    override fun getItemCount(): Int {
        return this.specificDaysHourWeightList.size
    }
}
