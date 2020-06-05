package com.codingblocks.cbonlineapp.dashboard.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.*
import com.codingblocks.onlineapi.models.Wishlist
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_course_card.view.*
import kotlinx.android.synthetic.main.item_course_card_secondary.view.chip
import kotlinx.android.synthetic.main.item_course_card_secondary.view.courseCardTitleTv
import kotlinx.android.synthetic.main.item_course_card_secondary.view.courseCover
import kotlinx.android.synthetic.main.item_course_card_secondary.view.courseLogo
import kotlinx.android.synthetic.main.item_course_card_secondary.view.course_card_like
import kotlinx.android.synthetic.main.item_course_card_secondary.view.course_card_share
import kotlinx.android.synthetic.main.item_course_card_secondary.view.ratingTv

class WishListAdapter(val type: String = "") : ListAdapter<Wishlist, WishListItemViewHolder>(
    object : DiffUtil.ItemCallback<Wishlist>() {
        override fun areItemsTheSame(oldItem: Wishlist, newItem: Wishlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wishlist, newItem: Wishlist): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }) {

    var onItemClick: WishListItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListItemViewHolder {

        return when(type){
            "LIST"-> WishListItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card_list, parent, false))
            else-> WishListItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card_secondary, parent, false))
        }
    }

    override fun onBindViewHolder(holder: WishListItemViewHolder, position: Int) {
        holder.bind(getItem(position), if (type == "LIST") 0 else 1)
        holder.itemClickListener = onItemClick
    }
}

class WishListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: WishListItemClickListener? = null

    fun bind(item: Wishlist, type: Int) = with(itemView) {
        with(item.course!!){
            courseLogo.loadImage(logo)
            ViewCompat.setTransitionName(courseLogo, title)
            val ratingText = getSpannableSring("${rating}/5.0", ", ${reviewCount} ratings")
            ratingTv.text = ratingText
            courseCardTitleTv.text = title
            setOnClickListener {
                itemClickListener?.onClick(
                    id, logo, courseLogo
                )
            }

            chip.text = when (difficulty) {
                "0" -> "Beginner"
                "1" -> "Advanced"
                "2" -> "Expert"
                else -> "Beginner"
            }

            if (type == 0){
                if (!item.course?.instructors.isNullOrEmpty())
                    courseCardInstructorsTv.text = getSpannableStringSecondBold("Instructor: ", item.course?.instructors?.first()?.name ?: "")
                else
                    courseCardInstructorsTv.isVisible = false
                var list = item.course?.runs?.filter { run -> !run.unlisted && run.enrollmentEnd.greater() && !run.enrollmentStart.greater() }?.sortedWith(compareBy { run -> run.price })
                if (list.isNullOrEmpty()) {
                    list = item.course?.runs?.sortedWith(compareBy { run -> run.price })
                }
                val price = list?.first()?.price
                if (price == null)
                    courseCardPriceTv.isVisible = false
                courseCardPriceTv.text = if (price == "0") "FREE" else "₹ $price"
                if (list?.first()?.mrp == null)
                    courseCardMrpTv.isVisible = false
                courseCardMrpTv.text = "₹ " + list?.first()?.mrp
                courseCardMrpTv.paintFlags = courseCardPriceTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }else{
                courseCover.loadImage(coverImage?:"")
                course_card_like.isVisible = false
                course_card_share.isVisible = false
            }
        }
    }
}

interface WishListItemClickListener {
    fun onClick(id: String, name: String, logo: CircleImageView)
}

