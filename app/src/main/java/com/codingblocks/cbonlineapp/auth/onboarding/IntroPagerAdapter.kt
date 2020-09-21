package com.codingblocks.cbonlineapp.auth.onboarding

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.tour_layout.view.*

/**
 * IntroPagerAdapter is an adapter class for IntroScreen ViewPager.
 * transforms introData into views that displays on the screen
 *
 * @param introData is Array of items that adapter needs to use to show up on UI
 */
class IntroPagerAdapter(
    private val introData: Array<Intro>
) : RecyclerView.Adapter<IntroPagerAdapter.IntroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        return IntroViewHolder.from(parent)
    }

    override fun getItemCount(): Int = introData.size

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        val item = introData[position]
        holder.bind(item)
    }

    /**
     * IntroViewHolder is responsible to display one item from item's layout
     * @param itemView view of the current item
     */
    class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Intro) {
            with(itemView) {
                titleTv.text = context.getString(item.title)
                descriptionTV.text = context.getString(item.description)
                val bm = BitmapFactory.decodeResource(context.resources, item.image)
                imageView.setImageBitmap(bm)
            }
        }

        companion object {
            fun from(parent: ViewGroup): IntroViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.tour_layout, parent, false)
                return IntroViewHolder(view)
            }
        }
    }
}
