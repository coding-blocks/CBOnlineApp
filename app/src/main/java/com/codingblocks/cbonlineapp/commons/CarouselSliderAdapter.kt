package com.codingblocks.cbonlineapp.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.otherwise
import com.codingblocks.onlineapi.models.CarouselCards
import kotlinx.android.synthetic.main.item_carousel.view.*

class CarouselSliderAdapter(var list: ArrayList<CarouselCards>, var mContext: Context?) :
    PagerAdapter() {

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return (p0 == p1)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_carousel, container, false)
        view.title.text = list[position].title
        view.subTitle.text = list[position].subtitle
        view.button.text = list[position].buttonText
        view.imgView.loadImage(list[position].img)
        view.button.setOnClickListener {
            Router.open("activity://courseRun/" + list[position].buttonLink).otherwise {
                it.context.openChrome(list[position].buttonLink)
            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as FrameLayout)
    }
}
