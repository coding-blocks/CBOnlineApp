package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.viewpager.widget.PagerAdapter
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.onlineapi.models.CarouselCards
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_carousel.view.*
import org.jetbrains.anko.intentFor

class CarouselSliderAdapter(var list: ArrayList<CarouselCards>, var mContext: Context?) : PagerAdapter() {


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
        if (list[position].img!!.takeLast(3) == "png") {
            Picasso.get().load(list[position].img!!)
                .fit().into(view.imgView)
        } else {
            view.imgView.loadSvg(list[position].img!!)
        }
        view.setOnClickListener {
            if (position == 0)
            //||position== 1 || position==2
            {
                it.context.startActivity(
                    it.context.intentFor<CourseActivity>(
                        "courseId" to list[position].id,
                        "courseLogo" to list[position].img,
                        "courseName" to list[position].title
                    )
                )
            }
            if (position == 1) {
                it.context.startActivity(
                    it.context.intentFor<CourseActivity>(
                        "courseId" to list[position].id,
                        "courseLogo" to list[position].img,
                        "courseName" to list[position].title
                    )
                )

            }
            if (position == 2) {
                it.context.startActivity(
                    it.context.intentFor<CourseActivity>(
                        "courseId" to list[position].id,
                        "courseLogo" to list[position].img,
                        "courseName" to list[position].title
                    ))
            }


            if (position == 3) {
                val url = "https://codingblocks.com/reviews/"
                it.context.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
            }

        }

        view.button.setOnClickListener {
            when {
                list[position].buttonLink == "http://cb.lk/ss" -> {
                    val builder = CustomTabsIntent.Builder().enableUrlBarHiding()
                    mContext?.let {
                        builder.enableUrlBarHiding().setToolbarColor(it.resources.getColor(R.color.colorPrimaryDark))
                    }
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(mContext, Uri.parse(list[position].buttonLink))
                }
                list[position].buttonLink == "http://cb.lk/ss" -> {

                }
                else -> Router.open("activity://course/" + list[position].buttonLink)
            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as FrameLayout)
    }

}
