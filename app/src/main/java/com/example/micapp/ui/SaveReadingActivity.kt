package com.example.micapp.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.viewmodel.AudioRecorderViewModel

class SaveReadingActivity : AppCompatActivity() {

    private val viewModel: AudioRecorderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_reading)

        val txtLastReading: TextView = findViewById(R.id.txt_last_reading)
        val spinnerLocation: Spinner = findViewById(R.id.spinner_location)
        val spinnerCategory: Spinner = findViewById(R.id.spinner_category)
        val spinnerTimestamp: Spinner = findViewById(R.id.spinner_timestamp)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)

        val lastReading = intent.getStringExtra("DECIBEL_READING") ?: "No data"
        txtLastReading.text = "Last Reading: $lastReading"

        // Dummy data for spinners
        val locations = listOf("Home", "Office", "Outdoor")
        val categories = listOf("Noise", "Music", "Traffic")
        val timestamps = listOf("10:00 AM", "12:30 PM", "6:45 PM")

        spinnerLocation.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerTimestamp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timestamps)

        btnConfirm.setOnClickListener {
            val selectedLocation = spinnerLocation.selectedItem.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedTimestamp = spinnerTimestamp.selectedItem.toString()

            // Save reading to database
            viewModel.saveReading(
                lastReading.toInt(),
                selectedCategory,
                selectedLocation,
                0, // Assuming housenumber is not used in this context
                selectedTimestamp
            )

            Toast.makeText(this, "Saved: $selectedLocation, $selectedCategory, $selectedTimestamp", Toast.LENGTH_SHORT).show()
        }
    }
}