package com.example.micapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

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

    fun insertSavedReading(decibel: Int, category: String, streetname: String, housenumber: Int, timestamp: String) {
        val values = ContentValues().apply {
            put("decibel", decibel)
            put("category", category)
            put("streetname", streetname)
            put("housenumber", housenumber)
            put("timestamp", timestamp)
        }
        db.insert(DatabaseHelper.TABLE_SAVED_READING, null, values)
    }

    fun getCategories(): List<String> {
        val categories = mutableListOf<String>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_ADD_CATEGORY,
            arrayOf("category"),
            null, null, null, null, null
        )
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow("category")))
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
            locations.add("$street $houseNum")
        }
        cursor.close()
        return locations
    }

    fun getSavedReadings(): Cursor {
        return db.query(DatabaseHelper.TABLE_SAVED_READING, null, null, null, null, null, null)
    }

    fun close() {
        dbHelper.close()
    }
}