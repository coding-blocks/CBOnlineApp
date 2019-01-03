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
import com.codingblocks.onlineapi.models.QuizSubmission
import com.codingblocks.onlineapi.models.Quizqnas
import kotlinx.android.synthetic.main.quizlayout.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ViewPagerAdapter(var mContext: Context, var quizId: String, var qaId: String, var attemptId: String, private var questionList: HashMap<Int, String>, submission: List<QuizSubmission>?) : PagerAdapter(), AnkoLogger {
    private lateinit var choiceDataAdapter: QuizChoiceAdapter
    var submissionList: ArrayList<QuizSubmission> = submission as ArrayList<QuizSubmission>


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = LayoutInflater.from(mContext).inflate(R.layout.quizlayout, container, false).apply {
        fetchQuestion(position, this)
        container.addView(this)
    }

    private fun fetchQuestion(pos: Int, view: View) {
        Clients.onlineV2JsonApi.getQuestionById(questionList[pos]!!).enqueue(retrofitCallback { throwable, response ->
            response?.body().let { it ->
                view.questionTitle.text = "Q${pos + 1}. ${it?.title}"
                if (it?.title.equals(it?.description)) {
                    view.questionDescription.visibility = View.GONE
                } else {
                    view.questionDescription.loadMarkdown(it?.description)
                }
                choiceDataAdapter = QuizChoiceAdapter(it?.choices!!, object : OnItemClickListener {
                    override fun onItemClick(position: Int, id: String) {
                        info { "quiz${it.choices!![position].id}" }
//TODO need to improvise this
                        it.choices!![position].marked = true
                        choiceDataAdapter.notifyDataSetChanged()
                        val quizAttempt = QuizAttemptModel()
                        quizAttempt.id = qaId
                        quizAttempt.status = "DRAFT"
                        val quizSubmission = QuizSubmission()
                        quizSubmission.id = questionList[pos]!!
                        quizSubmission.markedChoices = arrayOf(id)
                        submissionList.add(quizSubmission)
                        quizAttempt.submission.addAll(submissionList)
                        val qna = Quizqnas()
                        qna.id = quizId
                        quizAttempt.qna = qna
                        Clients.onlineV2JsonApi.updateQuizAttempt(qaId, quizAttempt).enqueue(retrofitCallback { throwable, response ->

                        })
                    }

                })

                submissionList.forEach { quizSumbission ->
                    if (quizSumbission.id == questionList[pos]!!) {
                        quizSumbission.markedChoices?.forEach { markedChoice ->
                            it.choices?.forEach { choice ->
                                info { "Question choice" + choice.id + ">," + markedChoice }
                                if (markedChoice.contains(choice.id!!)) {
                                    choice.marked = true
                                    choiceDataAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                view.questionRv.layoutManager = LinearLayoutManager(mContext)
                view.questionRv.adapter = choiceDataAdapter

            }
        })

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        info { "isViewFromObject" }
        return view == `object`

    }

    override fun getCount(): Int {
        info { "count" }
        return questionList.size
    }
}