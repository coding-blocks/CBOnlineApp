package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.DoubtsModel

class DoubtsAdapter(private var doubtsData: ArrayList<DoubtsModel>) : RecyclerView.Adapter<DoubtsAdapter.DoubtsViewHolder>() {


    private lateinit var context: Context

    fun setData(doubtsData: ArrayList<DoubtsModel>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context

        return DoubtsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.doubt_item, parent, false))
    }

    override fun getItemCount(): Int {
        return doubtsData.size
    }

    override fun onBindViewHolder(holder: DoubtsViewHolder, position: Int) {
//        holder.bindView(sectionData!![position])
    }

    class DoubtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}