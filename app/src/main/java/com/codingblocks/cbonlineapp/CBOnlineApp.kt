package com.codingblocks.cbonlineapp

import android.app.Application
import android.content.Context
import cn.campusapp.router.Router
import cn.campusapp.router.router.IActivityRouteTableInitializer
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class CBOnlineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Picasso.setSingletonInstance(Picasso.Builder(this).build())

        //Intitate Calligraphy
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
        Router.initActivityRouter(applicationContext, IActivityRouteTableInitializer { router ->
            router["activity://course/classroom/course/:s{course_id}/run/:s{runId}/overview"] = MyCourseActivity::class.java
            router["activity://course/https://online.codingblocks.com/courses/:s{courseId}"] = CourseActivity::class.java
        })


        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)
    }

    companion object {
        lateinit var mInstance: CBOnlineApp
    }
}
