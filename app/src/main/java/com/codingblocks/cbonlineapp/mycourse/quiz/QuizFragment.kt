package com.codingblocks.cbonlineapp.mycourse.quiz

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_question_sheet.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizFragment : Fragment(), AnkoLogger, ViewPager.OnPageChangeListener, View.OnClickListener, ViewPagerAdapter.QuizInteractor {

    private var isSubmitted: Boolean = false

    private lateinit var mAdapter: ViewPagerAdapter
    private var questionList = SparseArray<String>()
    private var sheetBehavior: BottomSheetBehavior<*>? = null

    private val vm by viewModel<QuizViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_quiz, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        nextBtn.setOnClickListener(this)
        prevBtn.setOnClickListener(this)
        questionBtn.setOnClickListener(this)

        vm.quizDetails.observer(viewLifecycleOwner) {
            it.questions?.let { list ->
                setUpQuestionBottomSheet(list.size)
                setupMutableBottomSheetData(list.size)
                list.forEachIndexed { index, question ->
                    questionList.put(index, question.id)
                    if (index == list.size - 1) {
                        vm.getQuizAttempt()
                    }
                }
            }
        }

        vm.quizAttempt.observer(viewLifecycleOwner) {
            //                                mAdapter = ViewPagerAdapter(context!!, qnaId, quizAttemptId, questionList, it?.submission, it?.result, this, viewModel)
//                                quizViewPager.adapter = mAdapter
//                                quizViewPager.currentItem = 0
//                                quizViewPager.offscreenPageLimit = quiz.questions?.size ?: 0
//                                quizViewPager.setOnPageChangeListener(this)
//                                quizViewPager.offscreenPageLimit = 3
        }
    }

    private fun setupMutableBottomSheetData(size: Int) {
        val tempList = mutableListOf<MutableLiveData<Boolean>>()
        repeat(size) {
            tempList.add(MutableLiveData(false))
        }
        vm.bottomSheetQuizData.value = tempList
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

            vm.bottomSheetQuizData.value?.get(i)?.observer(viewLifecycleOwner) {
                numberBtn.background = if (it) context!!.getDrawable(R.drawable.submit_button_background) else context!!.getDrawable(R.drawable.button_rounded_background)
            }

            numberBtn.textColor = context!!.resources.getColor(R.color.white)
            numberBtn.layoutParams = buttonParams
            numberBtn.text = (i + 1).toString()
            numberBtn.setOnClickListener {
                quizViewPager.currentItem = i
            }
            count++
            rowLayout.addView(numberBtn)
        }
        val submitButton = Button(context)
        submitButton.background = context!!.getDrawable(R.drawable.submit_button_background)
        submitButton.textColor = context!!.resources.getColor(R.color.white)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.bottomMargin = 16
        submitButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.submit_image, 0)
        submitButton.compoundDrawablePadding = 16
        params.topMargin = 8
        // submitButton.backgroundColor = context!!.resources.getColor(R.color.colorPrimaryDark)
        submitButton.layoutParams = params
        submitButton.text = "Submit"
        submitButton.setOnClickListener {
            confirmSubmitQuiz()
        }
        numberLayout.addView(submitButton)
        // hide submit button if quiz has already been submitted
//        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { throwable, response ->
//            val body = response?.body()
//            if (body?.status.equals("FINAL")) {
//                isSubmitted = true
//                submitButton.visibility = View.GONE
//            }
//        })
    }

    private fun confirmSubmitQuiz() {
        Components.showConfirmation(requireContext(), "quiz") {
            submitQuiz()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        when {
            position + 1 == questionList.size() -> {

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

    private fun submitQuiz() {
//        Clients.onlineV2JsonApi.sumbitQuizById(quizAttemptId).enqueue(retrofitCallback { throwable, response ->
//            val fragmentManager = fragmentManager!!
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
//            fragmentTransaction.replace(R.id.framelayout_quiz,
//                QuizResultFragment.newInstance(quizId, qnaId, attemptId, quizAttemptId))
//            fragmentTransaction.addToBackStack("")
//            fragmentTransaction.commit()
//        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nextBtn -> if (nextBtn.text == "End" && !isSubmitted) {
                submitQuiz()
            } else
                quizViewPager.currentItem = if (quizViewPager.currentItem < questionList.size() - 1)
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

    override fun onQuizSubmitted() {
        confirmSubmitQuiz()
    }
}
