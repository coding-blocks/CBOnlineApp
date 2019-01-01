package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R

class ViewPagerAdapter(var mContext: Context, var quizId: String, var qaId: String, var questionList: List<String>) : PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = LayoutInflater.from(mContext).inflate(R.layout.quizlayout, container, false).apply {
        container.addView(this)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }

    override fun getCount(): Int {
        return questionList.size
    }
}