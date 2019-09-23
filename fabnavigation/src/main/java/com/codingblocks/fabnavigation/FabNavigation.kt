package com.codingblocks.fabnavigation

import android.animation.Animator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.codingblocks.fabnavigation.Helpers.FabHelper
import com.codingblocks.fabnavigation.Helpers.FabNotificationHelper
import com.codingblocks.fabnavigation.behaviours.FabNavigationBehaviour
import com.codingblocks.fabnavigation.behaviours.FabNavigationButtonBehaviour
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class FabNavigation : FrameLayout {

    // Listener
    private var tabSelectedListener: OnTabSelectedListener? = null
    private var navigationPositionListener: OnNavigationPositionListener? = null

    // Variables
    private lateinit var mcontext: Context
    private lateinit var mResources: Resources
    private val items = arrayListOf<FabNavigationItem>()
    private val views = arrayListOf<View>()
    private var bottomNavigationBehavior: FabNavigationBehaviour<FabNavigation>? = null
    private lateinit var linearLayoutContainer: LinearLayout
    private lateinit var backgroundColorView: View
    private lateinit var circleRevealAnim: Animator
    private var colored = false
    private var selectedBackgroundVisible = false
    /**
     * Return if the translucent navigation is enabled
     */
    /**
     * Set the translucent navigation value
     */
    var isTranslucentNavigationEnabled: Boolean = false
    private var notifications = FabNotification.generateEmptyList(MAX_ITEMS)
    private val itemsEnabledStates = arrayOf(true, true, true, true, true)
    private var isBehaviorTranslationSet = false
    private var currentItem = 0
    private var currentColor = 0
    /**
     * Return if the behavior translation is enabled
     *
     * @return a boolean value
     */
    /**
     * Set the behavior translation value
     *
     * @param behaviorTranslationEnabled boolean for the state
     */
    var isBehaviorTranslationEnabled = true
        set(behaviorTranslationEnabled) {
            field = behaviorTranslationEnabled
            if (parent is CoordinatorLayout) {
                val params = layoutParams
                if (bottomNavigationBehavior == null) {
                    bottomNavigationBehavior = FabNavigationBehaviour(behaviorTranslationEnabled, navigationBarHeight)
                } else {
                    bottomNavigationBehavior!!.setBehaviorTranslationEnabled(behaviorTranslationEnabled, navigationBarHeight)
                }
                if (navigationPositionListener != null) {
                    bottomNavigationBehavior!!.setOnNavigationPositionListener(navigationPositionListener!!)
                }
                (params as CoordinatorLayout.LayoutParams).behavior = bottomNavigationBehavior
                if (needHideBottomNavigation) {
                    needHideBottomNavigation = false
                    bottomNavigationBehavior!!.hideView(this, bottomNavigationHeight, hideBottomNavigationWithAnimation)
                }
            }
        }
    private var needHideBottomNavigation = false
    private var hideBottomNavigationWithAnimation = false
    private var soundEffectsEnabled = true

    // Variables (Styles)
    private var titleTypeface: Typeface? = null
    /**
     * Return the bottom navigation background color
     *
     * @return The bottom navigation background color
     */
    /**
     * Set the bottom navigation background color
     *
     * @param defaultBackgroundColor The bottom navigation background color
     */
    var defaultBackgroundColor = Color.WHITE
        set(@ColorInt defaultBackgroundColor) {
            field = defaultBackgroundColor
            createItems()
        }
    private var defaultBackgroundResource = 0
    @ColorInt
    private var itemActiveColor: Int = 0
    @ColorInt
    private var itemInactiveColor: Int = 0
    @ColorInt
    private var titleColorActive: Int = 0
    @ColorInt
    private var itemDisableColor: Int = 0
    @ColorInt
    private var titleColorInactive: Int = 0
    @ColorInt
    private var coloredTitleColorActive: Int = 0
    @ColorInt
    private var coloredTitleColorInactive: Int = 0
    private var titleActiveTextSize: Float = 0.toFloat()
    private var titleInactiveTextSize: Float = 0.toFloat()
    private var bottomNavigationHeight: Int = 0
    private var navigationBarHeight = 0
    private var selectedItemWidth: Float = 0.toFloat()
    private var notSelectedItemWidth: Float = 0.toFloat()
    /**
     * Return if the tint should be forced (with setColorFilter)
     *
     * @return Boolean
     */
    /**
     * Set the force tint value
     * If forceTint = true, the tint is made with drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
     *
     * @param forceTint Boolean
     */
    var isForceTint = true
        set(forceTint) {
            field = forceTint
            createItems()
        }
    /**
     * Return the title state for display
     *
     * @return TitleState
     */
    /**
     * Sets the title state for each tab
     * SHOW_WHEN_ACTIVE: when a tab is focused
     * ALWAYS_SHOW: show regardless of which tab is in focus
     * ALWAYS_HIDE: never show tab titles
     * Note: Always showing the title is against Material Design guidelines
     *
     * @param titleState TitleState
     */
    var titleState = TitleState.SHOW_WHEN_ACTIVE
        set(titleState) {
            field = titleState
            createItems()
        }

    // Notifications
    @ColorInt
    private var notificationTextColor: Int = 0
    @ColorInt
    private var notificationBackgroundColor: Int = 0
    private var notificationBackgroundDrawable: Drawable? = null
    private var notificationTypeface: Typeface? = null
    private var notificationActiveMarginLeft: Int = 0
    private var notificationInactiveMarginLeft: Int = 0
    private var notificationActiveMarginTop: Int = 0
    private var notificationInactiveMarginTop: Int = 0
    private var notificationAnimationDuration: Long = 0

    // updated

    /**
     * Check if items must be classic
     *
     * @return true if classic (icon + title)
     */
    private val isClassic: Boolean
        get() = (this.titleState != TitleState.ALWAYS_HIDE &&
            this.titleState != TitleState.SHOW_WHEN_ACTIVE_FORCE &&
            (items.size == MIN_ITEMS || this.titleState == TitleState.ALWAYS_SHOW))

    /**
     * Return the number of items
     *
     * @return int
     */
    val itemsCount: Int
        get() = items.size

    /**
     * Return if the Bottom Navigation is colored
     */
    /**
     * Set if the Bottom Navigation is colored
     */
    var isColored: Boolean
        get() = colored
        set(colored) {
            this.colored = colored
            this.itemActiveColor = if (colored) coloredTitleColorActive else titleColorActive
            this.itemInactiveColor = if (colored) coloredTitleColorInactive else titleColorInactive
            createItems()
        }

    /**
     * Get the accent color (used when the view contains 3 items)
     *
     * @return The default accent color
     */
    /**
     * Set the accent color (used when the view contains 3 items)
     *
     * @param accentColor The new accent color
     */
    var accentColor: Int
        get() = itemActiveColor
        set(accentColor) {
            this.titleColorActive = accentColor
            this.itemActiveColor = accentColor
            createItems()
        }

    /**
     * Get the inactive color (used when the view contains 3 items)
     *
     * @return The inactive color
     */
    /**
     * Set the inactive color (used when the view contains 3 items)
     *
     * @param inactiveColor The inactive color
     */
    var inactiveColor: Int
        get() = itemInactiveColor
        set(inactiveColor) {
            this.titleColorInactive = inactiveColor
            this.itemInactiveColor = inactiveColor
            createItems()
        }

    /**
     * Return if the Bottom Navigation is hidden or not
     */
    val isHidden: Boolean
        get() = if (bottomNavigationBehavior != null) {
            bottomNavigationBehavior!!.isHidden
        } else false

    // Title state
    enum class TitleState {
        SHOW_WHEN_ACTIVE,
        SHOW_WHEN_ACTIVE_FORCE,
        ALWAYS_SHOW,
        ALWAYS_HIDE
    }

    /**
     * Constructors
     */
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun setSoundEffectsEnabled(soundEffectsEnabled: Boolean) {
        super.setSoundEffectsEnabled(soundEffectsEnabled)
        this.soundEffectsEnabled = soundEffectsEnabled
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createItems()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isBehaviorTranslationSet) {
            //The translation behavior has to be set up after the super.onMeasure has been called.
            isBehaviorTranslationEnabled = isBehaviorTranslationEnabled
            isBehaviorTranslationSet = true
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("current_item", currentItem)
        bundle.putParcelableArrayList("notifications", ArrayList(notifications))
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) {
            val bundle = state as Bundle?
            currentItem = bundle!!.getInt("current_item")
            notifications = bundle.getParcelableArrayList("notifications")!!
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    /////////////
    // PRIVATE //
    /////////////

    /**
     * Init
     *
     * @param context
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        this.mcontext = context
        mResources = this.mcontext.resources

        // Item colors
        titleColorActive = ContextCompat.getColor(context, R.color.colorBottomNavigationAccent)
        titleColorInactive = ContextCompat.getColor(context, R.color.colorBottomNavigationInactive)
        itemDisableColor = ContextCompat.getColor(context, R.color.colorBottomNavigationDisable)

        // Colors for colored bottom navigation
        coloredTitleColorActive = ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored)
        coloredTitleColorInactive = ContextCompat.getColor(context, R.color.colorBottomNavigationInactiveColored)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AHBottomNavigationBehavior_Params, 0, 0)
            try {
                selectedBackgroundVisible = ta.getBoolean(R.styleable.AHBottomNavigationBehavior_Params_selectedBackgroundVisible, false)
                isTranslucentNavigationEnabled = ta.getBoolean(R.styleable.AHBottomNavigationBehavior_Params_translucentNavigationEnabled, false)

                titleColorActive = ta.getColor(R.styleable.AHBottomNavigationBehavior_Params_accentColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationAccent))
                titleColorInactive = ta.getColor(R.styleable.AHBottomNavigationBehavior_Params_inactiveColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationInactive))
                itemDisableColor = ta.getColor(R.styleable.AHBottomNavigationBehavior_Params_disableColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationDisable))

                coloredTitleColorActive = ta.getColor(R.styleable.AHBottomNavigationBehavior_Params_coloredActive,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored))
                coloredTitleColorInactive = ta.getColor(R.styleable.AHBottomNavigationBehavior_Params_coloredInactive,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationInactiveColored))

                colored = ta.getBoolean(R.styleable.AHBottomNavigationBehavior_Params_colored, false)

            } finally {
                ta.recycle()
            }
        }

        notificationTextColor = ContextCompat.getColor(context, android.R.color.white)
        bottomNavigationHeight = mResources.getDimension(R.dimen.bottom_navigation_height).toInt()

        itemActiveColor = titleColorActive
        itemInactiveColor = titleColorInactive

        // Notifications
        notificationActiveMarginLeft = mResources.getDimension(R.dimen.bottom_navigation_notification_margin_left_active).toInt()
        notificationInactiveMarginLeft = mResources.getDimension(R.dimen.bottom_navigation_notification_margin_left).toInt()
        notificationActiveMarginTop = mResources.getDimension(R.dimen.bottom_navigation_notification_margin_top_active).toInt()
        notificationInactiveMarginTop = mResources.getDimension(R.dimen.bottom_navigation_notification_margin_top).toInt()
        notificationAnimationDuration = 150

        ViewCompat.setElevation(this, mResources.getDimension(R.dimen.bottom_navigation_elevation))

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, bottomNavigationHeight)
        layoutParams = params
    }

    /**
     * Create the items in the bottom navigation
     */
    private fun createItems() {
        if (items.size < MIN_ITEMS) {
            Log.w(TAG, "The items list should have at least 3 items")
        } else if (items.size > MAX_ITEMS) {
            Log.w(TAG, "The items list should not have more than 5 items")
        }

        val layoutHeight = mResources.getDimension(R.dimen.bottom_navigation_height).toInt()

        removeAllViews()

        views.clear()
        backgroundColorView = View(mcontext)
        val backgroundLayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, calculateHeight(layoutHeight))
        addView(backgroundColorView, backgroundLayoutParams)
        bottomNavigationHeight = layoutHeight

        linearLayoutContainer = LinearLayout(mcontext)
        linearLayoutContainer.orientation = LinearLayout.HORIZONTAL
        linearLayoutContainer.gravity = Gravity.CENTER

        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight)
        addView(linearLayoutContainer, layoutParams)

        if (isClassic) {
            createClassicItems(linearLayoutContainer)
        } else {
            createSmallItems(linearLayoutContainer)
        }

        // Force a request layout after all the items have been created
        post { requestLayout() }
    }

    @SuppressLint("NewApi", "ResourceType")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun calculateHeight(layoutHeight: Int): Int {
        var layoutHeight = layoutHeight
        if (!isTranslucentNavigationEnabled) return layoutHeight

        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = mResources.getDimensionPixelSize(resourceId)
        }

        val attrs = intArrayOf(android.R.attr.fitsSystemWindows, android.R.attr.windowTranslucentNavigation)
        val typedValue = context.theme.obtainStyledAttributes(attrs)

        val fitWindow = typedValue.getBoolean(0, false)

        val translucentNavigation = typedValue.run { getBoolean(1, true) }

        if (hasImmersive() /*&& !fitWindow*/ && translucentNavigation) {
            layoutHeight += navigationBarHeight
        }

        typedValue.recycle()

        return layoutHeight
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasImmersive(): Boolean {
        val d = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

        val realDisplayMetrics = DisplayMetrics()
        d.getRealMetrics(realDisplayMetrics)

        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels

        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)

        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels

        return realWidth > displayWidth || realHeight > displayHeight
    }

    /**
     * Create classic items (only 3 items in the bottom navigation)
     *
     * @param linearLayout The layout where the items are added
     */
    private fun createClassicItems(linearLayout: LinearLayout) {

        val inflater = mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val height = mResources.getDimension(R.dimen.bottom_navigation_height)
        var minWidth = mResources.getDimension(R.dimen.bottom_navigation_min_width)
        var maxWidth = this.mResources.getDimension(R.dimen.bottom_navigation_max_width)

        if (this.titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            minWidth = mResources.getDimension(R.dimen.bottom_navigation_small_inactive_min_width)
            maxWidth = mResources.getDimension(R.dimen.bottom_navigation_small_inactive_max_width)
        }

        val layoutWidth = width - paddingLeft - paddingRight
        if (layoutWidth == 0 || items.size == 0) {
            return
        }

        var itemWidth = (layoutWidth / items.size).toFloat()
        if (itemWidth < minWidth) {
            itemWidth = minWidth
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth
        }

        var activeSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_active)
        var inactiveSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_inactive)
        val activePaddingTop = mResources.getDimension(R.dimen.bottom_navigation_margin_top_active).toInt()

        if (titleActiveTextSize != 0f && titleInactiveTextSize != 0f) {
            activeSize = titleActiveTextSize
            inactiveSize = titleInactiveTextSize
        } else if (this.titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            activeSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_forced_active)
            inactiveSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive)
        }

        var iconDrawable: Drawable
        for (i in items.indices) {
            val current = currentItem == i
            val item = items[i]

            val view = inflater.inflate(R.layout.bottom_navigation_item, this, false)
            val container = view.findViewById(R.id.bottom_navigation_container) as FrameLayout
            val icon = view.findViewById(R.id.bottom_navigation_item_icon) as ImageView
            val title = view.findViewById(R.id.bottom_navigation_item_title) as TextView
            val notification = view.findViewById(R.id.bottom_navigation_notification) as TextView

            icon.setImageDrawable(item.getDrawable(mcontext))
            title.setText(item.getTitle(mcontext))

            if (titleTypeface != null) {
                title.setTypeface(titleTypeface)
            }

            if (this.titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
                container.setPadding(0, container.paddingTop, 0, container.paddingBottom)
            }

            if (current) {
                if (selectedBackgroundVisible) {
                    view.setSelected(true)
                }
                icon.isSelected = true
                // Update margins (icon & notification)
                if (view.getLayoutParams() is MarginLayoutParams) {
                    val p = icon.layoutParams as MarginLayoutParams
                    p.setMargins(p.leftMargin, activePaddingTop, p.rightMargin, p.bottomMargin)

                    val paramsNotification = notification.layoutParams as MarginLayoutParams
                    paramsNotification.setMargins(notificationActiveMarginLeft, paramsNotification.topMargin,
                        paramsNotification.rightMargin, paramsNotification.bottomMargin)

                    view.requestLayout()
                }
            } else {
                icon.isSelected = false
                val paramsNotification = notification.layoutParams as MarginLayoutParams
                paramsNotification.setMargins(notificationInactiveMarginLeft, paramsNotification.topMargin,
                    paramsNotification.rightMargin, paramsNotification.bottomMargin)
            }

            if (colored) {
                if (current) {
                    setBackgroundColor(item.getColor(mcontext))
                    currentColor = item.getColor(mcontext)
                }
            } else {
                if (defaultBackgroundResource != 0) {
                    setBackgroundResource(defaultBackgroundResource)
                } else {
                    setBackgroundColor(this.defaultBackgroundColor)
                }
            }

            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, if (current) activeSize else inactiveSize)

            if (itemsEnabledStates[i]) {
                view.setOnClickListener(OnClickListener { updateItems(i, true) })
                iconDrawable = if (isForceTint)
                    FabHelper.getTintDrawable(items[i].getDrawable(mcontext),
                        if (current) itemActiveColor else itemInactiveColor, isForceTint)
                else
                    items[i].getDrawable(mcontext)
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(if (current) itemActiveColor else itemInactiveColor)
                view.isSoundEffectsEnabled = soundEffectsEnabled
                view.isEnabled = true
            } else {
                iconDrawable = if (isForceTint)
                    FabHelper.getTintDrawable(items[i].getDrawable(mcontext),
                        itemDisableColor, isForceTint)
                else
                    items[i].getDrawable(mcontext)
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(itemDisableColor)
                view.isClickable = true
                view.isEnabled = false
            }

            val params = LayoutParams(itemWidth.toInt(), height.toInt())
            linearLayout.addView(view, params)
            views.add(view)
        }

        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Create small items (more than 3 items in the bottom navigation)
     *
     * @param linearLayout The layout where the items are added
     */
    private fun createSmallItems(linearLayout: LinearLayout) {

        val inflater = mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val height = mResources.getDimension(R.dimen.bottom_navigation_height)
        val minWidth = mResources.getDimension(R.dimen.bottom_navigation_small_inactive_min_width)
        val maxWidth = mResources.getDimension(R.dimen.bottom_navigation_small_inactive_max_width)

        val layoutWidth = width - paddingLeft - paddingRight
        if (layoutWidth == 0 || items.size == 0) {
            return
        }

        var itemWidth = (layoutWidth / items.size).toFloat()

        if (itemWidth < minWidth) {
            itemWidth = minWidth
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth
        }

        val activeMarginTop = mResources.getDimension(R.dimen.bottom_navigation_small_margin_top_active).toInt()
        val difference = mResources.getDimension(R.dimen.bottom_navigation_small_selected_width_difference)

        selectedItemWidth = itemWidth + items.size * difference
        itemWidth -= difference
        notSelectedItemWidth = itemWidth

        var iconDrawable: Drawable
        for (i in items.indices) {

            val item = items[i]

            val view = inflater.inflate(R.layout.bottom_navigation_small_item, this, false)
            val icon = view.findViewById(R.id.bottom_navigation_small_item_icon) as ImageView
            val title = view.findViewById(R.id.bottom_navigation_small_item_title) as TextView
            val notification = view.findViewById(R.id.bottom_navigation_notification) as TextView
            icon.setImageDrawable(item.getDrawable(mcontext))

            if (this.titleState != TitleState.ALWAYS_HIDE) {
                title.text = item.getTitle(mcontext)
            }

            if (titleActiveTextSize != 0f) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleActiveTextSize)
            }

            if (titleTypeface != null) {
                title.setTypeface(titleTypeface)
            }

            if (i == currentItem) {
                if (selectedBackgroundVisible) {
                    view.isSelected = true
                }
                icon.isSelected = true
                // Update margins (icon & notification)

                if (this.titleState != TitleState.ALWAYS_HIDE) {
                    if (view.layoutParams is MarginLayoutParams) {
                        val p = icon.layoutParams as MarginLayoutParams
                        p.setMargins(p.leftMargin, activeMarginTop, p.rightMargin, p.bottomMargin)

                        val paramsNotification = notification.layoutParams as MarginLayoutParams
                        paramsNotification.setMargins(notificationActiveMarginLeft, notificationActiveMarginTop,
                            paramsNotification.rightMargin, paramsNotification.bottomMargin)

                        view.requestLayout()
                    }
                }
            } else {
                icon.isSelected = false
                val paramsNotification = notification.layoutParams as MarginLayoutParams
                paramsNotification.setMargins(notificationInactiveMarginLeft, notificationInactiveMarginTop,
                    paramsNotification.rightMargin, paramsNotification.bottomMargin)
            }

            if (colored) {
                if (i == currentItem) {
                    setBackgroundColor(item.getColor(mcontext))
                    currentColor = item.getColor(mcontext)
                }
            } else {
                if (defaultBackgroundResource != 0) {
                    setBackgroundResource(defaultBackgroundResource)
                } else {
                    setBackgroundColor(this.defaultBackgroundColor)
                }
            }

            if (itemsEnabledStates[i]) {
                iconDrawable = if (isForceTint)
                    FabHelper.getTintDrawable(items[i].getDrawable(mcontext),
                        if (currentItem == i) itemActiveColor else itemInactiveColor, isForceTint)
                else
                    items[i].getDrawable(mcontext)
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(if (currentItem == i) itemActiveColor else itemInactiveColor)
                title.alpha = (if (currentItem == i) 1 else 0).toFloat()
                view.setOnClickListener({ updateSmallItems(i, true) })
                view.isSoundEffectsEnabled = soundEffectsEnabled
                view.isEnabled = true
            } else {
                iconDrawable = if (isForceTint)
                    FabHelper.getTintDrawable(items[i].getDrawable(mcontext),
                        itemDisableColor, isForceTint)
                else
                    items[i].getDrawable(mcontext)
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(itemDisableColor)
                title.alpha = 0f
                view.isClickable = true
                view.isEnabled = false
            }

            var width = if (i == currentItem)
                selectedItemWidth.toInt()
            else
                itemWidth.toInt()

            if (this.titleState == TitleState.ALWAYS_HIDE) {
                width = (itemWidth * 1.16).toInt()
            }

            val params = LayoutParams(width, height.toInt())
            linearLayout.addView(view, params)
            views.add(view)
        }

        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }


    /**
     * Update Items UI
     *
     * @param itemIndex   int: Selected item position
     * @param useCallback boolean: Use or not the callback
     */
    private fun updateItems(itemIndex: Int, useCallback: Boolean) {

        if (currentItem == itemIndex) {
            if (tabSelectedListener != null && useCallback) {
                tabSelectedListener!!.onTabSelected(itemIndex, true)
            }
            return
        }

        if (tabSelectedListener != null && useCallback) {
            val selectionAllowed = tabSelectedListener!!.onTabSelected(itemIndex, false)
            if (!selectionAllowed) return
        }

        val activeMarginTop = mResources.getDimension(R.dimen.bottom_navigation_margin_top_active).toInt()
        val inactiveMarginTop = mResources.getDimension(R.dimen.bottom_navigation_margin_top_inactive).toInt()
        var activeSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_active)
        var inactiveSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_inactive)

        if (titleActiveTextSize != 0f && titleInactiveTextSize != 0f) {
            activeSize = titleActiveTextSize
            inactiveSize = titleInactiveTextSize
        } else if (this.titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            activeSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_forced_active)
            inactiveSize = mResources.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive)
        }

        for (i in views.indices) {

            val view = views[i]
            if (selectedBackgroundVisible) {
                view.isSelected = i == itemIndex
            }

            if (i == itemIndex) {

                val title = view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
                val notification = view.findViewById<View>(R.id.bottom_navigation_notification) as TextView

                icon.isSelected = true
                FabHelper.updateTopMargin(icon, inactiveMarginTop, activeMarginTop)
                FabHelper.updateLeftMargin(notification, notificationInactiveMarginLeft, notificationActiveMarginLeft)
                FabHelper.updateTextColor(title, itemInactiveColor, itemActiveColor)
                FabHelper.updateTextSize(title, inactiveSize, activeSize)
                if (isForceTint) {
                    FabHelper.updateDrawableColor(mcontext, items[itemIndex].getDrawable(mcontext), icon,
                        itemInactiveColor, itemActiveColor, isForceTint)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {

                    val finalRadius = Math.max(width, height)
                    val cx = view.x.toInt() + view.width / 2
                    val cy = view.height / 2

                    if (circleRevealAnim.isRunning) {
                        circleRevealAnim.cancel()
                        setBackgroundColor(items[itemIndex].getColor(mcontext))
                        backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                    }

                    circleRevealAnim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0f, finalRadius.toFloat())
                    circleRevealAnim.startDelay = 5
                    circleRevealAnim.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            backgroundColorView.setBackgroundColor(items[itemIndex].getColor(mcontext))
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            setBackgroundColor(items[itemIndex].getColor(mcontext))
                            backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                        }

                        override fun onAnimationCancel(animation: Animator) {}

                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                    circleRevealAnim.start()
                } else if (colored) {
                    FabHelper.updateViewBackgroundColor(this, currentColor,
                        items[itemIndex].getColor(mcontext))
                } else {
                    if (defaultBackgroundResource != 0) {
                        setBackgroundResource(defaultBackgroundResource)
                    } else {
                        setBackgroundColor(this.defaultBackgroundColor)
                    }
                    backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                }

            } else if (i == currentItem) {

                val title = view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
                val notification = view.findViewById<View>(R.id.bottom_navigation_notification) as TextView

                icon.isSelected = false
                FabHelper.updateTopMargin(icon, activeMarginTop, inactiveMarginTop)
                FabHelper.updateLeftMargin(notification, notificationActiveMarginLeft, notificationInactiveMarginLeft)
                FabHelper.updateTextColor(title, itemActiveColor, itemInactiveColor)
                FabHelper.updateTextSize(title, activeSize, inactiveSize)
                if (isForceTint) {
                    FabHelper.updateDrawableColor(mcontext, items[currentItem].getDrawable(mcontext), icon,
                        itemActiveColor, itemInactiveColor, isForceTint)
                }
            }
        }

        currentItem = itemIndex
        if (currentItem > 0 && currentItem < items.size) {
            currentColor = items[currentItem].getColor(mcontext)
        } else if (currentItem == CURRENT_ITEM_NONE) {
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(this.defaultBackgroundColor)
            }
            backgroundColorView!!.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Update Small items UI
     *
     * @param itemIndex   int: Selected item position
     * @param useCallback boolean: Use or not the callback
     */
    private fun updateSmallItems(itemIndex: Int, useCallback: Boolean) {

        if (currentItem == itemIndex) {
            if (tabSelectedListener != null && useCallback) {
                tabSelectedListener!!.onTabSelected(itemIndex, true)
            }
            return
        }

        if (tabSelectedListener != null && useCallback) {
            val selectionAllowed = tabSelectedListener!!.onTabSelected(itemIndex, false)
            if (!selectionAllowed) return
        }

        val activeMarginTop = mResources.getDimension(R.dimen.bottom_navigation_small_margin_top_active).toInt()
        val inactiveMargin = mResources.getDimension(R.dimen.bottom_navigation_small_margin_top).toInt()

        for (i in views.indices) {

            val view = views[i]
            if (selectedBackgroundVisible) {
                view.isSelected = i == itemIndex
            }

            if (i == itemIndex) {

                val container = view.findViewById<View>(R.id.bottom_navigation_small_container) as FrameLayout
                val title = view.findViewById<View>(R.id.bottom_navigation_small_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_small_item_icon) as ImageView
                val notification = view.findViewById<View>(R.id.bottom_navigation_notification) as TextView

                icon.isSelected = true

                if (this.titleState != TitleState.ALWAYS_HIDE) {
                    FabHelper.updateTopMargin(icon, inactiveMargin, activeMarginTop)
                    FabHelper.updateLeftMargin(notification, notificationInactiveMarginLeft, notificationActiveMarginLeft)
                    FabHelper.updateTopMargin(notification, notificationInactiveMarginTop, notificationActiveMarginTop)
                    FabHelper.updateTextColor(title, itemInactiveColor, itemActiveColor)
                    FabHelper.updateWidth(container, notSelectedItemWidth, selectedItemWidth)
                }

                FabHelper.updateAlpha(title, 0F, 1F)
                if (isForceTint) {
                    FabHelper.updateDrawableColor(mcontext, items[itemIndex].getDrawable(mcontext), icon,
                        itemInactiveColor, itemActiveColor, isForceTint)
                }

                if (colored) {
                    val finalRadius = max(width, height)
                    val cx = views[itemIndex].x.toInt() + views[itemIndex].width / 2
                    val cy = views[itemIndex].height / 2

                    if (circleRevealAnim.isRunning) {
                        circleRevealAnim.cancel()
                        setBackgroundColor(items[itemIndex].getColor(mcontext))
                        backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                    }

                    circleRevealAnim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0f, finalRadius.toFloat())
                    circleRevealAnim.startDelay = 5
                    circleRevealAnim.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            backgroundColorView.setBackgroundColor(items[itemIndex].getColor(mcontext))
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            setBackgroundColor(items[itemIndex].getColor(mcontext))
                            backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                        }

                        override fun onAnimationCancel(animation: Animator) {}

                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                    circleRevealAnim.start()
                } else if (colored) {
                    FabHelper.updateViewBackgroundColor(this, currentColor,
                        items[itemIndex].getColor(mcontext))
                } else {
                    if (defaultBackgroundResource != 0) {
                        setBackgroundResource(defaultBackgroundResource)
                    } else {
                        setBackgroundColor(this.defaultBackgroundColor)
                    }
                    backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
                }

            } else if (i == currentItem) {

                val container = view.findViewById<View>(R.id.bottom_navigation_small_container)
                val title = view.findViewById<View>(R.id.bottom_navigation_small_item_title) as TextView
                val icon = view.findViewById<View>(R.id.bottom_navigation_small_item_icon) as ImageView
                val notification = view.findViewById<View>(R.id.bottom_navigation_notification) as TextView

                icon.isSelected = false

                if (this.titleState != TitleState.ALWAYS_HIDE) {
                    FabHelper.updateTopMargin(icon, activeMarginTop, inactiveMargin)
                    FabHelper.updateLeftMargin(notification, notificationActiveMarginLeft, notificationInactiveMarginLeft)
                    FabHelper.updateTopMargin(notification, notificationActiveMarginTop, notificationInactiveMarginTop)
                    FabHelper.updateTextColor(title, itemActiveColor, itemInactiveColor)
                    FabHelper.updateWidth(container, selectedItemWidth, notSelectedItemWidth)
                }

                FabHelper.updateAlpha(title, 1F, 0F)
                if (isForceTint) {
                    FabHelper.updateDrawableColor(mcontext, items[currentItem].getDrawable(mcontext), icon,
                        itemActiveColor, itemInactiveColor, isForceTint)
                }
            }
        }

        currentItem = itemIndex
        if (currentItem > 0 && currentItem < items.size) {
            currentColor = items[currentItem].getColor(mcontext)
        } else if (currentItem == CURRENT_ITEM_NONE) {
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(this.defaultBackgroundColor)
            }
            backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Update notifications
     */
    private fun updateNotifications(updateStyle: Boolean, itemPosition: Int) {

        for (i in views.indices) {

            if (i >= notifications.size) {
                break
            }

            if (itemPosition != UPDATE_ALL_NOTIFICATIONS && itemPosition != i) {
                continue
            }

            val notificationItem = notifications[i]
            val currentTextColor = FabNotificationHelper.getTextColor(notificationItem, notificationTextColor)
            val currentBackgroundColor = FabNotificationHelper.getBackgroundColor(notificationItem, notificationBackgroundColor)

            val notification = views[i].findViewById<View>(R.id.bottom_navigation_notification) as TextView

            val currentValue = notification.text.toString()
            val animate = currentValue != notificationItem.text.toString()

            if (updateStyle) {
                notification.setTextColor(currentTextColor)
                if (notificationTypeface != null) {
                    notification.setTypeface(notificationTypeface)
                } else {
                    notification.setTypeface(null, Typeface.BOLD)
                }

                if (notificationBackgroundDrawable != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        val drawable = notificationBackgroundDrawable!!.constantState!!.newDrawable()
                        notification.background = drawable
                    } else {
                        notification.setBackgroundDrawable(notificationBackgroundDrawable)
                    }

                } else if (currentBackgroundColor != 0) {
                    val defautlDrawable = ContextCompat.getDrawable(mcontext, R.drawable.notification_background)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notification.background = defautlDrawable?.let {
                            FabHelper.getTintDrawable(it,
                                currentBackgroundColor, isForceTint)
                        }
                    } else {
                        notification.setBackgroundDrawable(defautlDrawable?.let {
                            FabHelper.getTintDrawable(it,
                                currentBackgroundColor, isForceTint)
                        })
                    }
                }
            }

            if (notificationItem.isEmpty && notification.text.isNotEmpty()) {
                notification.text = ""
                if (animate) {
                    notification.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setInterpolator(AccelerateInterpolator())
                        .setDuration(notificationAnimationDuration)
                        .start()
                }
            } else if (!notificationItem.isEmpty) {
                notification.setText(notificationItem.text.toString())
                if (animate) {
                    notification.scaleX = 0f
                    notification.scaleY = 0f
                    notification.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(notificationAnimationDuration)
                        .start()
                }
            }
        }
    }


    ////////////
    // PUBLIC //
    ////////////

    /**
     * Add an item at the given index
     */
    fun addItemAtIndex(index: Int, item: FabNavigationItem) {
        if (this.items.size > MAX_ITEMS) {
            Log.w(TAG, "The items list should not have more than 5 items")
        }
        if (index < items.size) {
            this.items.add(index, item)
        } else {
            Log.w(TAG, "The index is out of bounds (index: " + index + ", size: " + this.items.size + ")")
        }
        createItems()
    }

    /**
     * Add an item
     */
    fun addItem(item: FabNavigationItem) {
        if (this.items.size > MAX_ITEMS) {
            Log.w(TAG, "The items list should not have more than 5 items")
        }
        items.add(item)
        createItems()
    }

    /**
     * Add all items
     */
    fun addItems(items: List<FabNavigationItem>) {
        if (items.size > MAX_ITEMS || this.items.size + items.size > MAX_ITEMS) {
            Log.w(TAG, "The items list should not have more than 5 items")
        }
        this.items.addAll(items)
        createItems()
    }

    /**
     * Remove an item at the given index
     */
    fun removeItemAtIndex(index: Int) {
        if (index < items.size) {
            this.items.removeAt(index)
            createItems()
        }
    }

    /**
     * Remove all items
     */
    fun removeAllItems() {
        this.items.clear()
        createItems()
    }

    /**
     * Refresh the AHBottomView
     */
    fun refresh() {
        createItems()
    }

    /**
     * Set the bottom navigation background resource
     *
     * @param defaultBackgroundResource The bottom navigation background resource
     */
    fun setDefaultBackgroundResource(@DrawableRes defaultBackgroundResource: Int) {
        this.defaultBackgroundResource = defaultBackgroundResource
        createItems()
    }

    /**
     * Set the colors used when the bottom bar uses the colored mode
     *
     * @param colorActive   The active color
     * @param colorInactive The inactive color
     */
    fun setColoredModeColors(@ColorInt colorActive: Int, @ColorInt colorInactive: Int) {
        this.coloredTitleColorActive = colorActive
        this.coloredTitleColorInactive = colorInactive
        createItems()
    }

    /**
     * Set selected background visibility
     */
    fun setSelectedBackgroundVisible(visible: Boolean) {
        this.selectedBackgroundVisible = visible
        createItems()
    }

    /**
     * Set notification typeface
     *
     * @param typeface Typeface
     */
    fun setTitleTypeface(typeface: Typeface) {
        this.titleTypeface = typeface
        createItems()
    }

    /**
     * Set title text size in pixels
     *
     * @param activeSize
     * @param inactiveSize
     */
    fun setTitleTextSize(activeSize: Float, inactiveSize: Float) {
        this.titleActiveTextSize = activeSize
        this.titleInactiveTextSize = inactiveSize
        createItems()
    }

    /**
     * Set title text size in SP
     *
     * +	 * @param activeSize in sp
     * +	 * @param inactiveSize in sp
     */
    fun setTitleTextSizeInSp(activeSize: Float, inactiveSize: Float) {
        this.titleActiveTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, activeSize, mResources.displayMetrics)
        this.titleInactiveTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, inactiveSize, mResources.displayMetrics)
        createItems()
    }

    /**
     * Get item at the given index
     *
     * @param position int: item position
     * @return The item at the given position
     */
    fun getItem(position: Int): FabNavigationItem? {
        if (position < 0 || position > items.size - 1) {
            Log.w(TAG, "The position is out of bounds of the items (" + items.size + " elements)")
            return null
        }
        return items[position]
    }

    /**
     * Get the current item
     *
     * @return The current item position
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * Set the current item
     *
     * @param position    int: item position
     * @param useCallback boolean: use or not the callback
     */
    @JvmOverloads
    fun setCurrentItem(position: Int, useCallback: Boolean = true) {
        if (position >= items.size) {
            Log.w(TAG, "The position is out of bounds of the items (" + items.size + " elements)")
            return
        }

        if (this.titleState != TitleState.ALWAYS_HIDE &&
            this.titleState != TitleState.SHOW_WHEN_ACTIVE_FORCE &&
            (items.size == MIN_ITEMS || this.titleState == TitleState.ALWAYS_SHOW)) {
            updateItems(position, useCallback)
        } else {
            updateSmallItems(position, useCallback)
        }
    }

    /**
     * Manage the floating action button behavior with AHBottomNavigation
     * @param fab Floating Action Button
     */
    fun manageFloatingActionButtonBehavior(fab: View) {
        if (fab.parent is CoordinatorLayout) {
            val fabBehavior = FabNavigationButtonBehaviour(navigationBarHeight)
            (fab.layoutParams as CoordinatorLayout.LayoutParams).behavior = fabBehavior
        }
    }

    /**
     * Hide Bottom Navigation with or without animation
     *
     * @param withAnimation Boolean
     */
    @JvmOverloads
    fun hideBottomNavigation(withAnimation: Boolean = true) {
        when {
            bottomNavigationBehavior != null -> bottomNavigationBehavior!!.hideView(this, bottomNavigationHeight, withAnimation)
            parent is CoordinatorLayout -> {
                needHideBottomNavigation = true
                hideBottomNavigationWithAnimation = withAnimation
            }
            else -> // Hide bottom navigation
                ViewCompat.animate(this)
                    .translationY(bottomNavigationHeight.toFloat())
                    .setInterpolator(LinearOutSlowInInterpolator())
                    .setDuration((if (withAnimation) 300 else 0).toLong())
                    .start()
        }
    }

    /**
     * Restore Bottom Navigation with or without animation
     *
     * @param withAnimation Boolean
     */
    @JvmOverloads
    fun restoreBottomNavigation(withAnimation: Boolean = true) {
        if (bottomNavigationBehavior != null) {
            bottomNavigationBehavior!!.resetOffset(this, withAnimation)
        } else {
            // Show bottom navigation
            ViewCompat.animate(this)
                .translationY(0f)
                .setInterpolator(LinearOutSlowInInterpolator())
                .setDuration((if (withAnimation) 300 else 0).toLong())
                .start()
        }
    }

    /**
     * Set AHOnTabSelectedListener
     */
    fun setOnTabSelectedListener(tabSelectedListener: OnTabSelectedListener) {
        this.tabSelectedListener = tabSelectedListener
    }

    /**
     * Remove AHOnTabSelectedListener
     */
    fun removeOnTabSelectedListener() {
        this.tabSelectedListener = null
    }

    /**
     * Set OnNavigationPositionListener
     */
    fun setOnNavigationPositionListener(navigationPositionListener: OnNavigationPositionListener) {
        this.navigationPositionListener = navigationPositionListener
        if (bottomNavigationBehavior != null) {
            bottomNavigationBehavior!!.setOnNavigationPositionListener(navigationPositionListener)
        }
    }

    /**
     * Remove OnNavigationPositionListener()
     */
    fun removeOnNavigationPositionListener() {
        this.navigationPositionListener = null
        if (bottomNavigationBehavior != null) {
            bottomNavigationBehavior!!.removeOnNavigationPositionListener()
        }
    }

    /**
     * Set the notification number
     *
     * @param nbNotification int
     * @param itemPosition   int
     */
    @Deprecated("")
    fun setNotification(nbNotification: Int, itemPosition: Int) {
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(String.format(Locale.US, EXCEPTION_INDEX_OUT_OF_BOUNDS, itemPosition, items.size))
        }
        val title = if (nbNotification == 0) "" else nbNotification.toString()
        notifications[itemPosition] = FabNotification.justText(title)
        updateNotifications(false, itemPosition)
    }

    /**
     * Set notification text
     *
     * @param title        String
     * @param itemPosition int
     */
    fun setNotification(title: String, itemPosition: Int) {
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(String.format(Locale.US, EXCEPTION_INDEX_OUT_OF_BOUNDS, itemPosition, items.size))
        }
        notifications[itemPosition] = FabNotification.justText(title)
        updateNotifications(false, itemPosition)
    }

    /**
     * Set fully customized Notification
     *
     * @param notification AHNotification
     * @param itemPosition int
     */
    fun setNotification(notification: FabNotification?, itemPosition: Int) {
        var notification = notification
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(String.format(Locale.US, EXCEPTION_INDEX_OUT_OF_BOUNDS, itemPosition, items.size))
        }
        if (notification == null) {
            notification = FabNotification() // instead of null, use empty notification
        }
        notifications[itemPosition] = notification
        updateNotifications(true, itemPosition)
    }

    /**
     * Set notification text color
     *
     * @param textColor int
     */
    fun setNotificationTextColor(@ColorInt textColor: Int) {
        this.notificationTextColor = textColor
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification text color
     *
     * @param textColor int
     */
    fun setNotificationTextColorResource(@ColorRes textColor: Int) {
        this.notificationTextColor = ContextCompat.getColor(mcontext, textColor)
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background resource
     *
     * @param drawable Drawable
     */
    fun setNotificationBackground(drawable: Drawable) {
        this.notificationBackgroundDrawable = drawable
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background color
     *
     * @param color int
     */
    fun setNotificationBackgroundColor(@ColorInt color: Int) {
        this.notificationBackgroundColor = color
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background color
     *
     * @param color int
     */
    fun setNotificationBackgroundColorResource(@ColorRes color: Int) {
        this.notificationBackgroundColor = ContextCompat.getColor(mcontext!!, color)
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification typeface
     *
     * @param typeface Typeface
     */
    fun setNotificationTypeface(typeface: Typeface) {
        this.notificationTypeface = typeface
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    fun setNotificationAnimationDuration(notificationAnimationDuration: Long) {
        this.notificationAnimationDuration = notificationAnimationDuration
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set the notification margin left
     *
     * @param activeMargin
     * @param inactiveMargin
     */
    fun setNotificationMarginLeft(activeMargin: Int, inactiveMargin: Int) {
        this.notificationActiveMarginLeft = activeMargin
        this.notificationInactiveMarginLeft = inactiveMargin
        createItems()
    }

    /**
     * Activate or not the elevation
     *
     * @param useElevation boolean
     */
    fun setUseElevation(useElevation: Boolean) {
        ViewCompat.setElevation(this, if (useElevation)
            mResources.getDimension(R.dimen.bottom_navigation_elevation)
        else
            0F)
        clipToPadding = false
    }

    /**
     * Activate or not the elevation, and set the value
     *
     * @param useElevation boolean
     * @param elevation    float
     */
    fun setUseElevation(useElevation: Boolean, elevation: Float) {
        ViewCompat.setElevation(this, if (useElevation) elevation else 0F)
        clipToPadding = false
    }

    /**
     * Get the view at the given position
     * @param position int
     * @return The view at the position, or null
     */
    fun getViewAtPosition(position: Int): View? {
        return if ((linearLayoutContainer != null && position >= 0
                && position < linearLayoutContainer.childCount)) {
            linearLayoutContainer.getChildAt(position)
        } else null
    }

    /**
     * Enable the tab item at the given position
     * @param position int
     */
    fun enableItemAtPosition(position: Int) {
        if (position < 0 || position > items.size - 1) {
            Log.w(TAG, "The position is out of bounds of the items (" + items.size + " elements)")
            return
        }
        itemsEnabledStates[position] = true
        createItems()
    }

    /**
     * Disable the tab item at the given position
     * @param position int
     */
    fun disableItemAtPosition(position: Int) {
        if (position < 0 || position > items.size - 1) {
            Log.w(TAG, "The position is out of bounds of the items (" + items.size + " elements)")
            return
        }
        itemsEnabledStates[position] = false
        createItems()
    }

    /**
     * Set the item disable color
     * @param itemDisableColor int
     */
    fun setItemDisableColor(@ColorInt itemDisableColor: Int) {
        this.itemDisableColor = itemDisableColor
    }

    ////////////////
    // INTERFACES //
    ////////////////

    /**
     *
     */
    interface OnTabSelectedListener {
        /**
         * Called when a tab has been selected (clicked)
         *
         * @param position    int: Position of the selected tab
         * @param wasSelected boolean: true if the tab was already selected
         * @return boolean: true for updating the tab UI, false otherwise
         */
        fun onTabSelected(position: Int, wasSelected: Boolean): Boolean
    }

    interface OnNavigationPositionListener {
        /**
         * Called when the bottom navigation position is changed
         *
         * @param y int: y translation of bottom navigation
         */
        fun onPositionChange(y: Int)
    }

    companion object {

        // Constant
        const val CURRENT_ITEM_NONE = -1
        const val UPDATE_ALL_NOTIFICATIONS = -1

        // Static
        private val TAG = "AHBottomNavigation"
        private const val EXCEPTION_INDEX_OUT_OF_BOUNDS = "The position (%d) is out of bounds of the items (%d elements)"
        private const val MIN_ITEMS = 3
        private const val MAX_ITEMS = 5
    }

}
/**
 * Set the current item
 *
 * @param position int: position
 */
/**
 * Hide Bottom Navigation with animation
 */
/**
 * Restore Bottom Navigation with animation
 */
