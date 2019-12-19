package com.codingblocks.cbonlineapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.ADMIN_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_TAB
import com.codingblocks.cbonlineapp.util.DOWNLOAD_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.NotificationOpenedHandler
import com.codingblocks.cbonlineapp.util.NotificationReceivedHandler
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.crashlytics.android.Crashlytics
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.jetbrains.anko.notificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.File

class CBOnlineApp : Application() {

    companion object {
        lateinit var mInstance: CBOnlineApp
        @JvmStatic
        var appContext: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        mInstance = this

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
                databaseModule))
        }

        Picasso.setSingletonInstance(Picasso.Builder(this).build())



        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .setNotificationReceivedHandler(NotificationReceivedHandler())
            .setNotificationOpenedHandler(NotificationOpenedHandler())
            .init()

        // Initiate Calligraphy
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/nunitosans_regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        // Configure Routers
        try {
            Router.initActivityRouter(applicationContext, IActivityRouteTableInitializer { router ->
                router["activity://courseRun/https://online.codingblocks.com/classroom/courseRun/:s{$COURSE_ID}/run/:s{$RUN_ID}/:s{$COURSE_TAB}"] =
                    MyCourseActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/courses/:s{courseId}"] =
                    CourseActivity::class.java
                router["activity://courseRun/https://online.codingblocks.com/player/:s{$RUN_ATTEMPT_ID}/content/:s{$SECTION_ID}/:s{$CONTENT_ID}"] =
                    VideoPlayerActivity::class.java
            })
        } catch (e: ConcurrentModificationException) {
            Crashlytics.log("Router not working : ${e.localizedMessage}")
        }
    }

    fun clearApplicationData() {
        val applicationCacheDirectory = File(cacheDir.parent)
        if (applicationCacheDirectory.exists()) {
            val fileNames = applicationCacheDirectory.list()
            for (fileName in fileNames) {
                if (fileName != "lib") {
                    deleteFile(File(applicationCacheDirectory, fileName))
                }
            }
        }
    }

    private fun deleteFile(file: File?): Boolean {
        var deletedAll = true
        if (file != null) {
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    deletedAll = deleteFile(File(file, children[i])) && deletedAll
                }
            } else {
                deletedAll = file.delete()
            }
        }

        return deletedAll
    }
}
