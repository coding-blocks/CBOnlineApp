package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.quizlayout.view.*

class ViewPagerAdapter(var mContext: Context, var quizId: String, var qaId: String, var attemptId: String, private var questionList: HashMap<Int, String>) : PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = LayoutInflater.from(mContext).inflate(R.layout.quizlayout, container, false).apply {
        fetchQuestion(position, this)
        container.addView(this)
    }

    private fun fetchQuestion(pos: Int, view: View) {
        Clients.onlineV2JsonApi.getQuestionById(questionList[pos]!!).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                view.questionTitle.text = "Q${pos + 1}. ${it?.title}"
                if (it?.title.equals(it?.description)) {
                    view.questionDescription.visibility = View.GONE
                } else {
                    view.questionDescription.loadMarkdown(it?.description)
                }

            }
        })

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }

    override fun getCount(): Int {
        return questionList.size
    }
}