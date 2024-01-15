package com.example.mms.model

import android.mtp.MtpConstants
import com.example.mms.model.medicines.MType

class ShowableHourWeight (
    val medicineName: String,
    val medicineType: MType,
    val task: Task,
    val hourWeight: HourWeight,
    val medicineStorage: MedicineStorage?
) {
    fun hasEnoughStock(): Boolean {
        if (this.medicineStorage == null) {
            return true
        }

        return this.medicineStorage.storage >= this.hourWeight.weight
    }
}
