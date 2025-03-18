package com.example.micapp.model

data class Reading(
    val id: Long = 0,
    val decibel: Int,
    val category: String,
    val streetname: String,
    val housenumber: Int,
    val timestamp: String
)