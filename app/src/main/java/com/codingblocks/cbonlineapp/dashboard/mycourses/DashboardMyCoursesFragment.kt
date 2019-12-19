package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_my_courses.*


class DashboardMyCoursesFragment : Fragment() {

    private val dialog by lazy {
        BottomSheetDialog(requireContext())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_my_courses, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpBottomSheet()
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_mycourses, null)
        val list = arrayListOf<SheetItem>()
        val imgs = resources.obtainTypedArray(R.array.course_type_img)
        val couresType = resources.getStringArray(R.array.course_type)
        repeat(5) {
            list.add(SheetItem(couresType[it], imgs.getResourceId(it, 0)))
        }
        sheetDialog.sheetLv.adapter = SheetAdapter(list)
        dialog.setContentView(sheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseTypeTv.setOnClickListener {
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

