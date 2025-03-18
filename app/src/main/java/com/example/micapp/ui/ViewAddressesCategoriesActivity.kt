package com.example.micapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.database.DatabaseRepository

class ViewAddressesCategoriesActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseRepository
    private lateinit var listAddresses: ListView
    private lateinit var listCategories: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_addresses_categories)

        repository = DatabaseRepository(this)
        listAddresses = findViewById(R.id.list_addresses)
        listCategories = findViewById(R.id.list_categories)

        loadAddresses()
        loadCategories()
    }

    private fun loadAddresses() {
        val addresses = repository.getLocations()
        val adapter = object : ArrayAdapter<String>(this, R.layout.list_item_address_category, R.id.txt_item, addresses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val deleteButton: Button = view.findViewById(R.id.btn_delete)
                deleteButton.setOnClickListener {
                    val address = addresses[position]
                    val parts = address.split(", ")
                    if (parts.size == 2) {
                        val streetname = parts[0]
                        val housenumber = parts[1].toIntOrNull()
                        if (housenumber != null) {
                            repository.deleteAddress(streetname, housenumber)
                            Toast.makeText(this@ViewAddressesCategoriesActivity, "Address deleted", Toast.LENGTH_SHORT).show()
                            loadAddresses()
                        }
                    }
                }
                return view
            }
        }
        listAddresses.adapter = adapter
    }

    private fun loadCategories() {
        val categories = repository.getCategories().map { it.category }
        val adapter = object : ArrayAdapter<String>(this, R.layout.list_item_address_category, R.id.txt_item, categories) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val deleteButton: Button = view.findViewById(R.id.btn_delete)
                deleteButton.setOnClickListener {
                    val category = categories[position]
                    repository.deleteCategory(category)
                    Toast.makeText(this@ViewAddressesCategoriesActivity, "Category deleted", Toast.LENGTH_SHORT).show()
                    loadCategories()
                }
                return view
            }
        }
        listCategories.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}