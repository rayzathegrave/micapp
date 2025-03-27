package com.example.micapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.database.DatabaseRepository
import com.example.micapp.model.Address
import com.example.micapp.model.Reading

class SaveReadingActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_reading)

        repository = DatabaseRepository(this)

        val txtLastReading: TextView = findViewById(R.id.txt_last_reading)
        val spinnerLocation: Spinner = findViewById(R.id.spinner_location)
        val spinnerCategory: Spinner = findViewById(R.id.spinner_category)
        val spinnerTimestamp: Spinner = findViewById(R.id.spinner_timestamp)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)

        val lastReading = intent.getStringExtra("DECIBEL_READING") ?: "No data"
        txtLastReading.text = "Last Reading: $lastReading"

        // Fetch data from database
        val addresses = repository.getAddresses().map { "${it.streetname}, ${it.housenumber}" }
        val categories = repository.getCategories().map { it.category }
        val timestamps = listOf("morning", "afternoon", "evening", "night")

        spinnerLocation.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, addresses)
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerTimestamp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timestamps)

        btnConfirm.setOnClickListener {
            val selectedLocation = spinnerLocation.selectedItem.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedTimestamp = spinnerTimestamp.selectedItem.toString()

            val locationParts = selectedLocation.split(", ")
            if (locationParts.size < 2) {
                Toast.makeText(this, "Invalid location format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val streetname = locationParts[0]
            if (!streetname.matches(Regex("^[a-zA-Z\\s]+$"))) {
                Toast.makeText(this, "Street name must contain only letters and spaces", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val housenumber = locationParts[1].toIntOrNull()
            if (housenumber == null) {
                Toast.makeText(this, "Invalid house number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val decibelReading = lastReading.filter { it.isDigit() }.toIntOrNull()
            if (decibelReading == null) {
                Toast.makeText(this, "Invalid decibel reading", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reading = Reading(
                decibel = decibelReading,
                category = selectedCategory,
                streetname = streetname,
                housenumber = housenumber,
                timestamp = selectedTimestamp
            )

            repository.insertSavedReading(reading)
            Toast.makeText(this, "Saved: $selectedLocation, $selectedCategory, $selectedTimestamp", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}