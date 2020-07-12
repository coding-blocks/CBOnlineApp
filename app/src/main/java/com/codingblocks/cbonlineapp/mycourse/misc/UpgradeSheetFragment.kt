package com.codingblocks.cbonlineapp.mycourse.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.RoundedBottomSheetDialogFragment
import com.codingblocks.cbonlineapp.course.batches.BatchComparisonAdapter
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.livedata.observeOnce
import com.codingblocks.onlineapi.models.Comparision
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.bottom_sheet_comparsion.view.*
import kotlinx.android.synthetic.main.course_upgrade_fragment.*
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.reflect.Type

class UpgradeSheetFragment : RoundedBottomSheetDialogFragment() {

    val vm: MyCourseViewModel by sharedViewModel()
    private val comparisionDialog by lazy { BottomSheetDialog(requireContext()) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setUpBottomSheet()
        return inflater.inflate(R.layout.course_upgrade_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.run?.observeOnce {
            vm.getUpgradePack(it.course.cid).observeOnce {
                upgradeCourseBtn.setOnClickListener {
                    vm.upgradeCourse()
                }
            }
        }

        compareBtn.setOnClickListener {
            comparisionDialog.show()
        }
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_comparsion, null)
        val json = FileUtils.loadJsonObjectFromAsset(requireContext(), "comparision.json") as JSONArray?
        val listType: Type = object : TypeToken<List<Comparision>>() {}.type
        val list: List<Comparision> = Gson().fromJson(json.toString(), listType)
        val sheetAdapter = BatchComparisonAdapter(list)
        sheetDialog.compareBatchRv.adapter = sheetAdapter
        comparisionDialog.dismissWithAnimation = true
        comparisionDialog.setContentView(sheetDialog)
    }
}
