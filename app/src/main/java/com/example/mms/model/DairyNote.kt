package com.example.mms.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes"
)
class DairyNote (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val note: String
)