package com.example.mms.dao

import android.content.Context
import com.example.mms.database.jsonMedicines.MedicineJsonDatabase
import com.example.mms.model.medicines.Medicine

class InteractionDao(context: Context) {
    private val interactions = MedicineJsonDatabase(context).getInteractions()

    fun thisMedicineInteractsWith(medicine: Medicine, othersMedicines: List<Medicine>): Map<String, Map<String, String>> {

        var medicineInteractions: Map<String, Map<String, String>>? = null
        for (key in this.interactions.keys) {
            if (medicine.name.contains(key, ignoreCase = true) ||
                (medicine.composition?.substance_name ?: "").contains(key, ignoreCase = true)) {
                medicineInteractions = this.interactions[key]
                break
            }
        }

        if (medicineInteractions == null) {
            return emptyMap()
        }

        return medicineInteractions.filterKeys {it ->
            it in othersMedicines.map { it.composition?.substance_name ?: it.name }
        }
    }
}
