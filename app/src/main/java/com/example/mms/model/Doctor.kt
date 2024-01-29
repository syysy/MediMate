package com.example.mms.model

import kotlinx.serialization.Serializable

@Serializable
class Doctor (
    val rpps: String,

    val firstName: String,
    val lastName: String,
    var fullName: String,
    val phone: String,
    val email: String,

    val address: String,
    val city: String,
    val zipCode: String,
)
