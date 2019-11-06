package com.codingblocks.cbonlineapp

import androidx.room.Room
import com.codingblocks.cbonlineapp.course.CourseViewModel
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.home.HomeActivityViewModel
import com.codingblocks.cbonlineapp.home.HomeRepository
import com.codingblocks.cbonlineapp.home.HomeViewModel
import com.codingblocks.cbonlineapp.home.mycourses.MyCoursesRepository
import com.codingblocks.cbonlineapp.home.mycourses.MyCoursesViewModel
import com.codingblocks.cbonlineapp.jobs.JobsViewModel
import com.codingblocks.cbonlineapp.jobs.jobdetails.JobDetailViewModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseRepository
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.mycourse.leaderboard.LeaderboardViewModel
import com.codingblocks.cbonlineapp.notifications.NotificationViewModel
import com.codingblocks.cbonlineapp.player.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.quiz.QuizViewModel
import com.codingblocks.cbonlineapp.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { MyCoursesViewModel(get()) }

    viewModel { MyCourseViewModel(get()) }
    viewModel { LeaderboardViewModel() }
    viewModel { NotificationViewModel(get()) }

    viewModel { QuizViewModel() }

    // Activities
    viewModel { CourseViewModel(get(), get()) }
    viewModel { VideoPlayerViewModel(get(), get(), get(), get(), get()) }
    viewModel { HomeActivityViewModel() }
    viewModel { SettingsViewModel(get()) }
    viewModel { JobsViewModel(get()) }
    viewModel { JobDetailViewModel(get(), get(), get(), get()) }

    single { HomeRepository(get(), get(), get(), get(), get()) }
    single { MyCoursesRepository(get(), get(), get(), get()) }
    single { MyCourseRepository(get(), get(), get(), get(), get()) }
}
val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "app-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        val database: AppDatabase = get()
        database.doubtsDao()
    }

    factory {
        val database: AppDatabase = get()
        database.notesDao()
    }

    factory {
        val database: AppDatabase = get()
        database.courseDao()
    }

    factory {
        val database: AppDatabase = get()
        database.instructorDao()
    }

    factory {
        val database: AppDatabase = get()
        database.featuresDao()
    }

    factory {
        val database: AppDatabase = get()
        database.courseRunDao()
    }

    factory {
        val database: AppDatabase = get()
        database.courseWithInstructorDao()
    }

    factory {
        val database: AppDatabase = get()
        database.sectionDao()
    }

    factory {
        val database: AppDatabase = get()
        database.contentDao()
    }

    factory {
        val database: AppDatabase = get()
        database.sectionWithContentsDao()
    }
    factory {
        val database: AppDatabase = get()
        database.notificationDao()
    }
    factory {
        val database: AppDatabase = get()
        database.jobsDao()
    }
}
