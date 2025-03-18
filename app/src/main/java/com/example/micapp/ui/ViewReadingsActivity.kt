package com.example.micapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.database.DatabaseRepository

class ViewReadingsActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseRepository
    private lateinit var listView: ListView
    private lateinit var adapter: SimpleAdapter
    private lateinit var readings: MutableList<Map<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_readings)

        repository = DatabaseRepository(this)
        listView = findViewById(R.id.list_readings)

        loadReadings()
    }

    private fun loadReadings() {
        readings = repository.getSavedReadings().map {
            mapOf(
                "id" to it.id.toString(),
                "decibel" to it.decibel.toString(),
                "category" to it.category,
                "streetname" to it.streetname,
                "housenumber" to it.housenumber.toString(),
                "timestamp" to it.timestamp
            )
        }.toMutableList()

        adapter = object : SimpleAdapter(
            this, readings, R.layout.list_item_reading,
            arrayOf("category", "decibel", "streetname", "housenumber", "timestamp"),
            intArrayOf(R.id.txt_category, R.id.txt_decibel, R.id.txt_streetname, R.id.txt_housenumber, R.id.txt_timestamp)
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val deleteButton: Button = view.findViewById(R.id.btn_delete)
                deleteButton.tag = readings[position]["id"]
                deleteButton.setOnClickListener {
                    val id = it.tag.toString().toLong()
                    repository.deleteReading(id)
                    Toast.makeText(this@ViewReadingsActivity, "Reading deleted", Toast.LENGTH_SHORT).show()
                    loadReadings()
                }
                return view
            }
        }

        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}