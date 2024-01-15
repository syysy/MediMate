package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(
    tableName = "cycle",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Cycle(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var taskId: Long,
    var hoursOfTreatment: Int,
    var currentHourInTreatment: Int,
    var hoursOfRest: Int,

    @Ignore
    var hourWeights: MutableList<HourWeight> = mutableListOf()
) {
    constructor() : this(0, 0, 0, 0, 0, mutableListOf())

    fun isEmpty(): Boolean {
        return this.id == 0 &&
                this.hoursOfTreatment == 0 &&
                this.currentHourInTreatment == 0 &&
                this.hoursOfRest == 0 &&
                this.hourWeights.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return !this.isEmpty()
    }
}
