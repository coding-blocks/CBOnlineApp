package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.loadImage
import com.codingblocks.cbonlineapp.extensions.otherwise
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.onlineapi.models.CarouselCards
import kotlinx.android.synthetic.main.item_carousel.view.button
import kotlinx.android.synthetic.main.item_carousel.view.imgView
import kotlinx.android.synthetic.main.item_carousel.view.subTitle
import kotlinx.android.synthetic.main.item_carousel.view.title

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
                Components.openChrome(it.context, list[position].buttonLink)
            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as FrameLayout)
    }
}
