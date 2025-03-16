package com.example.micapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.micapp.database.DatabaseRepository

class CustomizeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DatabaseRepository(application)

    fun addCategory(newCategory: String) {
        repository.insertCategory(newCategory)
    }

    fun addLocation(streetName: String, houseNumber: Int) {
        repository.insertAddress(streetName, houseNumber)
    }

    fun getCategories(): List<String> {
        val readings = repository.getSavedReadings()
        val categories = readings.map { it.category }.distinct()
        return categories
    }

    fun getLocations(): List<String> {
        val readings = repository.getSavedReadings()
        val locations = readings.map { "${it.streetname}, ${it.housenumber}" }.distinct()
        return locations
    }
}