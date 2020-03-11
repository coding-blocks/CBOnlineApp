package com.codingblocks.cbonlineapp.mycourse.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_quiz_result.*

class QuizResultFragment : BaseCBFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_quiz_result, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val totalBackground = total_questions_image.background as GradientDrawable
        // 0x696969
        totalBackground.color = ColorStateList.valueOf(Color.parseColor("#696969"))
        val correctBackground = correct_answers_score_image.background as GradientDrawable
        // 0x90CE8700
        correctBackground.color = ColorStateList.valueOf(Color.parseColor("#90ce87"))

//        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, response ->
//            val questions = response?.body()?.result?.questions
//            val totalQuestions = questions?.size
//            var correctQuestions = 0
//            questions?.forEach { question ->
//                if (question.score!! > 0)
//                    correctQuestions++
//            }
//            val wrongQuestions = totalQuestions?.minus(correctQuestions)
//            total_questions_image.text = totalQuestions.toString()
//            correct_answers_score_image.text = correctQuestions.toString()
//            wrong_answers_image.text = wrongQuestions.toString()
//        })

        quizResultGoBackBtn.setOnClickListener {
            replaceFragmentSafely(QuizFragment(), containerViewId = R.id.quizContainer)
        }
    }
}
