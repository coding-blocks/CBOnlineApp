package com.codingblocks.cbonlineapp.mycourse.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import kotlinx.android.synthetic.main.fragment_misc.*
import org.jetbrains.anko.AnkoLogger

class CourseMiscFragment : BaseCBFragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_misc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCourse.setOnClickListener {
            val pauseCourse = PauseSheetFragment()
            pauseCourse.show(childFragmentManager, pauseCourse.tag)
        }

        upgradeCourse.setOnClickListener {
            val upgradeCourse = UpgradeSheetFragment()
            upgradeCourse.show(childFragmentManager, upgradeCourse.tag)
        }
    }
}
