package com.codingblocks.cbonlineapp.course.batches

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.getDateForTime
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Runs
import kotlinx.android.synthetic.main.item_run.view.*

class RunListAdapter : ListAdapter<Runs, RunListAdapter.RunsViewHolder>(DiffCallback()) {

    var tracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunListAdapter.RunsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RunsViewHolder(
            inflater.inflate(R.layout.item_run, parent, false))
    }

    override fun onBindViewHolder(holder: RunsViewHolder, position: Int) {
        holder.apply {
            tracker?.let {
                val item = getItem(position)
                bind(item, it.isSelected(item.id))
            }
        }
    }

    inner class RunsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Runs, isActivated: Boolean = false) = with(itemView) {
            priceTv.text = if (item.price == "0") "FREE" else context.getString(R.string.rupee_price, item.price)
            deadlineTv.text = context.getString(R.string.batch_starting, getDateForTime(item.start))

            mrpTv.apply {
                text = context.getString(R.string.rupee_price, item.mrp)
                paintFlags = mrpTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            titleTv.text = item.tier ?: "PREMIUM"
            when (RUNTIERS.valueOf(item.tier ?: "PREMIUM")) {
                RUNTIERS.LITE -> {
                    titleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lite, 0, 0, 0)
                }
                RUNTIERS.PREMIUM -> {
                    titleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_premium, 0, 0, 0)
                }
                RUNTIERS.LIVE -> {
                    titleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live, 0, 0, 0)
                }
                RUNTIERS.CLASSROOM -> {
                    titleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_classroom, 0, 0, 0)
                }
            }
            selectionImg.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = getItem(adapterPosition).id
                override fun inSelectionHotspot(e: MotionEvent): Boolean { return true }
            }
    }
}

enum class RUNTIERS {
    LITE,
    PREMIUM,
    LIVE,
    CLASSROOM
}

class DiffCallback : DiffUtil.ItemCallback<Runs>() {
    override fun areItemsTheSame(oldItem: Runs, newItem: Runs): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Runs, newItem: Runs): Boolean {
        return oldItem.sameAndEqual(newItem)
    }
}
