package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["taskId", "hourWeightId"],
    tableName = "oneTake",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HourWeight::class,
            parentColumns = ["id"],
            childColumns = ["hourWeightId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class OneTake (
    val taskId: Long,
    val hourWeightId: Int,
)
