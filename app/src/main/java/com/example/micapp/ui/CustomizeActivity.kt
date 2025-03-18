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

        // Set input type for house number to number
        edtHouseNumber.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        btnSave.setOnClickListener {
            val newCategory = edtCategory.text.toString().trim()
            val newStreetName = edtStreetName.text.toString().trim()
            val houseNumberText = edtHouseNumber.text.toString().trim()

            if ((newStreetName.isNotEmpty() && houseNumberText.isEmpty()) || (newStreetName.isEmpty() && houseNumberText.isNotEmpty())) {
                Toast.makeText(this, "Please enter both street name and house number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newStreetName.isNotEmpty() && !newStreetName.matches(Regex("^[a-zA-Z\\s]+$"))) {
                Toast.makeText(this, "Street name must contain only letters and spaces", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newCategory.isNotEmpty()) {
                viewModel.addCategory(newCategory)
            }

            if (newStreetName.isNotEmpty() && houseNumberText.isNotEmpty()) {
                val houseNumber = houseNumberText.toIntOrNull()
                if (houseNumber == null) {
                    Toast.makeText(this, "House number must be a valid number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.addLocation(newStreetName, houseNumber)
            }

            Toast.makeText(this, "Saved: $newCategory, $newStreetName, $houseNumberText", Toast.LENGTH_SHORT).show()
        }
    }
}