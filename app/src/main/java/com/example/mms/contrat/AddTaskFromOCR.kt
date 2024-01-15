package com.example.mms.contrat

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.mms.ui.add.AddMedicamentActivity

class AddTaskFromOCR : ActivityResultContract<String, Long>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, AddMedicamentActivity::class.java)
            .putExtra("medicineName", input).putExtra("fromOcr", true)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Long {
        var res = -1L
        if (resultCode == Activity.RESULT_OK) {
            res = intent!!.getLongExtra("taskId", -1L)
        }
        return res
    }
}