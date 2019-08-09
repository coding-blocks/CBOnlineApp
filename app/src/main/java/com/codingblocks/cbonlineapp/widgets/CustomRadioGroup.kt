package com.codingblocks.cbonlineapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import androidx.constraintlayout.widget.ConstraintLayout

import com.codingblocks.cbonlineapp.R

class CustomRadioGroup : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is BaseCustomRadioButton) {
            child.setOnClickListener { view ->

                setAllButtonsToUnselectedState()
                setSelectedButtonToSelectedState(child)
                initOnClickListener(child)
            }
        }

        super.addView(child, index, params)
    }

    private fun setAllButtonsToUnselectedState() {
        val container = this

        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)

            if (child is BaseCustomRadioButton) {
                setButtonToUnselectedState(child)
            }
        }
    }

    private fun setButtonToUnselectedState(containerView: BaseCustomRadioButton) {
        val viewWithFilter = 0.5f

        containerView.alpha = viewWithFilter
        containerView.background = resources
                .getDrawable(R.drawable.background_custom_radio_buttons_unselected_state)
    }

    private fun setSelectedButtonToSelectedState(selectedButton: BaseCustomRadioButton) {
        val viewWithoutFilter = 1f

        selectedButton.alpha = viewWithoutFilter
        selectedButton.background = resources
                .getDrawable(R.drawable.background_custom_radio_buttons_selected_state)
    }

    private fun initOnClickListener(selectedButton: View) {
        if (onClickListener != null) {
            onClickListener!!.onClick(selectedButton)
        }
    }

    companion object {

        private var onClickListener: OnCustomRadioButtonListener? = null

        fun setOnClickListener(onClickListener: OnCustomRadioButtonListener) {
            CustomRadioGroup.onClickListener = onClickListener
        }
    }
}
