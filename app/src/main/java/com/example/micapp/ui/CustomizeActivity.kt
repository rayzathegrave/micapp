package com.example.micapp.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.viewmodel.CustomizeViewModel
import com.example.micapp.R

class CustomizeActivity : AppCompatActivity() {

    private val viewModel: CustomizeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize)

        val edtCategory: EditText = findViewById(R.id.edt_category)
        val edtStreetName: EditText = findViewById(R.id.edt_street_name)
        val edtHouseNumber: EditText = findViewById(R.id.edt_house_number)
        val btnSave: Button = findViewById(R.id.btn_save)
//        val btnLoad: Button = findViewById(R.id.btn_load)

        btnSave.setOnClickListener {
            val newCategory = edtCategory.text.toString().trim()
            val newStreetName = edtStreetName.text.toString().trim()
            val houseNumberText = edtHouseNumber.text.toString().trim()

            if (newCategory.isEmpty() || newStreetName.isEmpty() || houseNumberText.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val houseNumber = houseNumberText.toIntOrNull()
            if (houseNumber == null) {
                Toast.makeText(this, "House number must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addCategory(newCategory)
            viewModel.addLocation(newStreetName, houseNumber)
            Toast.makeText(this, "Saved: $newCategory, $newStreetName, $houseNumber", Toast.LENGTH_SHORT).show()
        }

//        btnLoad.setOnClickListener {
//            val categories = viewModel.getCategories()
//            val locations = viewModel.getLocations()
//
//            Toast.makeText(this, "Categories: $categories\nLocations: $locations", Toast.LENGTH_LONG).show()
//        }
    }
}
