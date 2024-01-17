package com.example.mms.Utils

import java.security.MessageDigest

/**
 * Hashes a string using SHA-256.
 *
 * @param input The string to hash.
 * @return The hashed string.
 */
fun hashString(input: String): String {
    val bytes = input.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(bytes)
    return hashBytes.joinToString("") { "%02x".format(it) }
}