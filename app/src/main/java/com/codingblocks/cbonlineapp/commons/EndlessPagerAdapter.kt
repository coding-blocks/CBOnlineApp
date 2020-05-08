package com.codingblocks.cbonlineapp.commons

import android.database.DataSetObserver
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class EndlessPagerAdapter(private val adapter: PagerAdapter, viewPager: ViewPager) :
    PagerAdapter() {

    init {
        viewPager.addOnPageChangeListener(SwapPageListener(viewPager))
    }

    override fun getCount(): Int {
        return adapter.count + 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (adapter.count < 2) {
            adapter.instantiateItem(container, position)
        }

        val newPosition: Int = when {
            position == 0 -> adapter.count - 1
            position >= count - 1 -> 0
            else -> position - 1
        }
        return adapter.instantiateItem(container, newPosition)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        adapter.destroyItem(container, position, `object`)
    }

    override fun finishUpdate(container: ViewGroup) {
        adapter.finishUpdate(container)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return adapter.isViewFromObject(view, `object`)
    }

    override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
        adapter.restoreState(bundle, classLoader)
    }

    override fun saveState(): Parcelable? {
        return adapter.saveState()
    }

    override fun startUpdate(container: ViewGroup) {
        adapter.startUpdate(container)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return adapter.getPageTitle(position)
    }

    override fun getPageWidth(position: Int): Float {
        return adapter.getPageWidth(position)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        adapter.setPrimaryItem(container, position, `object`)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        adapter.unregisterDataSetObserver(observer)
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        adapter.registerDataSetObserver(observer)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return adapter.getItemPosition(`object`)
    }

    private inner class SwapPageListener internal constructor(private val viewPager: ViewPager) :
        ViewPager.OnPageChangeListener {

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                val pagerAdapter = viewPager.adapter
                if (pagerAdapter != null) {
                    val itemCount = pagerAdapter.count
                    if (itemCount < 2) {
                        return
                    }
                    val index = viewPager.currentItem
                    if (index == 0) {
                        viewPager.setCurrentItem(itemCount - 2, false)
                    } else if (index == itemCount - 1) {
                        viewPager.setCurrentItem(1, false)
                    }
                }
            }
        }
    }
}
