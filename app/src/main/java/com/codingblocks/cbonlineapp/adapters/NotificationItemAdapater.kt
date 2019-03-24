package com.codingblocks.cbonlineapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.database.NotificationData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_notification_item.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class NotificationItemsAdapter(private var items: List<NotificationData>) : RecyclerView.Adapter<NotificationItemsAdapter.ItemsViewHolder>() {

    private lateinit var context: Context
    lateinit var db: AppDatabase
    lateinit var dao: NotificationDao


    fun setItems(items: List<NotificationData>) {
        this.items = items
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        context = parent.context

        return ItemsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.single_notification_item, parent, false))
    }

    override fun getItemCount(): Int {
        return (items.size)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        db = AppDatabase.getInstance(context)!!
        dao = db.notificationDao()

        holder.bindView(items[position])
        val type = items[position].type
        when (type) {
            "video" -> {
                holder.itemView.setOnClickListener {
                    context.startActivity(context.intentFor<VideoPlayerActivity>("videoId" to items[position].videoId, "title" to items[position].videotitle, "description" to items[position].description).singleTop())
                    updateseen(items[position])

                }

            }
            "resources" -> {

                holder.itemView.setOnClickListener {

                    context.startActivity(context.intentFor<MyCourseActivity>("topic" to items[position].topic, "course" to items[position].course, "fragmentPosition" to items[position].fragmentPosition).singleTop())
                    updateseen(items[position])

                }


            }

            "external" -> {
                holder.itemView.setOnClickListener {
                    var url = items[position].url
                    if (!url.startsWith("http://")) {
                        url = "http://$url"
                    }
                    val builder = CustomTabsIntent.Builder().setToolbarColor(Color.parseColor("#1589ee"))
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                    updateseen(items[position])

                }

            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    private fun updateseen(notificationData: NotificationData) {
        object : AsyncTask<Void, Void, Void>() {
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                notifyDataSetChanged()
            }

            override fun doInBackground(vararg voids: Void): Void? {
                dao.updateseen(notificationData.id!!)
                return null
            }
        }.execute()

    }

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: NotificationData) {
            itemView.titletextView.text = item.title

            if (item.thumbnailUrl != "default" && item.thumbnailUrl.isNotEmpty()) {
                Picasso.get().load(item.thumbnailUrl).placeholder(R.drawable.placeholder_course_cover).into(itemView.notificationImageView)
            } else {
                itemView.notificationImageView.setImageResource(R.drawable.placeholder_course_cover)
            }

            if (item.seen) {
                itemView.titletextView.alpha = 0.5f
                itemView.notificationImageView.alpha = 0.5f

            }


        }
    }
}