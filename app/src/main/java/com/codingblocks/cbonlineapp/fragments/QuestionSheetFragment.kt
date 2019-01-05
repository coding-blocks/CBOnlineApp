package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG__ATTEMPT_ID = "attempt_id"

class QuestionSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_question_sheet, container, false).apply {

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                OverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__ATTEMPT_ID, param1)
                    }
                }
    }


}
