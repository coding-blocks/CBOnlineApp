package com.codingblocks.cbonlineapp

import androidx.room.Room
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.viewmodels.HomeActivityViewModel
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.codingblocks.cbonlineapp.viewmodels.LeaderboardViewModel
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.cbonlineapp.viewmodels.NotificationViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { MyCourseViewModel(get(), get(), get(), get(), get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { LeaderboardViewModel(get()) }
    //Activities
    viewModel { HomeActivityViewModel(get()) }
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
