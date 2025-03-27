package com.example.micapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.micapp.database.DatabaseRepository
import com.example.micapp.model.Address

class CustomizeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DatabaseRepository(application)

    fun addCategory(newCategory: String) {
        repository.insertCategory(newCategory)
    }

    fun addLocation(address: Address) {
        repository.insertAddress(address)
    }

    fun getCategories(): List<String> {
        val readings = repository.getSavedReadings()
        val categories = readings.map { it.category }.distinct()
        return categories
    }

    fun getLocations(): List<Address> {
        return repository.getAddresses()
    }
}