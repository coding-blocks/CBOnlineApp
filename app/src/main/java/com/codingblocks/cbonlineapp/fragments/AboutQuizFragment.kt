package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.QuizAttemptListAdapter
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_about_quiz.aboutQuiz
import kotlinx.android.synthetic.main.fragment_about_quiz.quizAttemptLv
import kotlinx.android.synthetic.main.fragment_about_quiz.quizDescription
import kotlinx.android.synthetic.main.fragment_about_quiz.quizMarks
import kotlinx.android.synthetic.main.fragment_about_quiz.quizQuestion
import kotlinx.android.synthetic.main.fragment_about_quiz.quizTitle
import kotlinx.android.synthetic.main.fragment_about_quiz.quizType
import kotlinx.android.synthetic.main.fragment_about_quiz.startQuiz
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast

class AboutQuizFragment : Fragment(), AnkoLogger {
    companion object {
        @JvmStatic
        fun newInstance(quizId: String, attemptId: String, qnaId: String) =
            AboutQuizFragment().apply {
                arguments = Bundle().apply {
                    putString(QUIZ_ID, quizId)
                    putString(RUN_ATTEMPT_ID, attemptId)
                    putString(QUIZ_QNA, qnaId)
                }
            }
    }

    private lateinit var quizAttemptListAdapter: QuizAttemptListAdapter
    private var attemptList = ArrayList<QuizAttempt>()
    private lateinit var quizId: String
    private lateinit var attemptId: String
    private lateinit var qnaId: String
    lateinit var skeletonScreen: SkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about_quiz, container, false).apply {
        quizAttemptListAdapter = QuizAttemptListAdapter(attemptList) { attempt: QuizAttempt ->
            initiateQuiz(attempt.id)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        skeletonScreen = Skeleton.bind(aboutQuiz)
            .shimmer(true)
            .angle(20)
            .duration(1200)
            .load(R.layout.item_skeleton_quiz)
            .show()

        arguments?.let {
            quizId = it.getString(QUIZ_ID)
            attemptId = it.getString(RUN_ATTEMPT_ID)
            qnaId = it.getString(QUIZ_QNA)
        }

        quizAttemptLv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = quizAttemptListAdapter
        }

        fetchQuizDetails()
        fetchQuizAttempts()

        startQuiz.setOnClickListener {

            val quizAttempt = QuizAttempt()
            val qna = Quizqnas()
            qna.id = qnaId
            quizAttempt.qna = qna
            quizAttempt.runAttempt = RunAttemptsId(attemptId)

            Clients.onlineV2JsonApi.createQuizAttempt(quizAttempt)
                .enqueue(retrofitCallback { throwable, response ->
                    response?.body().let {
                        initiateQuiz(it?.id ?: "")
                    }
                    throwable?.localizedMessage.let {
                        if (it != null)
                            toast("Can't Create Quiz.Try Again !!!$it")
                    }
                })
        }
    }

    private fun fetchQuizDetails() {
        Clients.onlineV2JsonApi.getQuizById(quizId)
            .enqueue(retrofitCallback { _, response ->
                response?.body()?.let { quiz ->
                    skeletonScreen.hide()
                    quizTitle.text = quiz.title
                    quizDescription.text = quiz.description
                    quizQuestion.text = String.format("%d Question", quiz.questions?.size)
                    quizMarks.text = String.format("%d Marks", quiz.questions?.size)
                    quizType.text = getString(R.string.mcq)
                }
            })
    }

    private fun fetchQuizAttempts() {
        Clients.onlineV2JsonApi.getQuizAttempt(qnaId)
            .enqueue(retrofitCallback { _, response ->
                response?.body()?.let { attempts ->
                    attemptList.clear()
                    attemptList.addAll(attempts as ArrayList<QuizAttempt>)
                    quizAttemptListAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun initiateQuiz(quizAttemptId: String) {
        val fragmentManager = fragmentManager!!
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
        fragmentTransaction.replace(
            R.id.framelayout_quiz,
            QuizFragment.newInstance(quizId, qnaId, attemptId, quizAttemptId), "quiz"
        )
        fragmentTransaction.addToBackStack("")
        fragmentTransaction.commit()
    }
}
