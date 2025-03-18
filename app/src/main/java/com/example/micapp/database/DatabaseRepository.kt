package com.example.micapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.micapp.model.Category
import com.example.micapp.model.Address
import com.example.micapp.model.Reading

class DatabaseRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertCategory(category: String) {
        val values = ContentValues().apply {
            put("category", category)
        }
        db.insert(DatabaseHelper.TABLE_ADD_CATEGORY, null, values)
    }

    fun insertAddress(streetname: String, housenumber: Int) {
        val values = ContentValues().apply {
            put("streetname", streetname)
            put("housenumber", housenumber)
        }
        db.insert(DatabaseHelper.TABLE_ADD_ADRES, null, values)
    }

    fun insertSavedReading(reading: Reading) {
        val values = ContentValues().apply {
            put("decibel", reading.decibel)
            put("category", reading.category)
            put("streetname", reading.streetname)
            put("housenumber", reading.housenumber)
            put("timestamp", reading.timestamp)
        }
        db.insert(DatabaseHelper.TABLE_SAVED_READING, null, values)
    }

    fun getCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_ADD_CATEGORY,
            arrayOf("category"),
            null, null, null, null, null
        )
        while (cursor.moveToNext()) {
            categories.add(Category(cursor.getString(cursor.getColumnIndexOrThrow("category"))))
        }
        cursor.close()
        return categories
    }

    fun getLocations(): List<String> {
        val locations = mutableListOf<String>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_ADD_ADRES,
            arrayOf("streetname", "housenumber"),
            null, null, null, null, null
        )
        while (cursor.moveToNext()) {
            val street = cursor.getString(cursor.getColumnIndexOrThrow("streetname"))
            val houseNum = cursor.getInt(cursor.getColumnIndexOrThrow("housenumber"))
            locations.add("$street, $houseNum")
        }
        cursor.close()
        return locations
    }

    fun getSavedReadings(): List<Reading> {
        val readings = mutableListOf<Reading>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SAVED_READING,
            arrayOf("decibel", "category", "streetname", "housenumber", "timestamp"),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val decibel = cursor.getInt(cursor.getColumnIndexOrThrow("decibel"))
            val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
            val streetname = cursor.getString(cursor.getColumnIndexOrThrow("streetname"))
            val housenumber = cursor.getInt(cursor.getColumnIndexOrThrow("housenumber"))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            readings.add(Reading(decibel, category, streetname, housenumber, timestamp))
        }
        cursor.close()
        return readings
    }

    fun close() {
        dbHelper.close()
    }
}