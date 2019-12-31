package com.codingblocks.cbonlineapp

import androidx.room.Room
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtRepository
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtsViewModel
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewRepository
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewViewModel
import com.codingblocks.cbonlineapp.course.CourseRepository
import com.codingblocks.cbonlineapp.course.CourseViewModel
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsViewModel
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesRepository
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.jobs.JobsViewModel
import com.codingblocks.cbonlineapp.jobs.jobdetails.JobDetailViewModel
import com.codingblocks.cbonlineapp.library.LibraryRepository
import com.codingblocks.cbonlineapp.library.LibraryViewModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseRepository
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.mycourse.leaderboard.LeaderboardViewModel
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerRepository
import com.codingblocks.cbonlineapp.notifications.NotificationViewModel
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizViewModel
import com.codingblocks.cbonlineapp.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.arnav.spork.Spork

val viewModelModule = module {

    viewModel { MyCourseViewModel(get()) }
    viewModel { LeaderboardViewModel() }
    viewModel { NotificationViewModel(get()) }

    viewModel { QuizViewModel() }

    // Activities
    viewModel { VideoPlayerViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { JobsViewModel(get()) }
    viewModel { JobDetailViewModel(get(), get()) }

    single { MyCourseRepository(get(), get(), get(), get(), get()) }



    viewModel { AdminDoubtsViewModel(get()) }
    viewModel { AdminOverviewViewModel(get()) }
    viewModel { DashboardDoubtsViewModel(get()) }
    viewModel { CourseViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }


    single { AdminDoubtRepository() }
    single { AdminOverviewRepository() }
    single { CourseRepository(get(), get(), get(), get(), get()) }
    single { DashboardDoubtsRepository(get(), get(), get()) }
    single { DashboardMyCoursesRepository(get(), get(), get(), get()) }
    single { LibraryRepository(get(), get()) }
    single { DashboardHomeRepository(get(), get()) }
    single { VideoPlayerRepository(get(), get(), get(), get(), get(), get()) }

    single { Spork.create(androidApplication(), AppPrefs::class) }
}
val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "app-database"
        )
            .allowMainThreadQueries()
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

    factory {
        val database: AppDatabase = get()
        database.commentsDao()
    }
}
