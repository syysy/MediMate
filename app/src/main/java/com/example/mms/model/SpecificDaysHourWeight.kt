package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore


@Entity(
    tableName = "specificDaysHourWeight",
    primaryKeys = ["taskId", "hourWeightId", "day"],
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
class SpecificDaysHourWeight (
    var taskId: Long,
    var hourWeightId: Int,
    var day: Int,

    @Ignore
    var hourWeight: HourWeight? = null
) {
    constructor() : this(0, 0, 0)
}
