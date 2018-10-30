package com.codingblocks.cbonlineapp.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.expandable_cardview.view.*


class ExpandableCardView1 : LinearLayout {

    private var title: String? = null
    private var description1: String? = null
    private var description2: String? = null
    private var innerView: View? = null
    private var containerView: ViewGroup? = null

    private var arrowBtn: ImageButton? = null
    private var headerIcon: ImageButton? = null
    private var textViewTitle: TextView? = null
    private var textViewDescription1: TextView? = null
    private var textViewDescription2: TextView? = null

    private var typedArray: TypedArray? = null
    private var innerViewRes: Int = 0
    private var iconDrawable: Drawable? = null

    private var card: CardView? = null
    var animDuration = DEFAULT_ANIM_DURATION.toLong()

    var isExpanded = false
        private set
    private var isExpanding = false
    private var isCollapsing = false
    private var expandOnClick = false
    private var startExpanded = false

    private var previousHeight = 0

    private var listener: OnExpandedListener? = null

    private val defaultClickListener = OnClickListener {
        if (isExpanded)
            collapse()
        else
            expand()
    }

    private val isMoving: Boolean
        get() = isExpanding || isCollapsing

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        initAttributes(context, attrs)
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        initAttributes(context, attrs)
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.expandable_cardview, this)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet) {
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView)
        title = typedArray!!.getString(R.styleable.ExpandableCardView_title)
        description1 = typedArray!!.getString(R.styleable.ExpandableCardView_description1)
        description2 = typedArray!!.getString(R.styleable.ExpandableCardView_description2)
        innerViewRes = typedArray!!.getResourceId(R.styleable.ExpandableCardView_inner_view, View.NO_ID)
        expandOnClick = typedArray!!.getBoolean(R.styleable.ExpandableCardView_expandOnClick, false)
        animDuration = typedArray!!.getInteger(R.styleable.ExpandableCardView_animationDuration, DEFAULT_ANIM_DURATION).toLong()
        startExpanded = typedArray!!.getBoolean(R.styleable.ExpandableCardView_startExpanded, false)
        typedArray!!.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        arrowBtn = findViewById(R.id.arrow)
        textViewTitle = findViewById(R.id.title)
//        textViewDescription1 = findViewById(R.id.lectures)
//        textViewDescription2 = findViewById(R.id.lectureTime)

        headerIcon = findViewById(R.id.icon)

        //Setting attributes
        if (!TextUtils.isEmpty(title)) textViewTitle!!.text = title

        if (iconDrawable != null) {
            headerIcon!!.visibility = View.VISIBLE
            headerIcon!!.background = iconDrawable
        }

        card = findViewById(R.id.card)

        setInnerView(innerViewRes)

        containerView = findViewById(R.id.viewContainer)

        elevation = convertDpToPixels(context, 4F)

        if (startExpanded) {
            animDuration = 0
            expand()
            animDuration = animDuration
        }

        if (expandOnClick) {
            card!!.setOnClickListener(defaultClickListener)
            arrowBtn!!.setOnClickListener(defaultClickListener)
        }

    }

    fun expand() {

        val initialHeight = card!!.height

        if (!isMoving) {
            previousHeight = initialHeight
        }

        card!!.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = card!!.measuredHeight

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING)
        }
    }

    fun collapse() {
        val initialHeight = card!!.measuredHeight

        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING)
        }

    }

    private fun animateViews(initialHeight: Int, distance: Int, animationType: Int) {

        isExpanding = false
        isCollapsing = false

        if (listener != null) {
            if (animationType == EXPANDING) {
                listener!!.onExpandChanged(card, true)
            } else {
                listener!!.onExpandChanged(card, false)
            }
        }

//        val expandAnimation = object : Animation() {
//            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
//                if (interpolatedTime == 0.3f) {
//                    //Setting isExpanding/isCollapsing to false
//                    isExpanding = false
//                    isCollapsing = false
//
//                    if (listener != null) {
//                        if (animationType == EXPANDING) {
//                            listener!!.onExpandChanged(card, true)
//                        } else {
//                            listener!!.onExpandChanged(card, false)
//                        }
//                    }
//                }
//
//                card!!.layoutParams.height = if (animationType == EXPANDING)
//                    (initialHeight + distance * interpolatedTime).toInt()
//                else
//                    (initialHeight - distance * interpolatedTime).toInt()
//                card!!.viewContainer.requestLayout()
//
//                containerView!!.layoutParams.height = if (animationType == EXPANDING)
//                    (initialHeight + distance * interpolatedTime).toInt()
//                else
//                    (initialHeight - distance * interpolatedTime).toInt()
//
//            }
//
//            override fun willChangeBounds(): Boolean {
//                return true
//            }
//        }

        val arrowAnimation = if (animationType == EXPANDING)
            RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
        else
            RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)

        arrowAnimation.fillAfter = true


        arrowAnimation.duration = animDuration
//        expandAnimation.duration = animDuration

        isExpanding = animationType == EXPANDING
        isCollapsing = animationType == COLLAPSING

//        startAnimation(expandAnimation)
//        Log.d("SO", "Started animation: " + if (animationType == EXPANDING) "Expanding" else "Collapsing")
        arrowBtn!!.startAnimation(arrowAnimation)
        isExpanded = animationType == EXPANDING

    }

    fun setOnExpandedListener(listener:OnExpandedListener) {
        this.listener = listener
    }

    fun removeOnExpandedListener() {
        this.listener = null
    }

    fun setTitle(title: String) {
        if (textViewTitle != null) textViewTitle!!.text = title
    }

    fun setTitle(resId: Int) {
        if (textViewTitle != null) textViewTitle!!.setText(resId)
    }

    fun setDescription1(title: String) {
        if (textViewDescription1 != null) textViewDescription1!!.text = title
    }

    fun setDescription1(resId: Int) {
        if (textViewDescription1 != null) textViewDescription1!!.setText(resId)
    }

    fun setDescription2(title: String) {
        if (textViewDescription2 != null) textViewDescription2!!.text = title
    }

    fun setDescription2(resId: Int) {
        if (textViewDescription2 != null) textViewDescription2!!.setText(resId)
    }


    private fun setInnerView(resId: Int) {
        val stub = viewStub
        stub.layoutResource = resId
        innerView = stub.inflate()
    }


    override fun setOnClickListener(@Nullable l: View.OnClickListener?) {
        if (arrowBtn != null) arrowBtn!!.setOnClickListener(l)
        super.setOnClickListener(l)
    }


    /**
     * Interfaces
     */

    interface OnExpandedListener {

        fun onExpandChanged(v: View?, isExpanded: Boolean)

    }

    companion object {

        val DEFAULT_ANIM_DURATION = 50

        private val COLLAPSING = 0
        private val EXPANDING = 1
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixels(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

}
