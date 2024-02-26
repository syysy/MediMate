package com.example.mms.dao

import android.content.Context
import com.example.mms.database.jsonMedicines.MedicineJsonDatabase
import com.example.mms.model.Interaction
import com.example.mms.model.medicines.Medicine

class InteractionDao(context: Context) {
    private val interactions = MedicineJsonDatabase(context).getInteractions()

    fun thisMedicineInteractsWith(medicine: Medicine, othersMedicines: List<Medicine>): List<Interaction> {

        var medicineInteractions: Map<String, Map<String, String>>? = null
        for (key in this.interactions.keys) {
            if (medicine.name.contains(key, ignoreCase = true) ||
                (medicine.composition?.substance_name ?: "").contains(key, ignoreCase = true)) {
                medicineInteractions = this.interactions[key]
                break
            }
        }

        // ACIDE ACETYLSALICYLIQUE
        // CLOPIDOGREL
        if (medicineInteractions == null) {
            return listOf()
        }

        medicineInteractions = medicineInteractions.filterKeys { key ->
            othersMedicines.any {
                it.name.contains(key, ignoreCase = true) ||
                        (it.composition?.substance_name ?: "").contains(key, ignoreCase = true)
            }
        }

        return medicineInteractions.map { it ->
            Interaction(
                it.key,
                it.value["type"] ?: "",
                it.value["message"] ?: ""
            )
        }
    }
}
