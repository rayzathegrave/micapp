package com.example.micapp.model

data class Address(
    val streetname: String,
    val housenumber: Int
) {
    override fun toString(): String {
        return "$streetname, $housenumber"
    }
}