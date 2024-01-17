package com.example.mms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.model.ShowableTakes
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

//
class TakesHistoryAdapter (
    private val context: Context,
    private val items: MutableList<ShowableTakes>
): RecyclerView.Adapter<TakesHistoryAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicine_name)
        val medicineDoseForme: TextView = itemView.findViewById(R.id.medicine_dose_forme)
        val takeAtDate: TextView = itemView.findViewById(R.id.heure_prise)
        val takeAtHour: TextView = itemView.findViewById(R.id.date_prise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_takes_history, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = this.items[position]

        holder.medicineName.text = item.medicineName
        holder.medicineDoseForme.text = this.context.getString(R.string.doses_weight_type, item.weight.toString(), item.medicineType)

        val hourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        val hourString = item.takeAt.format(hourFormatter)

        holder.takeAtHour.text = hourString

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val dateString = item.takeAt.format(dateFormatter)

        holder.takeAtDate.text = dateString

    }
}
