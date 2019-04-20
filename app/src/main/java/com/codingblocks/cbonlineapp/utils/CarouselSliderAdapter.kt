package com.codingblocks.cbonlineapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.onlineapi.models.CarouselCards
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_carousel.view.*

class CarouselSliderAdapter(var list: ArrayList<CarouselCards>, var mContext: Context) : PagerAdapter() {


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

//        Picasso.with(mContext).load(list[position]["imgUrl"]).placeholder(R.drawable.default_slider).into(view.courseCoverImgView)
//        view.setOnClickListener {
//            when {
//                list[position]["type"] == "external" -> {
//                    var url = list[position]["target"]!!
//                    if (!url.startsWith("http://")) {
//                        url = "http://$url"
//                    }
//                    val builder = CustomTabsIntent.Builder().setToolbarColor(Color.parseColor("#1589ee"))
//                    val customTabsIntent = builder.build()
//                    customTabsIntent.launchUrl(mContext, Uri.parse(url))
//                }
//                list[position]["type"] == "package" -> fragmentChangeListner?.setPackagesFragment()
//
//                list[position]["type"] == "video" -> mContext.startActivity(mContext.intentFor<VideoPlayerActivity>("videoId" to list[position]["videoId"], "title" to list[position]["title"], "description" to list[position]["description"]).singleTop())
//
//                list[position]["type"] == "highlights" -> mContext.startActivity(mContext.intentFor<HighlightsActivity>().singleTop())
//
//                list[position]["type"] == "rank_predictor" -> mContext.startActivity(mContext.intentFor<RankActivity>().singleTop())
//
//                list[position]["type"] == "quiz" -> mContext.startActivity(mContext.intentFor<QuizrrHome>().singleTop())


        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as FrameLayout)
    }

}
