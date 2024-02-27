package com.example.mms.Utils

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.example.mms.R

class LoaderDialog(
    private val text: String,
    private val context: Context
) {
    lateinit var dialog: Dialog

    fun show() {
        this.dialog = Dialog(this.context)
        this.dialog.setContentView(R.layout.custom_dialog_loader)

        val textView = this.dialog.findViewById<TextView>(R.id.loader_text)
        textView.text = this.text

        this.dialog.show()
    }

    fun dismiss() {
        this.dialog.dismiss()
    }
}
