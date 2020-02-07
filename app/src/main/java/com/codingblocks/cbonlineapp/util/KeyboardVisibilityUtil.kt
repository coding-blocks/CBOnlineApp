package com.codingblocks.cbonlineapp.util

import android.graphics.Rect
import android.view.View

class KeyboardVisibilityUtil(contentView: View, onKeyboardShown: (Boolean) -> Unit) {

    private var currentKeyboardState: Boolean = false

    val visibilityListener = {
        val rectangle = Rect()
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height

        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        val keypadHeight = screenHeight.minus(rectangle.bottom)
        // 0.15 ratio is perhaps enough to determine keypad height.
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15

        if (currentKeyboardState != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                onKeyboardShown(false)
            } else {
                onKeyboardShown(true)
            }
        }
        currentKeyboardState = isKeyboardNowVisible
    }
}
