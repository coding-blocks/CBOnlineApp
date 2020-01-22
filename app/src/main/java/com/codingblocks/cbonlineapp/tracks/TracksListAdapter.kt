package com.codingblocks.cbonlineapp.tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.CareerTracks
import kotlinx.android.synthetic.main.item_track.view.*

class TracksListAdapter : ListAdapter<CareerTracks, TracksListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TracksListAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CareerTracks) = with(itemView) {
            trackTitleTv.text = item.name
            trackLogo.loadImage(item.logo)
            trackCourseNumTv.text = item.courses?.size.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CareerTracks>() {
        override fun areItemsTheSame(oldItem: CareerTracks, newItem: CareerTracks): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CareerTracks, newItem: CareerTracks): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
