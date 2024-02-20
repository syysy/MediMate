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
        var string = ""

        if (!this.city.isNullOrEmpty()) {
            string += "${this.city} "
        }
        if (!this.zipCode.isNullOrEmpty()) {
            string += "${this.zipCode}"
        }
        if (!this.address.isNullOrEmpty()) {
            if (string.isNotEmpty()) {
                string += ": "
            }
            string += this.address
        }

        return string
    }

    override fun toString(): String {
        return "Doctor(rpps='$rpps', firstName='$firstName', lastName='$lastName', fullName=$fullName, phone=$phone, email=$email, address=$address, city=$city, zipCode=$zipCode)"
    }
}
