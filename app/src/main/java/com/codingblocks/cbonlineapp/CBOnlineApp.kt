package com.codingblocks.cbonlineapp

import android.app.Application
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.crashlytics.android.core.CrashlyticsCore
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

        startKoin(this, listOf(
            viewModelModule,
            databaseModule
        ))
        Picasso.setSingletonInstance(Picasso.Builder(this).build())

        //Initiate Calligraphy
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

        //Configure Routers
        try {
            Router.initActivityRouter(applicationContext, IActivityRouteTableInitializer { router ->
                router["activity://course/classroom/course/:s{course_id}/run/:s{runId}/overview"] =
                    MyCourseActivity::class.java
                router["activity://course/https://online.codingblocks.com/courses/:s{courseId}"] =
                    CourseActivity::class.java
            })
        } catch (e: ConcurrentModificationException) {
            CrashlyticsCore.getInstance().apply {
                setString("Router not working", e.localizedMessage)
                log("Concurrent Modification Exception")
            }
        }
    }
}
