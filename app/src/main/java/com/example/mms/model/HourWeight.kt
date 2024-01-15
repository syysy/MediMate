package com.example.mms.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "hourWeight"
)
data class HourWeight(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var hour: String,
    var weight: Int
)
