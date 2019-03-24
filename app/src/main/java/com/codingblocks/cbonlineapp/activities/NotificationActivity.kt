package com.codingblocks.cbonlineapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.NotificationItemsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val dao by lazy {
        database.notificationDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val notificationAdapter = NotificationItemsAdapter(ArrayList())
        val mLayoutManager = LinearLayoutManager(this)
        notificationRv.layoutManager = mLayoutManager
        notificationRv.adapter = notificationAdapter
        mLayoutManager.stackFromEnd = true
        notificationAdapter.setItems(dao.allNotification)
        if (dao.allNotification.isNotEmpty()) {
            notificationRv.visibility = View.VISIBLE
            emptyTv.visibility = View.GONE
        } else {
            notificationRv.visibility = View.GONE
            emptyTv.visibility = View.VISIBLE
        }
//        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
//            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
////                mNotes.remove(position)
////                notificationAdapter.notifyItemRemoved(position)
//            }
//        })
//        itemTouchHelper.attachToRecyclerView(notificationList)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                if (dao.allNotification.isNotEmpty()) {
//                    showconfirmation()
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

//    private fun showconfirmation() {
//        val confirmDialog = AlertDialog.Builder(this).create()
//        val updateView = layoutInflater.inflate(R.layout.dialog_confirm, null)
//        updateView.clearBtn.setOnClickListener {
//            dao.nukeTable()
//            onBackPressed()
//        }
//        updateView.cancelBtn.setOnClickListener {
//            confirmDialog.dismiss()
//        }
//        confirmDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
//        confirmDialog.setView(updateView)
//        confirmDialog.setCancelable(false)
//        confirmDialog.show()
//    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

}
