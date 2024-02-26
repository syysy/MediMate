package com.example.mms.database.jsonMedicines

import android.content.Context
import com.example.mms.dao.MedicineDAO
import com.example.mms.dao.SideEffectsDAO
import com.example.mms.model.SideEffects
import com.example.mms.model.medicines.Medicine
import com.google.gson.Gson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * This class is used to transfer the json database into the room database
 *
 * @property context The context of the application
 * @property dbMedicineFileName The name of the json file
 */
class MedicineJsonDatabase(
    val context: Context
) {
    private val dbMedicineFileName = "databases/medicines_flat.json"
    private val dbSideEffectsFileName = "databases/side_effects.json"
    private val dbInteractionFileName = "databases/interactions.json"

    /**
     * Transfers the medicines json database into the room database
     */
    fun transferMedicinesJsonDBintoRoom(db: MedicineDAO) {
        val medicines = this.getMedicinesFromJson()
        db.insertMany(medicines)
    }


    /**
     * Returns the medicines from the json database
     *
     * @return The medicines from the json database
     */
    private fun getMedicinesFromJson(): List<Medicine> {
        return Json{ ignoreUnknownKeys = true }.decodeFromString<List<Medicine>>(this.getJsonContent(this.dbMedicineFileName))
    }

    /**
     * Returns the side effects from the json database
     */
    private fun getSideEffects(): List<SideEffects> {
        return Json.decodeFromString<List<SideEffects>>(this.getJsonContent(this.dbSideEffectsFileName))
    }

    /**
     * Returns the interactions from the json database
     */
    fun getInteractions(): Map<String, Map<String, Map<String, String>>> {
        return Json.decodeFromString(this.getJsonContent(this.dbInteractionFileName))
    }

    /**
     * Get the json content from the file
     */
    private fun getJsonContent(fileName: String): String {
        return this.context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }
}
