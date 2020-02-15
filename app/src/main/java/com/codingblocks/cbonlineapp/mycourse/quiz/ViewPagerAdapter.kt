package com.codingblocks.cbonlineapp.mycourse.quiz

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.OnItemClickListener
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Choice
import com.codingblocks.onlineapi.models.ContentQna
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.QuizResult
import com.codingblocks.onlineapi.models.QuizSubmission
import kotlinx.android.synthetic.main.quizlayout.view.*
import org.jetbrains.anko.AnkoLogger

class ViewPagerAdapter(private var mContext: Context, private var quizId: String, private var qaId: String, private var questionList: SparseArray<String>, submission: List<QuizSubmission>?, private var result: QuizResult?, val listener: QuizInteractor, private var viewModel: QuizViewModel) : PagerAdapter(), AnkoLogger {
    private lateinit var choiceDataAdapter: QuizChoiceAdapter
    var submissionList: ArrayList<QuizSubmission> = submission as ArrayList<QuizSubmission>

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as ScrollView)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = LayoutInflater.from(mContext).inflate(R.layout.quizlayout, container, false).apply {
        fetchQuestion(viewModel, position, this)
        container.addView(this)
    }

    private fun fetchQuestion(viewModel: QuizViewModel, pos: Int, view: View) {
        if (pos == questionList.size() - 1 && result == null) {
            view.submitButton.visibility = View.VISIBLE
        }
        Clients.onlineV2JsonApi.getQuestionById(questionList[pos]
            ?: "").enqueue(retrofitCallback { _, response ->
            response?.body().let { question ->
                view.questionTitle.text = "Q${pos + 1}. ${question?.title}"
                if (question?.title.equals(question?.description)) {
                    view.questionDescription.visibility = View.GONE
                } else {
                    question?.description?.let {
                        view.questionDescription.text = (it)
                    } ?: run {
                        view.questionDescription.visibility = View.GONE
                    }
                }
                choiceDataAdapter = QuizChoiceAdapter(question?.choices as ArrayList<Choice>, object : OnItemClickListener {
                    override fun onItemClick(position: Int, id: String) {
                        if (result == null) {
                            // marking correct option in the list
                            viewModel.bottomSheetQuizData.value?.get(pos)?.value = true
                            question.choices?.get(position)?.marked = true

                            // unmarking rest of the options
                            question.choices?.forEachIndexed { index, _ ->
                                if (index != position) {
                                    question.choices?.get(index)?.marked = false
                                    choiceDataAdapter.notifyDataSetChanged()
                                }
                            }
                            // adding or removing the previous marked option
                            submissionList.forEach { quizSumbission ->
                                if (quizSumbission.id == questionList[pos]) {
                                    quizSumbission.markedChoices = arrayOf(question.choices?.get(position)?.id
                                        ?: "")
                                }
                            }
                            val quizAttempt = QuizAttempt(qaId, ContentQna(quizId))
                            val quizSubmission = QuizSubmission()
                            quizSubmission.id = questionList[pos] ?: ""
                            quizSubmission.markedChoices = arrayOf(id)
                            submissionList.add(quizSubmission)
                            quizAttempt.submission?.addAll(submissionList)

                            Clients.onlineV2JsonApi.updateQuizAttempt(qaId, quizAttempt).enqueue(retrofitCallback { _, _ ->
                            })
                        }
                    }
                })
                submissionList.forEach { quizSumbission ->
                    if (quizSumbission.id == questionList[pos]) {
                        quizSumbission.markedChoices?.forEach { markedChoice ->
                            question.choices?.forEach { choice ->
                                if (markedChoice.contains(choice.id)) {
                                    choice.marked = true
                                    viewModel.bottomSheetQuizData.value?.get(pos)?.value = true
                                    choiceDataAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                result?.questions?.forEach { quizQuestion ->
                    if (quizQuestion.id == questionList[pos]) {
                        viewModel.bottomSheetQuizData.value?.get(pos)?.value = false
                        quizQuestion.answers?.forEach { answers ->
                            question.choices?.forEach { choice ->
                                if (answers.contains(choice.id)) {
                                    choice.correct = true
                                    viewModel.bottomSheetQuizData.value?.get(pos)?.value = choice.marked
                                    choiceDataAdapter.notifyDataSetChanged()
                                } else {
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
            listener.onQuizSubmitted()
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return questionList.size()
    }

    interface QuizInteractor {
        fun onQuizSubmitted()
    }
}
