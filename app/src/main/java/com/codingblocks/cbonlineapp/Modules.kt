package com.codingblocks.cbonlineapp

import androidx.room.Room
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.codingblocks.cbonlineapp.viewmodels.CourseViewModel
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.cbonlineapp.viewmodels.LeaderboardViewModel
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.viewmodels.HomeActivityViewModel
import com.codingblocks.cbonlineapp.viewmodels.NotificationViewModel
import com.codingblocks.cbonlineapp.viewmodels.AnnouncementsViewModel
import com.codingblocks.cbonlineapp.viewmodels.SettingsViewModel
import com.codingblocks.cbonlineapp.viewmodels.QuizViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { MyCourseViewModel(get(), get(), get(), get(), get()) }
    viewModel { LeaderboardViewModel() }
    viewModel { NotificationViewModel(get()) }

    viewModel { AnnouncementsViewModel(get()) }
    viewModel { QuizViewModel() }

    // Activities
    viewModel { CourseViewModel(get()) }
    viewModel { VideoPlayerViewModel(get(), get(), get(), get(), get()) }
    viewModel { HomeActivityViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "app-database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
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
}
