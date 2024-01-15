package com.example.mms.Utils

fun cryptEmail(email : String) : String{

    val split = email.split("@")
    var firstPart = split[0]

    if (firstPart.length < 2) {
        return email
    }

    val secondPart = split[1]
    firstPart = firstPart.substring(0, 2) + "*****"
    return "$firstPart@$secondPart"
}

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}