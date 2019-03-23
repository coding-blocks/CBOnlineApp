package com.codingblocks.cbonlineapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.database.NotificationData
import com.codingblocks.onlineapi.CustomResponseInterceptor
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import okhttp3.OkHttpClient

class CBOnlineApp : Application() {

    lateinit var database: AppDatabase
    lateinit var dao: NotificationDao
    var notificationData = NotificationData()
    var position: Long? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Picasso.setSingletonInstance(Picasso.Builder(this).build())
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Cabin-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(NotificationReceivedHandler())
                .setNotificationOpenedHandler(NotificationOpenedHandler())
                .init()

        configureExoMedia()

    }

    companion object {
        lateinit var mInstance: CBOnlineApp
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }

    private fun configureExoMedia() {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        ExoMedia.setDataSourceFactoryProvider(object : ExoMedia.DataSourceFactoryProvider {
            private var instance: DataSource.Factory? = null

            override fun provide(userAgent: String, listener: TransferListener?): DataSource.Factory {
                if (instance == null) {
                    // Updates the network data source to use the OKHttp implementation
                    val interceptor = CustomResponseInterceptor()
                    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

                    val upstreamFactory = OkHttpDataSourceFactory(client, userAgent, listener)
                    instance = upstreamFactory
                    // Adds a cache around the upstreamFactory
//                    val cache = SimpleCache(File(cacheDir, "ExoMediaCache"), LeastRecentlyUsedCacheEvictor((50 * 1024 * 1024).toLong()))
//                    instance = CacheDataSourceFactory(cache, upstreamFactory, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                }

                return instance!!
            }
        })
    }

    inner class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler {
        override fun notificationReceived(notification: OSNotification) {
            val data = notification.payload.additionalData
            val title = notification.payload.title
            val body = notification.payload.body
            var imgurl = data.optString("thumbnail")
            if (imgurl == null) {
                imgurl = "default"
            }
            val type = data.optString("type")
            when (type) {
                "video" -> {
                    notificationData.title = title
                    notificationData.thumbnailUrl = imgurl
                    notificationData.type = type
                    notificationData.videoId = data.optString("videoId")
                    notificationData.videotitle = data.optString("title")
                    notificationData.description = data.optString("description")
                    addtoDatabase()
                }
                "external" -> {

                    notificationData.title = title
                    notificationData.thumbnailUrl = imgurl
                    notificationData.type = type
                    notificationData.url = data.optString("url")
                    addtoDatabase()


                }

                "quiz" -> {
                    notificationData.title = title
                    notificationData.thumbnailUrl = imgurl
                    notificationData.type = type
                    addtoDatabase()
                }

            }
        }

        @SuppressLint("StaticFieldLeak")
        private fun addtoDatabase() {
            object : AsyncTask<Void, Void, Long>() {
                override fun onPostExecute(result: Long?) {
                    position = result

                    val local = Intent()

                    local.action = "com.codingblocks.notification"

                    mInstance.sendBroadcast(local)
                }

                override fun doInBackground(vararg p0: Void?): Long {
                    return dao.addtolist(notificationData)
                }
            }.execute()
        }
    }


    class NotificationOpenedHandler : OneSignal.NotificationOpenedHandler {
        override fun notificationOpened(result: OSNotificationOpenResult?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}