package com.codingblocks.cbonlineapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingblocks.cbonlineapp.database.models.Course
import com.codingblocks.cbonlineapp.database.models.CourseContent
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.CourseSection
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.database.models.Notification
import com.codingblocks.cbonlineapp.database.models.SectionWithContent

@Database(
    entities = [CourseRun::class, CourseSection::class, CourseContent::class, Instructor::class, Notification::class,
        CourseWithInstructor::class, SectionWithContent::class, DoubtsModel::class, NotesModel::class, Course::class
    ], exportSchema = false, version = 11
)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseRunDao(): CourseRunDao

    abstract fun sectionDao(): SectionDao

    abstract fun contentDao(): ContentDao

    abstract fun instructorDao(): InstructorDao

    abstract fun courseDao(): CourseDao

    abstract fun courseWithInstructorDao(): CourseWithInstructorDao

    abstract fun sectionWithContentsDao(): SectionWithContentsDao

    abstract fun doubtsDao(): DoubtsDao

    abstract fun notesDao(): NotesDao

    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app-database"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
