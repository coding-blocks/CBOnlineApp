package com.codingblocks.cbonlineapp.campaign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.onlineapi.models.Spins
import kotlinx.android.synthetic.main.item_winnings.view.*
import org.jetbrains.anko.toast

class WinningsListAdapter : ListAdapter<Spins, WinningsListAdapter.CampaignViewHolder>(object : DiffUtil.ItemCallback<Spins>() {
    override fun areItemsTheSame(oldItem: Spins, newItem: Spins): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Spins, newItem: Spins): Boolean {
        return oldItem.sameAndEqual(newItem)
    }
}
) {
    private lateinit var myClipboard: ClipboardManager

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        myClipboard = parent.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return CampaignViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_winnings, parent, false)
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class CampaignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Spins) = with(itemView) {
            subTitleTv.text = item.spinPrize?.title
            imgView.loadImage(item.spinPrize?.img ?: "")
            timeTv.text = "Won ${item.usedAt?.timeAgo()}"
            item.prizeRemarksExtra?.couponCreated?.let { referral ->
                codeTv.text = referral

                copy_clipboard.setOnClickListener {
                    val myClip = ClipData.newPlainText("referral", referral)
                    myClipboard.setPrimaryClip(myClip)
                    context.toast("Copied to Clipboard")
                }
                couponCard.isVisible = true
                timeTv.text = "Valid till ${item.prizeRemarksExtra!!.validEnd?.timeAgo()}"
            } ?: run {
                couponCard.isVisible = false
            }
        }
    }
}
