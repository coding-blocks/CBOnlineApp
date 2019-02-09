package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.DoubtsModel
import kotlinx.android.synthetic.main.doubt_item.view.*

class DoubtsAdapter(private var doubtsData: ArrayList<DoubtsModel>) : RecyclerView.Adapter<DoubtsAdapter.DoubtsViewHolder>() {


    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao

    fun setData(doubtsData: ArrayList<DoubtsModel>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()

        return DoubtsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.doubt_item, parent, false))
    }

    override fun getItemCount(): Int {
        return doubtsData.size
    }

    override fun onBindViewHolder(holder: DoubtsViewHolder, position: Int) {
        holder.bindView(doubtsData[position])
    }

    inner class DoubtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(doubt: DoubtsModel) {
            itemView.doubtTopic.text = contentDao.getContentWithId(doubt.runAttemptId, doubt.contentId).title
            itemView.doubtTitle.text = doubt.title
            itemView.doubtDescription.text = doubt.body
            if (doubt.status == "RESOLVED") {
                itemView.resolveDoubtTv.visibility = View.GONE
            }else{
                itemView.resolveDoubtTv.setOnClickListener {

                }
            }
        }
    }
}