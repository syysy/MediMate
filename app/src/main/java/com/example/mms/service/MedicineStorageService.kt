package com.example.mms.service

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.adapter.TakesAdapter
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.MedicineStorage
import com.google.android.material.snackbar.Snackbar

class MedicineStorageService(val context : Context, val view : View) {
    private var db = SingletonDatabase.getDatabase(context)

    fun updateMedicineStorage(medicineStorage: MedicineStorage, weight : Int, medicineName : String, takesAdapter: TakesAdapter) : Boolean {
        val res = medicineStorage.storage - weight
        var update = false
        var dismiss = false
        when {
            res < 0 -> {
                // Alert -> not enough storage for this takes -> update storage or cancel takes
                android.os.Handler(Looper.getMainLooper()).post {
                    val dialog = AlertDialog.Builder(context)
                    dialog.setTitle(this.context.getString(R.string.stock_insuffisant))
                    dialog.setMessage(this.context.getString(R.string.vous_avez_plus_beaucoup_doses))
                    dialog.setPositiveButton(this.context.getString(R.string.mettre_jour_stock)) { dialog, _ ->
                        dialogGererStock(medicineStorage, medicineName, takesAdapter)
                        dismiss = true
                        dialog.dismiss()
                    }
                    dialog.setNegativeButton(this.context.getString(R.string.annuler_la_prise)) { dialog, _ ->
                        dismiss = true
                        dialog.dismiss()
                    }
                    dialog.setCancelable(true)
                    dialog.setOnCancelListener { dialog ->
                        dismiss = true
                        dialog.dismiss()
                    }

                    val alertDialog = dialog.create()
                    alertDialog.show()
                }

            }
            res <= medicineStorage.alertValue -> {
                medicineStorage.storage = res
                update = true
                // Alert -> you stock is low -> buy more
                val snackbar = Snackbar.make(
                    view,
                    this.context.getString(R.string.stock_faible_pensez_racheter),
                    Snackbar.LENGTH_LONG)
                snackbar.setAction("X", View.OnClickListener {
                    snackbar.dismiss()
                })
                snackbar.setAction(this.context.getString(R.string.gerer_stock), View.OnClickListener {
                    dialogGererStock(medicineStorage, medicineName, takesAdapter)
                    snackbar.dismiss()
                })
                snackbar.setBackgroundTint(context.getColor(R.color.white))
                snackbar.setTextColor(context.getColor(R.color.black))

                // Position the snackbar on top
                val view = snackbar.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                params.topMargin = 100
                view.layoutParams = params

                snackbar.show()
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    snackbar.dismiss()
                }, 10000)
            }
            else -> {
                medicineStorage.storage = res
                update = true
            }
        }
        val tt = Thread {
            this.db.medicineStorageDao().insert(medicineStorage)
        }
        if (update) {
            tt.start()
            tt.join()
        }
        return dismiss
    }

    fun dialogGererStock(mStorage: MedicineStorage, medicineName: String, adapter: TakesAdapter) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog_edit_storage)

        val tvMedicineName = dialog.findViewById<TextView>(R.id.tv_info_takes)
        tvMedicineName.text = medicineName
        val editStorage = dialog.findViewById<EditText>(R.id.edit_actual_storage)
        editStorage.setText(mStorage.storage.toString())
        val editAlertValue = dialog.findViewById<EditText>(R.id.edit_alert_storage)
        editAlertValue.setText(mStorage.alertValue.toString())

        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        val btnUpdate = dialog.findViewById<TextView>(R.id.btn_save)
        btnUpdate.setOnClickListener {
            val storage = editStorage.text.toString().toInt()
            val alertValue = editAlertValue.text.toString().toInt()
            mStorage.storage = storage
            mStorage.alertValue = alertValue
            val tt = Thread {
                this.db.medicineStorageDao().update(mStorage)
            }
            tt.start()
            tt.join()
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }
}