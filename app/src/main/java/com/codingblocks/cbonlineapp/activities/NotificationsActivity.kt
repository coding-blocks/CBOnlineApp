package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.campusapp.router.Router
import cn.campusapp.router.route.ActivityRoute
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.NotificationsAdapter
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.commons.NotificationsDiffCallback
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.VIDEO_ID
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
            override fun onClick(
                notificationID: Long,
                url: String,
                videoId: String
            ) {
                notificationDao.updateseen(notificationID)
                if (url.contains("course", true) ||
                    url.contains("classroom", true)
                ) {
                    Router.open("activity://course/$url")
                } else if (url.contains("player", true)) {
                    val activityRoute = Router.getRoute("activity://course/$url") as ActivityRoute
                    activityRoute
                        .withParams(VIDEO_ID, videoId)
                        .open()
                } else {
                    Components.openChrome(this@NotificationsActivity, url)
                }
            }
        }
        notificationAdapter.apply {
            onClick = eventClickListener
        }

        val itemTouch = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // get all notifications
                val notifications = notificationDao.allNotificationNonLive
                // get the id of element which needs to be deleted
                val deleteUID = notifications[position].id
                // remove the item from database
                notificationDao.deleteNotificationByID(deleteUID.toString())
            }
        }
        val helper = ItemTouchHelper(itemTouch)
        helper.attachToRecyclerView(notificationRv)
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
