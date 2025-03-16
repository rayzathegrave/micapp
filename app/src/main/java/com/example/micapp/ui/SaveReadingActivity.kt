package com.example.micapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.database.DatabaseRepository

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
        val locations = repository.getLocations()
        val categories = repository.getCategories()
        val timestamps = listOf("morning", "afternoon", "evening", "night") // Assuming timestamps are still hardcoded

        spinnerLocation.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerTimestamp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timestamps)

        btnConfirm.setOnClickListener {
            val selectedLocation = spinnerLocation.selectedItem.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedTimestamp = spinnerTimestamp.selectedItem.toString()

            val (streetname, housenumber) = selectedLocation.split(" ").let {
                it[0] to it[1].toInt()
            }

            // Extract integer from lastReading
            val decibelReading = lastReading.filter { it.isDigit() }.toIntOrNull()
            if (decibelReading == null) {
                Toast.makeText(this, "Invalid decibel reading", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save reading to database
            repository.insertSavedReading(
                decibelReading,
                selectedCategory,
                streetname,
                housenumber,
                selectedTimestamp
            )

            Toast.makeText(this, "Saved: $selectedLocation, $selectedCategory, $selectedTimestamp", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}