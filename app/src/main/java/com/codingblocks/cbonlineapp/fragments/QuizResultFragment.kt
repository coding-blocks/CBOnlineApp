package com.codingblocks.cbonlineapp.fragments

import android.content.res.ColorStateList
import android.graphics.Color.BLACK
import android.graphics.Color.GREEN
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.QUIZ_ATTEMPT_ID
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_quiz_result.*

class QuizResultFragment : Fragment() {

    private lateinit var quizAttemptId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_quiz_result, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizAttemptId = it.getString(QUIZ_ATTEMPT_ID)!!
        }

        // change color of
        val totalBackground = total_questions_image.background as GradientDrawable
        totalBackground.color = ColorStateList.valueOf(BLACK)
        val correctBackground = correct_answers_score_image.background as GradientDrawable
        correctBackground.color = ColorStateList.valueOf(GREEN)

        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, response ->
            val questions = response?.body()?.result?.questions
            val totalQuestions = questions?.size
            var correctQuestions = 0
            questions?.forEach { question ->
                if (question.score!! > 0)
                    correctQuestions++
            }
            val wrongQuestions = totalQuestions?.minus(correctQuestions)
            total_questions_image.text = totalQuestions.toString()
            correct_answers_score_image.text = correctQuestions.toString()
            wrong_answers_image.text = wrongQuestions.toString()
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(attemptID: String) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(QUIZ_ATTEMPT_ID, attemptID)
                }
            }
    }

}