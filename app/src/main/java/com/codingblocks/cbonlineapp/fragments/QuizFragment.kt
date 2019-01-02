package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.ViewPagerAdapter
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_quiz.*
import org.jetbrains.anko.AnkoLogger

private const val ARG__QUIZ_ID = "quiz_id"
private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__QUIZ_ATTEMPT_ID = "quiz_attempt_id"


class QuizFragment : Fragment(), AnkoLogger, ViewPager.OnPageChangeListener {
    private lateinit var quizId: String
    private lateinit var attemptId: String
    private lateinit var quizAttemptId: String

    lateinit var mAdapter: ViewPagerAdapter
    var questionList = HashMap<Int, String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_quiz, container, false).apply {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizId = it.getString(ARG__QUIZ_ID)!!
            attemptId = it.getString(ARG__ATTEMPT_ID)!!
            quizAttemptId = it.getString(ARG__QUIZ_ATTEMPT_ID)!!
        }
        Clients.onlineV2JsonApi.getQuizById(quizId).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { quiz ->
                quiz.questions?.forEachIndexed { index, question ->
                    questionList[index] = question.id ?: ""
                    if (index == quiz.questions!!.size - 1) {
                        mAdapter = ViewPagerAdapter(context!!, quizId, quizAttemptId, attemptId, questionList)
                        quizViewPager.adapter = mAdapter
                        quizViewPager.currentItem = 0
                        quizViewPager.setOnPageChangeListener(this)
                        quizViewPager.offscreenPageLimit = 3
                    }
                }

            }
        })

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    companion object {
        @JvmStatic
        fun newInstance(quizId: String, attemptId: String, quizAttemptId: String) =
                QuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__QUIZ_ID, quizId)
                        putString(ARG__ATTEMPT_ID, attemptId)
                        putString(ARG__QUIZ_ATTEMPT_ID, quizAttemptId)


                    }
                }
    }


}
