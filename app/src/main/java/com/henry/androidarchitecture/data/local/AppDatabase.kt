package com.henry.androidarchitecture.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.util.AppUtils

@Database(entities = [Note::class], version = AppUtils.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
