package com.codingblocks.cbonlineapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.ViewPagerAdapter
import com.codingblocks.onlineapi.Clients
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_question_sheet.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor


private const val ARG__QUIZ_ID = "quiz_id"
private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__QUIZ_ATTEMPT_ID = "quiz_attempt_id"


class QuizFragment : Fragment(), AnkoLogger, ViewPager.OnPageChangeListener, View.OnClickListener {


    private lateinit var quizId: String
    private lateinit var attemptId: String
    private lateinit var quizAttemptId: String

    lateinit var mAdapter: ViewPagerAdapter
    var questionList = HashMap<Int, String>()
    var sheetBehavior: BottomSheetBehavior<*>? = null


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

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        nextBtn.setOnClickListener(this)
        prevBtn.setOnClickListener(this)
        questionBtn.setOnClickListener(this)

        Clients.onlineV2JsonApi.getQuizById(quizId).enqueue(retrofitCallback { _, response ->
            response?.body()?.let { quiz ->
                setUpQuestionBottomSheet(quiz.questions?.size ?: 0)
                quiz.questions?.forEachIndexed { index, question ->
                    questionList[index] = question.id ?: ""
                    if (index == quiz.questions!!.size - 1) {
                        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, attemptResponse ->
                            attemptResponse?.body().let {
                                mAdapter = ViewPagerAdapter(context!!, quizId, quizAttemptId, attemptId, questionList, it?.submission, it?.result)
                                quizViewPager.adapter = mAdapter
                                quizViewPager.currentItem = 0
                                quizViewPager.setOnPageChangeListener(this)
                                quizViewPager.offscreenPageLimit = 3
                            }
                        })

                    }
                }

            }
        })

    }

    private fun setUpQuestionBottomSheet(size: Int) {
        var count = 0

        val dpValue = 60 // margin in dips
        val d = context!!.resources.displayMetrics.density
        val buttonSize = (dpValue * d).toInt() // margin in pixels
        val buttonParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
        buttonParams.setMargins(buttonSize / 6, buttonSize / 12, buttonSize / 6, buttonSize / 12)
        var rowLayout: LinearLayout
        rowLayout = LinearLayout(context)
        rowLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        numberLayout.addView(rowLayout)
        for (i in 0 until size) {
            if (count == 3) {
                count = 0
                rowLayout = LinearLayout(context)
                rowLayout.orientation = LinearLayout.HORIZONTAL
                rowLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                numberLayout.addView(rowLayout)
            }
            val numberBtn = Button(context)
            numberBtn.background = context!!.getDrawable(R.drawable.button_rounded_background)
            numberBtn.textColor = context!!.resources.getColor(R.color.white)
            numberBtn.layoutParams = buttonParams
            numberBtn.text = (i + 1).toString()
            numberBtn.setOnClickListener {
                quizViewPager.currentItem = i
            }
            count++
            rowLayout.addView(numberBtn)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        when {
            position + 1 == questionList.size -> {

                nextBtn.text = "End"
                prevBtn.setTextColor(Color.parseColor("#000000"))
            }
            position == 0 -> {
                nextBtn.text = "Next"
                prevBtn.setTextColor(Color.parseColor("#808080"))
                nextBtn.setTextColor(Color.parseColor("#000000"))
            }
            else -> {
                nextBtn.text = "Next"
                nextBtn.setTextColor(Color.parseColor("#000000"))
                prevBtn.setTextColor(Color.parseColor("#000000"))

            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nextBtn -> if (nextBtn.text == "End") {
                val fragmentManager = fragmentManager!!
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                fragmentTransaction.replace(R.id.framelayout_quiz,
                        AboutQuizFragment.newInstance(quizId,
                                attemptId), "quiz")
//            fragmentTransaction.addToBackStack("quiz")
                fragmentTransaction.commit()
            } else
                quizViewPager.currentItem = if (quizViewPager.currentItem < questionList.size - 1)
                    quizViewPager.currentItem + 1
                else
                    0

            R.id.prevBtn -> quizViewPager.currentItem = if (quizViewPager.currentItem > 0)
                quizViewPager.currentItem - 1
            else
                0
            R.id.questionBtn -> {
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        }
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
