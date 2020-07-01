package com.codingblocks.cbonlineapp.mycourse.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.course_pause_fragment.*


class PauseFragment : RoundedBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_pause_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        pauseCourseBtn.setOnClickListener {
            //Pause Course
        }

        pauseDescriptionTv.text
    }


}
