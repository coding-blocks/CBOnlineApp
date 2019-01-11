package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.QuizAttemptListAdapter
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.QuizRunAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast


private const val ARG__QUIZ_ID = "quiz_id"
private const val ARG__ATTEMPT_ID = "attempt_id"


class AboutQuizFragment : Fragment(), AnkoLogger {

    private lateinit var quizAttemptListAdapter: QuizAttemptListAdapter
    private var attemptList = ArrayList<QuizAttempt>()
    private lateinit var quizId: String
    private lateinit var attemptId: String
    lateinit var skeletonScreen: SkeletonScreen


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_about_quiz, container, false).apply {

        quizAttemptListAdapter = QuizAttemptListAdapter(context, attemptList, object : OnItemClickListener {
            override fun onItemClick(position: Int, id: String) {
                intiateQuiz(id)

            }

        })

    }

    private fun intiateQuiz(quizAttemptId: String) {

        val fragmentManager = fragmentManager!!
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//            fragmentManager.addOnBackStackChangedListener(this)
        fragmentTransaction.replace(R.id.framelayout_quiz,
                QuizFragment.newInstance(quizId,
                        attemptId,
                        quizAttemptId), "quiz")
//            fragmentTransaction.addToBackStack("quiz")
        fragmentTransaction.commit()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        skeletonScreen = Skeleton.bind(aboutQuiz)
                .shimmer(true)
                .angle(20)
                .duration(1200)
                .load(R.layout.item_skeleton_course)
                .show()
        arguments?.let {
            quizId = it.getString(ARG__QUIZ_ID)!!
            attemptId = it.getString(ARG__ATTEMPT_ID)!!

        }
        val header = layoutInflater.inflate(R.layout.quiz_attempt_header, quizAttemptLv, false) as ViewGroup
        quizAttemptLv.addHeaderView(header, null, false)
        quizAttemptLv.adapter = quizAttemptListAdapter
        Clients.onlineV2JsonApi.getQuizById(quizId).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { quiz ->
                skeletonScreen.hide()
                quizTitle.text = quiz.title
                quizDescription.text = quiz.description
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

        startQuiz.setOnClickListener {

            val quizAttempt = QuizAttempt()
            val runAttempts = QuizRunAttempt()
            runAttempts.id = attemptId
            val qna = Quizqnas()
            qna.id = quizId
            quizAttempt.qna = qna
            quizAttempt.runAttempt = runAttempts

            Clients.onlineV2JsonApi.createQuizAttempt(quizAttempt).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    intiateQuiz(it?.id ?: "")
                }
                throwable?.localizedMessage.let {
                    if (it != null)
                        toast("Can't Create Quiz.Try Again !!!$it")
                }
            })

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizId: String, attemptId: String) =
                AboutQuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__QUIZ_ID, quizId)
                        putString(ARG__ATTEMPT_ID, attemptId)

                    }
                }
    }


}
