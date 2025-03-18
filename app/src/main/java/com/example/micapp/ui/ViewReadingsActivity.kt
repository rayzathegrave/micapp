package com.example.micapp.ui

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.micapp.R
import com.example.micapp.database.DatabaseRepository

class ViewReadingsActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_readings)

        repository = DatabaseRepository(this)
        val listView: ListView = findViewById(R.id.list_readings)

        val readings = repository.getSavedReadings().map {
            mapOf(
                "decibel" to it.decibel.toString(),
                "category" to it.category,
                "streetname" to it.streetname,
                "housenumber" to it.housenumber.toString(),
                "timestamp" to it.timestamp
            )
        }

        val adapter = SimpleAdapter(
            this, readings, R.layout.list_item_reading,
            arrayOf("category", "decibel", "streetname", "housenumber", "timestamp"),
            intArrayOf(R.id.txt_category, R.id.txt_decibel, R.id.txt_streetname, R.id.txt_housenumber, R.id.txt_timestamp)
        )

        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}