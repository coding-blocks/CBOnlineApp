package com.codingblocks.cbonlineapp.dashboard.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.adapter.WishlistListener
import com.codingblocks.cbonlineapp.util.extensions.*
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.onlineapi.models.Wishlist
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_course_card_list.view.*
import kotlinx.android.synthetic.main.item_course_wishlist.view.*
import kotlinx.android.synthetic.main.item_course_wishlist.view.chip
import kotlinx.android.synthetic.main.item_course_wishlist.view.courseCardTitleTv
import kotlinx.android.synthetic.main.item_course_wishlist.view.courseLogo
import kotlinx.android.synthetic.main.item_course_wishlist.view.ratingTv
import org.jetbrains.anko.share

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
    var wishlistListener: WishlistListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListItemViewHolder {

        return when(type){
            "LIST"-> WishListItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card_list, parent, false))
            else-> WishListItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_wishlist, parent, false))
        }
    }

    override fun onBindViewHolder(holder: WishListItemViewHolder, position: Int) {
        holder.bind(getItem(position), if (type == "LIST") 0 else 1)
        holder.itemClickListener = onItemClick
        holder.wishlistListener = wishlistListener
    }
}

class WishListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: WishListItemClickListener? = null
    var wishlistListener: WishlistListener? = null

    fun bind(item: Wishlist, type: Int) = with(itemView) {
        with(item.course!!){
            courseLogo.loadImage(logo)
            ViewCompat.setTransitionName(courseLogo, title)
            val ratingText = getSpannableSring("${rating}/5.0","")
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

            if(type == 0){
                courseCardInstructorsTv.isVisible = false
                courseCardMrpTv.isVisible = false
                courseCardPriceTv.isVisible = false
            }else{
                courseCover.loadImage(coverImage?:"")
                course_card_share.setOnClickListener {
                    context.share("Check out the course *${title}* by Coding Blocks!\n\n" +
                        subtitle + "\n" +
                        "https://online.codingblocks.com/courses/${slug}/")
                }
                course_card_like.setOnClickListener {
                    wishlistListener?.onWishListClickListener(
                        id,
                        adapterPosition
                    )
                }
                ratingBar.rating = rating
            }
        }
    }
}

interface WishListItemClickListener {
    fun onClick(id: String, name: String, logo: CircleImageView)
}

