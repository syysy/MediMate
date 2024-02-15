package com.example.mms.Utils


fun truncString(string: String, length: Int): String {
    return if (string.length > length + 3) {
        string.substring(0, length) + "..."
    } else {
        string
    }
}
