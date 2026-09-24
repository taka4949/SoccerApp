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

//DataBaseはVersionをその都度変更する必要がある。それが、Migration。