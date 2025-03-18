package com.example.micapp.model

data class Reading(
    val decibel: Int,
    val category: String,
    val streetname: String,
    val housenumber: Int,
    val timestamp: String
)