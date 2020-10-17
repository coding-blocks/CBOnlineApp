package com.codingblocks.cbonlineapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.campaign.CampaignActivity
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.course.SearchCourseActivity
import com.codingblocks.cbonlineapp.di.databaseModule
import com.codingblocks.cbonlineapp.di.firebaseModule
import com.codingblocks.cbonlineapp.di.preferencesModule
import com.codingblocks.cbonlineapp.di.viewModelModule
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.tracks.LearningTracksActivity
import com.codingblocks.cbonlineapp.tracks.TrackActivity
import com.codingblocks.cbonlineapp.util.ADMIN_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.DOWNLOAD_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.misc.AppSignatureHelper
import com.codingblocks.cbonlineapp.util.receivers.NotificationOpenedHandler
import com.codingblocks.cbonlineapp.util.receivers.NotificationReceivedHandler
import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.onesignal.OneSignal
import org.jetbrains.anko.notificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CBOnlineApp : Application() {

    companion object {
        lateinit var mInstance: CBOnlineApp
    }

    override fun onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(applicationContext))
        super.onCreate()
        mInstance = this
        val prefs = PreferenceHelper.getPrefs(this)

        CBOnlineLib.initialize(object : CBOnlineCommunicator {

            override var authJwt: String
                get() = prefs.SP_JWT_TOKEN_KEY
                set(value) {
                    prefs.SP_JWT_TOKEN_KEY = value
                }
            override var refreshToken: String
                get() = prefs.SP_JWT_REFRESH_TOKEN
                set(value) {
                    prefs.SP_JWT_REFRESH_TOKEN = value
                }
            override var baseUrl: String
                get() = BuildConfig.BASE_URL
                set(value) {}
            override var appVersion: Int
                get() = BuildConfig.VERSION_CODE
                set(value) {}
        })

        if (BuildConfig.DEBUG) {
            AppSignatureHelper(this).appSignatures.forEach {
                Log.d("APPSIG", it)
            }
            CBOnlineLib.httpLogging = true
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
            modules(
                listOf(
                    viewModelModule, firebaseModule,
                    databaseModule, preferencesModule
                )
            )
        }

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .setNotificationReceivedHandler(NotificationReceivedHandler())
            .setNotificationOpenedHandler(NotificationOpenedHandler())
            .init()

        // Configure Routers
        try {
            Router.initActivityRouter(
                applicationContext,
                IActivityRouteTableInitializer { router ->
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
                    router["activity://courseRun/https://online.codingblocks.com/app/spin-n-win"] =
                        CampaignActivity::class.java
                }
            )
        } catch (e: ConcurrentModificationException) {
            FirebaseCrashlytics.getInstance().log("Router not working : ${e.localizedMessage}")
        }
    }
}
