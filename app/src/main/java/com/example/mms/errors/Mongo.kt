package com.example.mms.errors

open class MongoExeption(message: String = "") : Exception(message)

class ConnectionFailed(message: String = "") : MongoExeption(message)
