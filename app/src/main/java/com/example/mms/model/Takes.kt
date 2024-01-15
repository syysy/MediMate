package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime


@Entity(
    tableName = "takes",
    primaryKeys = ["hourWeightId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HourWeight::class,
            parentColumns = ["id"],
            childColumns = ["hourWeightId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Takes (
    var hourWeightId: Int,
    var date: LocalDateTime,
    var takeAt: LocalDateTime,
    var isDone: Boolean,
)
