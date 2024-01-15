package com.example.mms.database.jsonMedicines

import android.content.Context
import com.example.mms.dao.MedicineDAO
import com.example.mms.model.medicines.GroupOfMedicines
import com.example.mms.model.medicines.Medicine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class MedicineJsonDatabase(
    val context: Context
) {
    private val dbFileName = "databases/medicines_flat.json"

    fun transferJsonDBintoRoom(db: MedicineDAO) {
        val medicines = this.getMedicinesFromJson()
        db.insertMany(medicines)
    }

    fun getMedicinesFromJson(): List<Medicine> {
        return Json.decodeFromString<List<Medicine>>(this.getJsonContent(this.dbFileName))
    }

    private fun getJsonContent(fileName: String): String {
        return this.context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }
}
