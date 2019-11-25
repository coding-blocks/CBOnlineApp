package com.codingblocks.cbonlineapp.admin.doubts

import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.util.extensions.formatDate
import com.codingblocks.onlineapi.models.Doubts
import kotlinx.android.synthetic.main.item_admin_doubt.view.*
import org.jetbrains.anko.AnkoLogger

class DoubtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {

    var ackClickListener: AckClickListener? = null
    var chatClickListener: ChatClickListener? = null
    var resolveClickListener: ResolveClickListener? = null
    var discussClickListener: DiscussClickListener? = null


    fun bind(doubt: Doubts, position: Int) {
        with(itemView) {
            titleTv.text = doubt.title
            descriptionTv.text = doubt.body
            contentChip.text = doubt.content?.title
            if (doubt.status == "PENDING") {
                timeTv.text = "Asked: ${formatDate(doubt.createdAt)}"
                ackBtn.setOnClickListener {
                    ackClickListener?.onClick(doubt.id, doubt)
                }
            } else if (doubt.status == "ACKNOWLEDGED") {
                timeTv.text = "Acknowledged: ${formatDate(doubt.acknowledgedAt ?: "")}"

                /**
                 *Change Visibilities  of Views
                 **/
                ackBtn.isVisible = false
                ackll.isVisible = true
                doubtSv.isVisible = true

                doubtSv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                        resolvBtn.isVisible = position != 0
                        resolvBtn.setOnClickListener {
                            resolveClickListener?.onClick(doubt.id, doubt)
                        }
                    }

                }
                chatBtn.setOnClickListener {
                    chatClickListener?.onClick(doubt.conversationId ?: "")
                }

                discussBtn.setOnClickListener {
                    discussClickListener?.onClick(doubt.discourseTopicId)
                }


            }

        }
    }
}
