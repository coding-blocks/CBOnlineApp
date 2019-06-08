package com.codingblocks.cbonlineapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow

class ToggleButtonGroupTableLayout : TableLayout, OnClickListener {
    private var activeRadioButton: RadioButton? = null

    val checkedRadioButtonId: Int
        get() = if (activeRadioButton != null) {
            activeRadioButton!!.id
        } else -1

    /**
     * @param context
     */
    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO Auto-generated constructor stub
    }

    override fun onClick(v: View) {
        val rb = v as RadioButton
        if (activeRadioButton != null) {
            activeRadioButton!!.isChecked = false
        }
        rb.isChecked = true
        activeRadioButton = rb
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    override fun addView(
        child: View,
        index: Int,
        params: android.view.ViewGroup.LayoutParams
    ) {
        super.addView(child, index, params)
        setChildrenOnClickListener(child as TableRow)
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    override fun addView(child: View, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, params)
        setChildrenOnClickListener(child as TableRow)
    }

    private fun setChildrenOnClickListener(tr: TableRow) {
        val c = tr.childCount
        for (i in 0 until c) {
            val v = tr.getChildAt(i)
            if (v is RadioButton) {
                v.setOnClickListener(this)
            }
        }
    }

    companion object {

        private val TAG = "ToggleButtonGroupTableLayout"
    }
}
