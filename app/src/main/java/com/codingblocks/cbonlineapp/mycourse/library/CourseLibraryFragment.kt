package com.codingblocks.cbonlineapp.mycourse.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CourseLibraryFragment : Fragment() {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_library, container, false)
    }

}
