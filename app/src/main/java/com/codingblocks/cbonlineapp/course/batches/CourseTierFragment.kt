package com.codingblocks.cbonlineapp.course.batches

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.CourseViewModel
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.extensions.getDateForRun
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.Clients.gson
import com.codingblocks.onlineapi.models.Comparision
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlinx.android.synthetic.main.bottom_sheet_comparsion.view.*
import kotlinx.android.synthetic.main.bottom_sheet_runs.*
import kotlinx.android.synthetic.main.bottom_sheet_runs.view.*
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseTierFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<CourseViewModel>()
    private val comparisionDialog by lazy { BottomSheetDialog(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setUpBottomSheet()
        return inflater.inflate(R.layout.bottom_sheet_runs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runsToolbar.setOnClickListener {
            dialog?.dismiss()
        }
        viewModel.course.distinctUntilChanged().observer(viewLifecycleOwner) { course ->
            with(view) {
                runTabs.removeAllTabs()
                val list = course.activeRuns?.sortedBy { it.start }?.groupBy { it.start }
                list?.forEach { runTabs.addTab(runTabs.newTab().setText(getDateForRun(it.key))) }

                val adapter = list?.keys?.let { RunsPagerAdapter(childFragmentManager, it.toList()) }
                viewPager.adapter = adapter
                viewPager.offscreenPageLimit = 1
                viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(runTabs))
                runTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {}

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        viewPager.currentItem = tab?.position ?: 0
                    }
                })

                //                if (view.runTabs.tabCount == 2) {
//                    view.runTabs.tabMode = TabLayout.MODE_FIXED
//                } else {
//                    view.runTabs.tabMode = TabLayout.MODE_SCROLLABLE
//                }
            }

            compareBtn.setOnClickListener {
                comparisionDialog.show()
            }
        }
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_comparsion, null)
        val json = FileUtils.loadJsonObjectFromAsset(requireContext(), "comparision.json") as JSONArray?
        val listType: Type = object : TypeToken<List<Comparision>>() {}.type
        val list: List<Comparision> = gson.fromJson(json.toString(), listType)
        val sheetAdapter = BatchComparisonAdapter(list)
        sheetDialog.compareBatchRv.adapter = sheetAdapter
        comparisionDialog.dismissWithAnimation = true
        comparisionDialog.setContentView(sheetDialog)
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener(DialogInterface.OnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        })
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val sheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
        val layoutParams = sheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        sheet.layoutParams = layoutParams
        BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}
