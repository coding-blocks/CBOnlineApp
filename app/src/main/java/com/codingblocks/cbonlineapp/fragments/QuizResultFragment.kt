package com.codingblocks.cbonlineapp.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.QUIZ_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_quiz_result.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class QuizResultFragment : Fragment() {

    private lateinit var quizId: String
    private lateinit var qnaId: String
    private lateinit var attemptId: String
    private lateinit var quizAttemptId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_quiz_result, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizId = it.getString(QUIZ_ID)
            qnaId = it.getString(QUIZ_QNA)
            attemptId = it.getString(RUN_ATTEMPT_ID)
            quizAttemptId = it.getString(QUIZ_ATTEMPT_ID)
        }

        val totalBackground = total_questions_image.background as GradientDrawable
        // 0x696969
        totalBackground.color = ColorStateList.valueOf(Color.parseColor("#696969"))
        val correctBackground = correct_answers_score_image.background as GradientDrawable
        // 0x90CE8700
        correctBackground.color = ColorStateList.valueOf(Color.parseColor("#90ce87"))

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

        quizResultGoBackBtn.setOnClickListener {
            val fragmentManager = fragmentManager!!
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
            fragmentTransaction.replace(R.id.framelayout_quiz,
                QuizFragment.newInstance(quizId, qnaId, attemptId, quizAttemptId))
            fragmentTransaction.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizId: String, qnaId: String, attemptId: String, quizAttemptId: String) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(QUIZ_ID, quizId)
                    putString(QUIZ_QNA, qnaId)
                    putString(RUN_ATTEMPT_ID, attemptId)
                    putString(QUIZ_ATTEMPT_ID, quizAttemptId)
                }
            }
    }
}
