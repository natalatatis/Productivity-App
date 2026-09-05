package com.example.sp2.data.local

import android.content.Context
import androidx.room.Room

// Provides a single instance of the local database
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Gets or creates the database
    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            // Creates the database
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sp2_database"
            ).build()

            INSTANCE = instance

            instance
        }
    }
}