package com.codingblocks.cbonlineapp.mycourse.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.mycourse.overview.LeaderBoardListAdapter
import kotlinx.android.synthetic.main.fragment_misc.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseMiscFragment : BaseCBFragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_misc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCourse.setOnClickListener {
            val pauseCourse = PauseFragment()
            pauseCourse.show(childFragmentManager, pauseCourse.tag)
        }
    }
}
