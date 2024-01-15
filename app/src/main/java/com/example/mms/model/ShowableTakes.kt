package com.example.mms.model

import java.time.LocalDateTime

class ShowableTakes (
    val medicineName: String,
    val medicineType: String,
    val takeAt: LocalDateTime,
    val weight: Int,
)
