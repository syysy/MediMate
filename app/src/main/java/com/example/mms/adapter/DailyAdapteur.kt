package com.example.mms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.model.DairyNote

/**
 * Adapter for the recycler view of the dairy
 *
 * @param context the context of the activity
 * @param items the list of item
 */
class DailyAdapteur (
    private val context: Context,
    private val items: MutableList<DairyNote>
): RecyclerView.Adapter<DailyAdapteur.MyViewHolder>() {

    /**
     * Class that represents the view holder of the recycler view
     * @param itemView the view
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textJournal: TextView = itemView.findViewById(R.id.journal_text)
    }

    /**
     * Function that creates the view holder
     * @param parent the parent view
     * @param viewType the view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapteur.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_dairy, parent, false)
        return DailyAdapteur.MyViewHolder(view)
    }

    /**
     * Function that returns the number of items
     */
    override fun getItemCount(): Int {
        return this.items.size
    }

    /**
     * Function that binds the view holder
     * @param holder the view holder
     * @param position the position of the item
     */
    override fun onBindViewHolder(holder: DailyAdapteur.MyViewHolder, position: Int) {
        val item = this.items[position]

        holder.textJournal.text = item.note
    }
}