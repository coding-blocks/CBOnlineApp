package com.codingblocks.cbonlineapp.mycourse.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QuizFragment : Fragment(), AnkoLogger, ViewPager.OnPageChangeListener, View.OnClickListener, ViewPagerAdapter.QuizInteractor {

    private var isSubmitted: Boolean = false

    private lateinit var mAdapter: ViewPagerAdapter
    private var questionList = SparseArray<String>()
    private var sheetBehavior: BottomSheetBehavior<*>? = null

    private val vm by sharedViewModel<QuizViewModel>()

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
            mAdapter = ViewPagerAdapter(requireContext(), vm.quiz.qnaUid, vm.quizAttemptId, questionList, it?.submission, it?.result, this, vm)
            quizViewPager.adapter = mAdapter
            quizViewPager.currentItem = 0
            quizViewPager.offscreenPageLimit = questionList.size()
            quizViewPager.setOnPageChangeListener(this)
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
        var rowLayout = LinearLayout(context)
        rowLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        numberLayout.addView(rowLayout)
        for (i in 0 until size) {
            if (count == 5) {
                count = 0
                rowLayout = LinearLayout(context)
                rowLayout.orientation = LinearLayout.HORIZONTAL
                rowLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                numberLayout.addView(rowLayout)
            }

            val numberBtn: AppCompatButton = LayoutInflater.from(context).inflate(R.layout.button_quiz_small, rowLayout, false) as AppCompatButton

            vm.bottomSheetQuizData.value?.get(i)?.observer(viewLifecycleOwner) {
                numberBtn.backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.freshGreen))
                numberBtn.textColor = getColor(requireContext(), R.color.freshGreen)
            }

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
                nextBtn.setTextColor(getColor(requireContext(), R.color.orangish))
                prevBtn.setTextColor(getColor(requireContext(), R.color.orangish))
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
                    questionBtn.text = getString(R.string.hide_questions)
                } else {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    questionBtn.text = getString(R.string.view_questions)
                }
            }
        }
    }

    override fun onQuizSubmitted() {
        confirmSubmitQuiz()
    }
}
