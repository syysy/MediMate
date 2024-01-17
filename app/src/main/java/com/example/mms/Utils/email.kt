package com.example.mms.Utils

/**
 * Function to mask email
 * @param email email to mask
 * @return masked email
 */
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

/**
 * Function to check if email is valid
 * @param email email to check
 * @return true if email is valid, false otherwise
 */
fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}