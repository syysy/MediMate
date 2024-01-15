package com.example.mms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.adapter.Interface.OnItemClickListener
import com.example.mms.R
import com.example.mms.Utils.cryptEmail
import com.example.mms.model.User

class AccountAdapter(private val context: Context, private val users: List<User>) :
    RecyclerView.Adapter<AccountAdapter.MyViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById<TextView>(R.id.account_name)
        val userEmail: TextView = itemView.findViewById<TextView>(R.id.account_email)
        val userImageFingerprint : ImageView = itemView.findViewById<ImageView>(R.id.account_fingerprint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = users[position]
        "${user.name} ${user.surname}".also { holder.userName.text = it }
        holder.userEmail.text = cryptEmail(user.email)
        holder.userImageFingerprint.visibility = if (user.isLinkedToBiometric) View.VISIBLE else View.INVISIBLE
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}
