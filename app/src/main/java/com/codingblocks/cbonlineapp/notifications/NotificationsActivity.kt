package com.codingblocks.cbonlineapp.notifications

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.campusapp.router.Router
import cn.campusapp.router.route.ActivityRoute
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class NotificationsActivity : BaseCBActivity() {

    private val notificationDao: NotificationDao by inject()
    private val notificationAdapter = NotificationsAdapter(NotificationsDiffCallback())
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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
                coroutineScope.launch(Dispatchers.IO) {
                    notificationDao.updateseen(notificationID)
                }
                if (url.contains("courseRun", true) ||
                    url.contains("classroom", true)
                ) {
                    Router.open("activity://courseRun/$url")
                } else if (url.contains("player", true)) {
                    val activityRoute =
                        Router.getRoute("activity://courseRun/$url") as ActivityRoute
                    activityRoute
                        .withParams(VIDEO_ID, videoId)
                        .open()
                } else {
                    this@NotificationsActivity.openChrome(url)
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
                coroutineScope.launch {
                    val position = viewHolder.adapterPosition
                    // get all notifications
                    val notifications = withContext(Dispatchers.IO) { notificationDao.allNotificationNonLive }
                    // get the id of element which needs to be deleted
                    val deleteUID = notifications[position].id
                    // remove the item from database
                    async(Dispatchers.IO) { notificationDao.deleteNotificationByID(deleteUID) }
                }
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
                coroutineScope.launch {
                    val isNotEmpty = withContext(Dispatchers.IO) { notificationDao.allNotificationNonLive.isNotEmpty() }
                    if (isNotEmpty)
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
        showDialog(
            type = "Delete",
            image = R.drawable.ic_info,
            cancelable = false,
            primaryText = R.string.confirmation,
            secondaryText = R.string.delete_all_notifications,
            primaryButtonText = R.string.confirm,
            secondaryButtonText = R.string.cancel,
            callback = { confirmed ->
                if (confirmed) {
                    coroutineScope.launch(Dispatchers.IO) {
                        notificationDao.nukeTable()
                    }
                }
            }

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationAdapter.apply {
            onClick = null
        }
    }
}
