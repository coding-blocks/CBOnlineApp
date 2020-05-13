package com.codingblocks.cbonlineapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.course.SearchCourseActivity
import com.codingblocks.cbonlineapp.di.databaseModule
import com.codingblocks.cbonlineapp.di.preferencesModule
import com.codingblocks.cbonlineapp.di.viewModelModule
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.tracks.LearningTracksActivity
import com.codingblocks.cbonlineapp.tracks.TrackActivity
import com.codingblocks.cbonlineapp.util.ADMIN_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.AppSignatureHelper
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.DOWNLOAD_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.NotificationOpenedHandler
import com.codingblocks.cbonlineapp.util.NotificationReceivedHandler
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.onlineapi.Clients
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import org.jetbrains.anko.notificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CBOnlineApp : Application() {

    companion object {
        lateinit var mInstance: CBOnlineApp

        @JvmStatic
        var appContext: Context? = null
            private set
    }

    override fun onCreate() {
        // Set your custom UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(applicationContext))
        super.onCreate()
        appContext = applicationContext
        mInstance = this

        if (BuildConfig.DEBUG) {
            AppSignatureHelper(this).appSignatures.forEach {
                Log.d("APPSIG", it)
            }
            Clients.setHttpLogging(true)
        }

        // Create Notification Channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                "Course Download",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val adminNotificationChannel = NotificationChannel(
                ADMIN_CHANNEL_ID,
                "Admin Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.createNotificationChannel(adminNotificationChannel)
        }
        startKoin {
            androidContext(this@CBOnlineApp)
            modules(listOf(viewModelModule,
                databaseModule, preferencesModule))
        }

        Picasso.setSingletonInstance(Picasso.Builder(this).build())

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .setNotificationReceivedHandler(NotificationReceivedHandler())
            .setNotificationOpenedHandler(NotificationOpenedHandler())
            .init()

        // Configure Routers
        try {
            Router.initActivityRouter(applicationContext, IActivityRouteTableInitializer { router ->
                router["activity://courseRun/https://online.codingblocks.com/app/classroom/course/:s{$COURSE_ID}/run/:s{$RUN_ID}"] =
                    MyCourseActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/courses/:s{courseId}"] =
                    CourseActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/courses"] =
                    SearchCourseActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/app/player/:s{$RUN_ATTEMPT_ID}/content/:s{$SECTION_ID}/:s{$CONTENT_ID}"] =
                    VideoPlayerActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/app/tracks/:s{courseId}"] =
                    TrackActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/app/tracks"] =
                    LearningTracksActivity::class.java
            })
        } catch (e: ConcurrentModificationException) {
            FirebaseCrashlytics.getInstance().log("Router not working : ${e.localizedMessage}")
        }
    }
}
