package com.example.mms.model.medicines

import androidx.room.Entity
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class MedicinesByName(
    val name: String,
    val medicines: List<Medicine>,
) {
    fun getAllMedicines(): List<Medicine> = this.medicines

    fun getMedicineByCIS(codeCis: Long): Medicine? =
        this.medicines.firstOrNull { it.code_cis == codeCis }
}
