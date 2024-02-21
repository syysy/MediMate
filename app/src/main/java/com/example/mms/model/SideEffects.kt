package com.example.mms.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SideEffects (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fullInfoUrl: String,
    val posology: String,
    val contraindications: String,
    val specialWarningsAndPrecautions: String,
    val interactionsOtherMedicines: String,
    val pregnancy: String,
    val drive: String,
    val sideEffects: String,
    val shelfLife: String,
)
