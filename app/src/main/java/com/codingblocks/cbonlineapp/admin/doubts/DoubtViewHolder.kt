package com.codingblocks.cbonlineapp.admin.doubts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.onlineapi.models.Doubts
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DoubtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {

    fun bind(doubt: Doubts) {
        info { doubt.toString() }
    }
}
