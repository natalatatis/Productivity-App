package com.example.sp2.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {

        override fun migrate(db: SupportSQLiteDatabase) {

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

    private val MIGRATION_2_3 = object : Migration(2, 3) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Creates the lists table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `task_lists` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL
                )
                """.trimIndent()
            )

            // Allows each task to optionally belong to a list
            db.execSQL(
                """
                ALTER TABLE `tasks`
                ADD COLUMN `listId` INTEGER DEFAULT NULL
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Creates the habits table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `habits` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `targetCount` INTEGER NOT NULL,
                    `currentPeriodKey` TEXT NOT NULL,
                    `currentCount` INTEGER NOT NULL DEFAULT 0,
                    `streak` INTEGER NOT NULL DEFAULT 0,
                    `lastCompletedPeriodKey` TEXT DEFAULT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Rebuilds the habits table with accumulated hit/miss
            // counters and duration support (any existing test
            // habits are cleared, since this feature is brand new)
            db.execSQL("DROP TABLE IF EXISTS `habits`")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `habits` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `targetCount` INTEGER NOT NULL,
                    `currentPeriodKey` TEXT NOT NULL,
                    `currentCount` INTEGER NOT NULL DEFAULT 0,
                    `streak` INTEGER NOT NULL DEFAULT 0,
                    `durationType` TEXT NOT NULL DEFAULT 'INDEFINITE',
                    `totalPeriods` INTEGER DEFAULT NULL,
                    `totalHits` INTEGER NOT NULL DEFAULT 0,
                    `totalMisses` INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Rebuilds the habits table using a real date as the
            // period anchor (instead of an opaque period string),
            // plus a grace-window counter for streak forgiveness
            db.execSQL("DROP TABLE IF EXISTS `habits`")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `habits` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `targetCount` INTEGER NOT NULL,
                    `anchorDate` TEXT NOT NULL,
                    `currentCount` INTEGER NOT NULL DEFAULT 0,
                    `streak` INTEGER NOT NULL DEFAULT 0,
                    `consecutiveMisses` INTEGER NOT NULL DEFAULT 0,
                    `durationType` TEXT NOT NULL DEFAULT 'INDEFINITE',
                    `totalPeriods` INTEGER DEFAULT NULL,
                    `totalHits` INTEGER NOT NULL DEFAULT 0,
                    `totalMisses` INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_6_7 = object : Migration(6, 7) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Tracks the app-open streak
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `app_usage_state` (
                    `id` INTEGER PRIMARY KEY NOT NULL,
                    `anchorDate` TEXT NOT NULL,
                    `streak` INTEGER NOT NULL DEFAULT 0,
                    `consecutiveMisses` INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            // Logs each day the app was opened, used for the
            // last-7-days dots
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `app_usage_log` (
                    `date` TEXT PRIMARY KEY NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_7_8 = object : Migration(7, 8) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `alarms` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `hour` INTEGER NOT NULL,
                    `minute` INTEGER NOT NULL,
                    `label` TEXT NOT NULL DEFAULT '',
                    `days` TEXT NOT NULL DEFAULT '',
                    `enabled` INTEGER NOT NULL DEFAULT 1
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_8_9 = object : Migration(8, 9) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                "ALTER TABLE `alarms` ADD COLUMN `soundUri` TEXT DEFAULT NULL"
            )

            db.execSQL(
                "ALTER TABLE `alarms` ADD COLUMN `snoozeMinutes` INTEGER NOT NULL DEFAULT 10"
            )
        }
    }

    private val MIGRATION_9_10 = object : Migration(9, 10) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `note_lists` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                "ALTER TABLE `notes` ADD COLUMN `listId` INTEGER DEFAULT NULL"
            )
        }
    }

    private val MIGRATION_10_11 = object : Migration(10, 11) {

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `alarms` ADD COLUMN `specificDate` TEXT DEFAULT NULL"
            )
        }
    }

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sp2_database"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_8_9,
                    MIGRATION_9_10,
                    MIGRATION_10_11
                )
                .build()

            INSTANCE = instance

            instance
        }
    }
}