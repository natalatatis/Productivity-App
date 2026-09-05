package com.example.sp2.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Provides a single instance of the local database
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Migration from database version 1 to version 2
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {

            // Adds the notes table without deleting existing tasks
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `notes` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `title` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `date` TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    // Gets or creates the database
    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sp2_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()

            INSTANCE = instance

            instance
        }
    }
}