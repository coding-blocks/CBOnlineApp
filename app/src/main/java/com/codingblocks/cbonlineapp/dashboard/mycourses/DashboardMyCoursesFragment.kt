package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_my_courses.*


class DashboardMyCoursesFragment : Fragment() {

    private val dialog by lazy { BottomSheetDialog(requireContext()) }
    private val imgs by lazy { resources.obtainTypedArray(R.array.course_type_img) }
    private val couresType by lazy { resources.getStringArray(R.array.course_type) }

    private val type = MutableLiveData(0)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_my_courses, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseTypeTv.setOnClickListener {
            setUpBottomSheet()
            dialog.show()
        }
        type.observer(viewLifecycleOwner) {
            courseTypeTv.apply {
                text = couresType[it]
                setCompoundDrawablesRelativeWithIntrinsicBounds(requireContext().getDrawable(imgs.getResourceId(it, 0)), null, requireContext().getDrawable(R.drawable.ic_dropdown), null)
            }
        }
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_mycourses, null)
        val list = arrayListOf<SheetItem>()
        repeat(5) {
            //            if (type.value == it)
//                list.add(SheetItem(couresType[it], imgs.getResourceId(it, 0),true))
//            else
            list.add(SheetItem(couresType[it], imgs.getResourceId(it, 0)))
        }
        sheetDialog.run {
            sheetLv.adapter = SheetAdapter(list)
            sheetLv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                type.postValue(position)
                dialog.dismiss()
            }
        }
        dialog.setContentView(sheetDialog)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}

