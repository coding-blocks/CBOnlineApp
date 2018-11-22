package com.codingblocks.cbonlineapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        version = 1, entities = [
    CourseRun::class,
    CourseSection::class,
    CourseContent::class,
    Instructor::class,
    Course::class,
    Announcement::class
], exportSchema = false
)
@TypeConverters(AnnouncementConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseRunDao(): CourseRunDao
    abstract fun setionDao(): SectionDao
    abstract fun contentDao(): ContentDao
    abstract fun instructorDao(): InstructorDao
    abstract fun courseDao(): CourseDao


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
