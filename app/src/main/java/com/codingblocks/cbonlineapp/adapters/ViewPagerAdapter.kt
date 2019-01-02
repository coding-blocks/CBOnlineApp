package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttemptModel
import com.codingblocks.onlineapi.models.QuizRunAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import kotlinx.android.synthetic.main.quizlayout.view.*

class ViewPagerAdapter(var mContext: Context, var quizId: String, var qaId: String, var attemptId: String, private var questionList: HashMap<Int, String>) : PagerAdapter() {
    private lateinit var choiceDataAdapter: QuizChoiceAdapter

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
                choiceDataAdapter = QuizChoiceAdapter(it?.choices!!,object : OnItemClickListener{
                    override fun onItemClick(position: Int, id: String) {
                        val quizAttempt = QuizAttemptModel()
                        quizAttempt.id = qaId
                        quizAttempt.status = "DRAFT"
                        quizAttempt.createdAt = System.currentTimeMillis().toString()

                        val qna = Quizqnas()
                        qna.id = quizId
                        quizAttempt.qna = qna


                        Clients.onlineV2JsonApi.updateQuizAttempt(qaId,quizAttempt).enqueue(retrofitCallback { throwable, response ->

                        })
                    }

                })
                view.questionRv.layoutManager = LinearLayoutManager(mContext)
                view.questionRv.adapter = choiceDataAdapter

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