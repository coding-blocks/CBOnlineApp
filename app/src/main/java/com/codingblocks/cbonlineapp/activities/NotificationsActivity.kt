package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.NotificationsAdapter
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.commons.NotificationsDiffCallback
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.extensions.observer
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_notifications.emptyTv
import kotlinx.android.synthetic.main.activity_notifications.notificationRv
import kotlinx.android.synthetic.main.activity_notifications.notificationToolbar
import org.koin.android.ext.android.inject

class NotificationsActivity : AppCompatActivity() {

    private val notificationDao: NotificationDao by inject()
    private val notificationAdapter = NotificationsAdapter(NotificationsDiffCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        setSupportActionBar(notificationToolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpUI()
        val eventClickListener: NotificationClickListener = object : NotificationClickListener {
            override fun onClick(notificationID: Long, url: String) {
                notificationDao.updateseen(notificationID)
                if (url.contains("course", true) ||
                    url.contains("classroom", true) ||
                    url.contains("player", true)
                )
                    Router.open("activity://course/$url")
                else {
                    val builder = CustomTabsIntent.Builder()
                        .enableUrlBarHiding()
                        .setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                        .setShowTitle(true)
                        .setSecondaryToolbarColor(resources.getColor(R.color.colorPrimary))
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this@NotificationsActivity, Uri.parse(url))
                }
            }

        }
        notificationAdapter.apply {
            onClick = eventClickListener
        }

    }

    private fun setUpUI() {
        val mLayoutManager = LinearLayoutManager(this)
        notificationRv.layoutManager = mLayoutManager
        notificationRv.adapter = notificationAdapter
        notificationDao.allNotification.observer(this) {
            if (it.isNotEmpty()) {
                notificationAdapter.submitList(it)
                notificationRv.visibility = View.VISIBLE
                emptyTv.visibility = View.GONE
            } else {
                notificationRv.visibility = View.GONE
                emptyTv.visibility = View.VISIBLE
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                if (notificationDao.allNotificationNonLive.isNotEmpty()) {
                    showconfirmation()
                }
                true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showconfirmation() {
        notificationDao.nukeTable()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationAdapter.apply {
            onClick = null
        }
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
