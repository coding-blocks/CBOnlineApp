package com.codingblocks.cbonlineapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingblocks.cbonlineapp.database.converters.CourseIdList
import com.codingblocks.cbonlineapp.database.converters.ProgressItemConverter
import com.codingblocks.cbonlineapp.database.converters.TimestampConverter
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.CommentModel
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.database.models.Notification
import com.codingblocks.cbonlineapp.database.models.PlayerState
import com.codingblocks.cbonlineapp.database.models.RunAttemptModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.database.models.SectionModel

@Database(
    entities = [CourseModel::class, SectionModel::class, ContentModel::class, InstructorModel::class, Notification::class,
        CourseWithInstructor::class, DoubtsModel::class, NotesModel::class, RunModel::class,
        JobsModel::class, SectionContentHolder.SectionWithContent::class, BookmarkModel::class,
        CommentModel::class, RunAttemptModel::class, RunPerformance::class, PlayerState::class
    ], exportSchema = true, version = 29
)
@TypeConverters(TimestampConverter::class, CourseIdList::class, ProgressItemConverter::class)
abstract class AppDatabase : RoomDatabase() {

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

    abstract fun commentsDao(): CommentsDao

    abstract fun runDao(): RunDao

    abstract fun runAttemptDao(): RunAttemptDao

    abstract fun runWithAttemptDao(): RunWithAttemptDao

    abstract fun runPerformanceDao(): RunPerformanceDao

    abstract fun libraryDao(): LibraryDao

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun playerDao(): PlayerDao
}
