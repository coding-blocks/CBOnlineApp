package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.onlineapi.models.TopicsItem
import kotlinx.android.synthetic.main.doubt_header_item.view.*

class DoubtsAdapter(private var doubtsData: ArrayList<TopicsItem?>) : RecyclerView.Adapter<DoubtsAdapter.DoubtsViewHolder>() {

    private lateinit var context: Context

    fun setData(doubtsData: ArrayList<TopicsItem?>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DoubtsViewHolder, position: Int) {
        holder.bindView(doubtsData[position])
    }

    override fun getItemCount(): Int {
        return doubtsData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context

        return DoubtsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.doubt_header_item, parent, false))
    }

    inner class DoubtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: TopicsItem?) {
            itemView.timeTv.text =
                formatDate(data?.createdAt!!)
            itemView.titleTv.text = data.title
            itemView.usernameTv.text = data.lastPosterUsername
            itemView.setOnClickListener {
                val DOUBT_ID = data.id
                val builder = CustomTabsIntent.Builder()
                        .enableUrlBarHiding()
                        .setToolbarColor(context.resources.getColor(R.color.colorPrimaryDark))
                        .setShowTitle(true)
                        .setSecondaryToolbarColor(context.resources.getColor(R.color.colorPrimary))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, Uri.parse("http://discuss.codingblocks.com/t/$DOUBT_ID"))
            }
        }
    }
}
