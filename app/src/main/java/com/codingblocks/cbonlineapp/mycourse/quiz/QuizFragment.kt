package com.codingblocks.cbonlineapp.mycourse.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_question_sheet.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QuizFragment : BaseCBFragment(), AnkoLogger, ViewPager.OnPageChangeListener,
    View.OnClickListener, ViewPagerAdapter.QuizInteractor {

    private var isSubmitted: Boolean = false

    private lateinit var mAdapter: ViewPagerAdapter
    private var questionList = SparseArray<String>()
    private var sheetBehavior: BottomSheetBehavior<*>? = null

    private val vm by sharedViewModel<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_quiz, container, false)

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
            mAdapter = ViewPagerAdapter(
                requireContext(),
                vm.quiz.qnaUid,
                vm.quizAttemptId,
                questionList,
                it?.submission,
                it?.result,
                this,
                vm
            )
            quizViewPager.adapter = mAdapter
            quizViewPager.currentItem = 0
            quizViewPager.offscreenPageLimit = questionList.size()
            quizViewPager.setOnPageChangeListener(this)
        }
    }

    private fun setupMutableBottomSheetData(size: Int) {
        val tempList = mutableListOf<MutableLiveData<Boolean>>()
        repeat(size) {
            tempList.add(MutableLiveData())
        }
        vm.bottomSheetQuizData.value = tempList
    }

    private fun setUpQuestionBottomSheet(size: Int) {
        var count = 0
        var rowLayout = LinearLayout(context)
        rowLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rowLayout.orientation = LinearLayout.HORIZONTAL
        numberLayout.addView(rowLayout)
        for (i in 0 until size) {
            if (count == 5) {
                count = 0
                rowLayout = LinearLayout(context)
                rowLayout.orientation = LinearLayout.HORIZONTAL
                rowLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                numberLayout.addView(rowLayout)
            }

            val numberBtn: AppCompatButton = LayoutInflater.from(context).inflate(
                R.layout.button_quiz_small,
                rowLayout,
                false
            ) as AppCompatButton

            vm.bottomSheetQuizData.value?.get(i)?.observer(viewLifecycleOwner) {
                numberBtn.backgroundTintList =
                    ColorStateList.valueOf(getColor(requireContext(), R.color.freshGreen))
                numberBtn.textColor = getColor(requireContext(), R.color.freshGreen)
            }

            numberBtn.text = (i + 1).toString()
            numberBtn.setOnClickListener {
                quizViewPager.currentItem = i
            }
            count++
            rowLayout.addView(numberBtn)
        }
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

                nextBtn.text = getString(R.string.end)
                prevBtn.setTextColor(getColor(requireContext(), R.color.orangish))
            }
            position == 0 -> {
                nextBtn.text = getString(R.string.next)
                prevBtn.setTextColor(Color.parseColor("#808080"))
                nextBtn.setTextColor(getColor(requireContext(), R.color.orangish))
            }
            else -> {
                nextBtn.text = getString(R.string.next)
                nextBtn.setTextColor(getColor(requireContext(), R.color.orangish))
                prevBtn.setTextColor(getColor(requireContext(), R.color.orangish))
            }
        }
    }

    private fun submitQuiz() {
        vm.submitQuiz {
            replaceFragmentSafely(
                QuizResultFragment.newInstance(vm.quizAttemptId),
                "result",
                containerViewId = R.id.quizContainer,
                addToStack = true
            )
        }
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
