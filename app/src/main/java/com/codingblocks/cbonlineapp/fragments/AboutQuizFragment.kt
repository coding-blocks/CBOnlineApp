package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.QuizAttemptListAdapter
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttempt
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import org.jetbrains.anko.AnkoLogger


private const val ARG__QUIZ_ID = "quiz_id"


class AboutQuizFragment : Fragment(), AnkoLogger {

    private lateinit var quizAttemptListAdapter: QuizAttemptListAdapter
    private var attemptList = ArrayList<QuizAttempt>()
    private lateinit var quizId: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_about_quiz, container, false).apply {
        quizAttemptListAdapter = QuizAttemptListAdapter(context, attemptList)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizId = it.getString(ARG__QUIZ_ID)!!
        }
        val header = layoutInflater.inflate(R.layout.quiz_attempt_header, quizAttemptLv, false) as ViewGroup
        quizAttemptLv.addHeaderView(header, null, false)
        quizAttemptLv.adapter = quizAttemptListAdapter
        Clients.onlineV2JsonApi.getQuizById(quizId).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { quiz ->
                quizTitle.text = quiz.description
                quizDescription.text = quiz.title
                quizQuestion.text = "${quiz.questions?.size} Question"
                quizMarks.text = "${quiz.questions?.size} Marks"
                quizType.text = "MCQ"

            }
        })

        Clients.onlineV2JsonApi.getQuizAttempt(quizId).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { attempts ->
                attemptList.addAll(attempts as ArrayList<QuizAttempt>)
                quizAttemptListAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun fetchQuizAttempts() {

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                AboutQuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__QUIZ_ID, param1)
                    }
                }
    }


}
