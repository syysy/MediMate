package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "cycleHourWeight",
    foreignKeys = [
        ForeignKey(
            entity = Cycle::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HourWeight::class,
            parentColumns = ["id"],
            childColumns = ["hourWeightId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["cycleId", "hourWeightId"]
)
class CycleHourWeight (
    var cycleId: Int,
    var hourWeightId: Int
)
