package com.codingblocks.cbonlineapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingblocks.cbonlineapp.database.models.*

@Database(
    entities = [CourseRun::class, CourseSection::class, CourseContent::class, Instructor::class, Notification::class,
        CourseWithInstructor::class, DoubtsModel::class, NotesModel::class, Course::class,
        JobsModel::class, CourseFeatures::class
    ], exportSchema = true, version = 20
)
@TypeConverters(TimestampConverter::class, CourseIdList::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        fun groupInstructor(courseAndInstructor: List<CourseInstructorPair>): List<CourseAndItsInstructor> {
            return mutableListOf<CourseAndItsInstructor>().also { items ->
                courseAndInstructor
                    .groupBy(keySelector = { it.course }, valueTransform = { it.instructor })
                    .forEach { items.add(CourseAndItsInstructor(it.key, it.value)) }
            }
        }
    }

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
