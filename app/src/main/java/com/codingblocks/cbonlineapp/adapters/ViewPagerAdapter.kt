package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.fragments.QuizFragment
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.QuizResult
import com.codingblocks.onlineapi.models.QuizSubmission
import com.codingblocks.onlineapi.models.Quizqnas
import kotlinx.android.synthetic.main.quizlayout.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import android.app.Activity



class ViewPagerAdapter(var mContext: Context, var quizId: String, var qaId: String, var attemptId: String, private var questionList: HashMap<Int, String>, submission: List<QuizSubmission>?, var result: QuizResult?) : PagerAdapter(), AnkoLogger {
    private lateinit var choiceDataAdapter: QuizChoiceAdapter
    var submissionList: ArrayList<QuizSubmission> = submission as ArrayList<QuizSubmission>


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as ScrollView)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = LayoutInflater.from(mContext).inflate(R.layout.quizlayout, container, false).apply {
        fetchQuestion(position, this)
        container.addView(this)
    }

    private fun fetchQuestion(pos: Int, view: View) {
        if (pos == questionList.size - 1 && result == null) {
            view.submitButton.visibility = View.VISIBLE
        }
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
                        if (result == null) {
                            //marking correct option in the list
                            it.choices!![position].marked = true

                            //unmarking rest of the options
                            it.choices!!.forEachIndexed { index, choice ->
                                if (index != position) {
                                    it.choices!![index].marked = false
                                    choiceDataAdapter.notifyDataSetChanged()
                                }
                            }
                            //adding or removing the previous marked option
                            submissionList.forEach { quizSumbission ->
                                if (quizSumbission.id == questionList[pos]!!) {
                                    quizSumbission.markedChoices = arrayOf(it.choices!![position].id!!)
                                }
                            }
                            val quizAttempt = QuizAttempt()
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
                    }

                })
                submissionList.forEach { quizSumbission ->
                    if (quizSumbission.id == questionList[pos]!!) {
                        quizSumbission.markedChoices?.forEach { markedChoice ->
                            it.choices?.forEach { choice ->
                                if (markedChoice.contains(choice.id!!)) {
                                    choice.marked = true
                                    choiceDataAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                result?.questions?.forEach { quizQuestion ->
                    if (quizQuestion.id == questionList[pos]!!) {
                        quizQuestion.answers?.forEach { answers ->
                            it.choices?.forEach { choice ->
                                if (answers.contains(choice.id!!)) {
                                    choice.correct = true
                                    choiceDataAdapter.notifyDataSetChanged()
                                }else{
                                    choice.correct = false
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
        view.submitButton.setOnClickListener {
            Clients.onlineV2JsonApi.sumbitQuizById(qaId).enqueue(retrofitCallback { throwable, response ->
                (mContext as Activity).finish()
            })
        }

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }

    override fun getCount(): Int {
        return questionList.size
    }
}