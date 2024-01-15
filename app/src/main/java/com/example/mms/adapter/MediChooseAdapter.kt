package com.example.mms.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.Utils.OCR
import com.example.mms.adapter.Interface.OnItemClickListener
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.service.TasksService
import java.time.Period

class MediChooseAdapter(
    private val context: Context,
    private val db : AppDatabase,
    private val listMedicament: List<OCR.MedicationInfo>,
    private val listTaskId: HashMap<Int,Long>

) : RecyclerView.Adapter<MediChooseAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private var taskService = TasksService(context)

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicamentName: TextView = itemView.findViewById(R.id.medicamentName)
        val medicamentQuantity: TextView = itemView.findViewById(R.id.medicamentQuantity)
        val medicamentFrequency: TextView = itemView.findViewById(R.id.medicamentFrequency)
        val medicamentDuration: TextView = itemView.findViewById(R.id.medicamentDuration)
        val medicamentCheckBox: CheckBox = itemView.findViewById(R.id.medicamentCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_choose_medicament, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicament = listMedicament[position]
        val taskId = listTaskId.getOrDefault(position, -1L)
        holder.medicamentName.text = medicament.name
        holder.medicamentQuantity.text = medicament.dosage
        holder.medicamentCheckBox.isClickable = false
        if (taskId != -1L) {
            val tt = Thread {
                val task = this.db.taskDao().getTask(taskId)
                val taskFilled =  this.taskService.getTaskFilled(task)
                if (taskFilled != null) {
                    holder.medicamentFrequency.text = taskFilled.type
                    val nbDays = (Period.between(taskFilled.startDate.toLocalDate(), taskFilled.endDate.toLocalDate()).days+1).toString()
                    holder.medicamentDuration.text = this.context.getString(R.string.nombre_jours, nbDays)
                }
            }
            tt.start()
            tt.join()
            holder.medicamentCheckBox.isChecked = true
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listMedicament.size
    }
}
