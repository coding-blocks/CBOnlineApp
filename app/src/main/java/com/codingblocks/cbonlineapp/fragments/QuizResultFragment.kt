package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.ViewPagerAdapter
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_quiz.*

private const val ARG_QUIZ_ATTEMPT_ID = "quiz_attempt_id"

class QuizResultFragment : Fragment() {

    private lateinit var quizAttemptId: String
    private var totalQuestions : Int = 0
    private var correctAnswers : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_quiz_result, container, false).apply {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizAttemptId = it.getString(ARG_QUIZ_ATTEMPT_ID)!!
        }

        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, response ->
            if (response != null) {
                val body = response.body()
                if (body != null) {
                    // TODO : Quiz Result is coming as null, needs to be fixed
                    // Also, the creation time is wrong...
                    Log.v("Created At : ", body.createdAt)
                    val quizResult = body.result
                    Log.v("Null Check : ", (quizResult == null).toString())
                    /**
                     * Logcat Output
                     * V/Created At :: 2019-05-15T09:55:32.864Z
                     * V/Null Check :: true
                     */
                }
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(attemptID : String) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUIZ_ATTEMPT_ID, attemptID)
                }
            }
    }

}
