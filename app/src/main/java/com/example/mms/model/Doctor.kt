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
    var phone: String?,
    var email: String?,

    val address: String?,
    val city: String?,
    val zipCode: String?,
) {
    fun getDisplayName(): String {
        return this.fullName ?: "$firstName $lastName"
    }

    fun getDisplayAdress(): String {
        return "${this.city} ${this.zipCode}: ${this.address} "
    }

    override fun toString(): String {
        return "Doctor(rpps='$rpps', firstName='$firstName', lastName='$lastName', fullName=$fullName, phone=$phone, email=$email, address=$address, city=$city, zipCode=$zipCode)"
    }
}
