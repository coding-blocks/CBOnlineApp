package com.codingblocks.cbonlineapp

import android.app.Application
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.cbonlineapp.activities.ThrowableActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.NotificationReceivedHandler
import com.codingblocks.cbonlineapp.util.NotificationOpenedHandler
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_TAB
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.crashlytics.android.core.CrashlyticsCore
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.koin.android.ext.android.startKoin

class CBOnlineApp : Application() {

    companion object {
        lateinit var mInstance: CBOnlineApp
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        startKoin(
            this,
            listOf(
                viewModelModule,
                databaseModule
            )
        )

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
                            .setDefaultFontPath("fonts/NunitoSans-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        // Configure Routers
        try {
            Router.initActivityRouter(applicationContext, IActivityRouteTableInitializer { router ->
                router["activity://course/https://online.codingblocks.com/classroom/course/:s{$COURSE_ID}/run/:s{$RUN_ID}/:s{$COURSE_TAB}"] =
                    MyCourseActivity::class.java
                router["activity://course/https://online.codingblocks.com/courses/:s{courseId}"] =
                    CourseActivity::class.java
                router["activity://course/https://online.codingblocks.com/player/:s{$RUN_ATTEMPT_ID}/content/:s{$SECTION_ID}/:s{$CONTENT_ID}"] =
                    VideoPlayerActivity::class.java
                router["activity://course/https://online.codingblocks.com/:s{$RUN_ATTEMPT_ID}"] =
                    ThrowableActivity::class.java
            })
        } catch (e: ConcurrentModificationException) {
            CrashlyticsCore.getInstance().apply {
                setString("Router not working", e.localizedMessage)
                log("Concurrent Modification Exception")
            }
        }
    }
}
