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
        val cursor = repository.getSavedReadings()
        val categories = mutableListOf<String>()

        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow("category")))
        }
        cursor.close()
        return categories
    }

    fun getLocations(): List<String> {
        val cursor = repository.getSavedReadings()
        val locations = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val street = cursor.getString(cursor.getColumnIndexOrThrow("streetname"))
            val houseNum = cursor.getInt(cursor.getColumnIndexOrThrow("housenumber"))
            locations.add("$street, $houseNum")
        }
        cursor.close()
        return locations
    }
}