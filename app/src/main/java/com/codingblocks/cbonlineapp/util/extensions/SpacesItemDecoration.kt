package com.codingblocks.cbonlineapp.util.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Float) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val isLast = position == state.itemCount - 1
        if (isLast) {
            outRect.right = space.toInt()
            outRect.left = 0 // don't forget about recycling...
        }
        if (position == 0) {
            outRect.left = 0
            // don't recycle bottom if first item is also last
// should keep bottom padding set above
            if (!isLast) outRect.right = 0
        }
    }
}
