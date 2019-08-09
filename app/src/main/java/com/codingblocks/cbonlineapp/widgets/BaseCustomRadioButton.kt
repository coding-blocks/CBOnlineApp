package com.codingblocks.cbonlineapp.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater

import androidx.constraintlayout.widget.ConstraintLayout

abstract class BaseCustomRadioButton : ConstraintLayout {

    protected var attrs: AttributeSet? = null
    protected var a: TypedArray? = null
    protected var styleable: IntArray? = null

    constructor(context: Context, layoutResId: Int, styleable: IntArray) : super(context) {

        initLayoutResId(layoutResId)
        initStyleable(styleable)
        initView()
    }

    constructor(context: Context,
                attrs: AttributeSet,
                layoutResId: Int,
                styleable: IntArray) : super(context, attrs) {

        initLayoutResId(layoutResId)
        initStyleable(styleable)
        initAttrs(attrs)
        initView()
    }

    constructor(context: Context,
                attrs: AttributeSet,
                defStyleAttr: Int,
                layoutResId: Int,
                styleable: IntArray) : super(context, attrs, defStyleAttr) {

        initLayoutResId(layoutResId)
        initStyleable(styleable)
        initAttrs(attrs)
        initView()
    }

    private fun initLayoutResId(layoutResId: Int) {
        LayoutInflater.from(context).inflate(layoutResId, this, true)
    }

    private fun initAttrs(attrs: AttributeSet) {
        this.attrs = attrs
    }

    private fun initStyleable(styleable: IntArray) {
        this.styleable = styleable
    }

    private fun initView() {
        initTypedArray()
        bindViews()
        initAttributes()
        populateViews()
    }

    private fun initTypedArray() {
        a = context
                .theme.obtainStyledAttributes(attrs, styleable, 0, 0)
    }

    protected abstract fun bindViews()

    protected abstract fun initAttributes()

    protected abstract fun populateViews()
}

