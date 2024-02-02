package com.example.mms.model

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "doctor",
    primaryKeys = ["rpps"]
)
class Doctor (
    val rpps: String,

    val firstName: String,
    val lastName: String,
    var fullName: String?,
    val phone: String?,
    val email: String?,

    val address: String?,
    val city: String?,
    val zipCode: String?,
)
