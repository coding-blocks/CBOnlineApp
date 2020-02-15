package com.codingblocks.cbonlineapp.auth.onboarding

import android.content.Context
import android.content.res.TypedArray
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.tour_layout.view.*

class IntroPagerAdapter(private val mContext: Context) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = 3

    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): View {
        val title = arrayOf("Learn from the best in\n the industry", "Learn on the go\n anytime, anywhere", "Resolve all your doubts with\n TA’s & Mentors")
        val message = arrayOf("Experienced, engaging instructors take you\n through course material, step by step, in our\n high-quality video lessons.", "You can learn anytime, anywhere with our all\n new android app. Download and save videos\n offline.", "Get exclusive access to Live Webinars where\n you can interact with mentors for important\n course topics & can solve all your doubts.")

        val img: TypedArray = mContext.resources.obtainTypedArray(R.array.tourslide)
        val itemView: View = LayoutInflater.from(mContext).inflate(R.layout.tour_layout, container, false)
        itemView.titleTv.text = title[position]
        itemView.descriptionTV.text = message[position]
        val bm = BitmapFactory.decodeResource(mContext.resources, img.getResourceId(position, 0))
        itemView.imageView.setImageBitmap(bm)
        container.addView(itemView)
        img.recycle()
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ScrollView)
    }
}
