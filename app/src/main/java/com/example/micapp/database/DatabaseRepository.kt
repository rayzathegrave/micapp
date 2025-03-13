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

    fun getSavedReadings(): Cursor {
        return db.query(DatabaseHelper.TABLE_SAVED_READING, null, null, null, null, null, null)
    }

    fun close() {
        dbHelper.close()
    }
}