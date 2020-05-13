package com.codingblocks.cbonlineapp.mycourse.goodies

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.bottom_sheet_goodies.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GoodiesRequestFragment : BottomSheetDialogFragment() {

    private val vm: MyCourseViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_goodies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetSaveBtn.setOnClickListener {
            if (checkValid(goodieFormNameEt) && checkValid(goodieFormAddressEt) && checkValid(goodieFormPostalCodeEt)) {
                vm.requestGoodies(goodieFormNameEt.text.toString(), goodieFormAddressEt.text.toString(), goodieFormPostalCodeEt.text.toString(), goodieFormAcnEt.text.toString())
            } else {
                when (true) {
                    !checkValid(goodieFormNameEt) -> toast("Name Required")
                    !checkValid(goodieFormAddressEt) -> toast("Address Required")
                    !checkValid(goodieFormPostalCodeEt) -> toast("Postal Code Required")
                }
            }
        }

        bottomSheetCancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener(DialogInterface.OnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        })
//        dialog.onBackPressed()
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

    private fun checkValid(textInputEditText: TextInputEditText): Boolean {
        return textInputEditText.text.toString().isNotEmpty()
    }
}
