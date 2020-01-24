package com.codingblocks.cbonlineapp.util.widgets

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.TextView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.getSpannableString

class ExpandableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    var originalText: CharSequence? = null
        private set
    private var trimmedText: CharSequence? = null
    private var bufferType: TextView.BufferType? = null
    private var trim = true
    private var trimLength: Int = 0

    private val displayableText: CharSequence?
        get() = if (trim) trimmedText else originalText

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH)
        typedArray.recycle()

        setOnClickListener {
            trim = !trim
            setText()
            requestFocusFromTouch()
        }
    }

    private fun setText() {
        super.setText(displayableText, bufferType)
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        originalText = text
        trimmedText = getTrimmedText()
        bufferType = type
        setText()
    }

    private fun getTrimmedText(): CharSequence? {
        return if (originalText != null && originalText!!.length > trimLength) {
            SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS)
        } else {
            originalText
        }
    }

    fun setTrimLength(trimLength: Int) {
        this.trimLength = trimLength
        trimmedText = getTrimmedText()
        setText()
    }

    fun getTrimLength(): Int {
        return trimLength
    }

    companion object {
        private const val DEFAULT_TRIM_LENGTH = 200
        private val ELLIPSIS = getSpannableString("...more")
    }
}
