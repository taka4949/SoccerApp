package com.example.soccerapp.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE matches ADD COLUMN homeTeamCrest TEXT"
        )

        db.execSQL(
            "ALTER TABLE matches ADD COLUMN awayTeamCrest TEXT"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS leagues (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                emblem TEXT
            )
            """.trimIndent()
        )
    }
}

//DataBaseはVersionをその都度変更する必要がある。それが、Migration。