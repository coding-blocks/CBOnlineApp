package com.codingblocks.cbonlineapp.mycourse.content.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.video_feedback_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.reflect.Field


class VideoFeedbackBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: VideoPlayerViewModel by sharedViewModel()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_feedback_fragment, container, false)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.setContentView(R.layout.video_feedback_fragment)
        val behaviorField: Field = bottomSheetDialog.javaClass.getDeclaredField("behavior")
        behaviorField.isAccessible = true
        val behavior = behaviorField.get(bottomSheetDialog) as BottomSheetBehavior<*>
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            if (ratingBar.rating > 0) {
                additionalFeedbackView.isVisible = true
            }
        }
    }
}
