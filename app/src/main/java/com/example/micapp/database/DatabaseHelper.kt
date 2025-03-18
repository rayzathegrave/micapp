// DatabaseHelper.kt
package com.example.micapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ADD_CATEGORY)
        db.execSQL(CREATE_TABLE_ADD_ADRES)
        db.execSQL(CREATE_TABLE_SAVED_READING)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SAVED_READING")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADD_ADRES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADD_CATEGORY")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "micapp.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ADD_CATEGORY = "add_category"
        const val TABLE_ADD_ADRES = "add_adres"
        const val TABLE_SAVED_READING = "saved_reading"

        private const val CREATE_TABLE_ADD_CATEGORY = """
            CREATE TABLE $TABLE_ADD_CATEGORY (
                category VARCHAR(75) PRIMARY KEY
            );
        """

        private const val CREATE_TABLE_ADD_ADRES = """
            CREATE TABLE $TABLE_ADD_ADRES (
                streetname VARCHAR(250),
                housenumber INT,
                PRIMARY KEY (streetname, housenumber)
            );
        """

        private const val CREATE_TABLE_SAVED_READING = """
    CREATE TABLE $TABLE_SAVED_READING (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        decibel INTEGER NOT NULL,
        category VARCHAR(75),
        streetname VARCHAR(250),
        housenumber INT,
        timestamp VARCHAR(75) NOT NULL,
        FOREIGN KEY (category) REFERENCES $TABLE_ADD_CATEGORY(category),
        FOREIGN KEY (streetname, housenumber) REFERENCES $TABLE_ADD_ADRES(streetname, housenumber)
    );
"""
    }
}