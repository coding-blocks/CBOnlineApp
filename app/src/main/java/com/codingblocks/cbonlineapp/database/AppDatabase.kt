package com.codingblocks.cbonlineapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingblocks.cbonlineapp.database.converters.CourseIdList
import com.codingblocks.cbonlineapp.database.converters.TimestampConverter
import com.codingblocks.cbonlineapp.database.models.*

@Database(
    entities = [CourseModel::class, SectionModel::class, ContentModel::class, InstructorModel::class, Notification::class,
        CourseInstructorHolder.CourseWithInstructor::class, DoubtsModel::class, NotesModel::class, RunModel::class,
        JobsModel::class, CourseFeatureModel::class, SectionContentHolder.SectionWithContent::class
    ], exportSchema = true, version = 21
)
@TypeConverters(TimestampConverter::class, CourseIdList::class)
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

    abstract fun jobsDao(): JobsDao

    abstract fun featuresDao(): FeaturesDao


}
