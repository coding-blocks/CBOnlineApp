package com.codingblocks.cbonlineapp.viewactions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher


class CustomViewAction{
    public fun clickChildViewWithId(text: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val outviews = ArrayList<View>()
                view.findViewsWithText(outviews,text,View.FIND_VIEWS_WITH_TEXT)
                outviews[0].performClick()
            }
        }
    }
}
