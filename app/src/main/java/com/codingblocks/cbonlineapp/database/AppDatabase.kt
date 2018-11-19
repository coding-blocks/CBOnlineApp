package com.codingblocks.cbonlineapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        version = 1, entities = [
    CourseRun::class,
    CourseSection::class,
    CourseContent::class
], exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseRunDao
    abstract fun setionDao(): SectionDao
    abstract fun contentDao(): ContentDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app-database")
                    .build()
        }
    }

}
