package com.codingblocks.cbonlineapp.auth

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.bottom_sheet_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginEmailBottomSheet : BottomSheetDialogFragment() {

    private val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailBtn.setOnClickListener {
            val email = emailEdtv.text.toString()
            vm.email = email
            if (passwordLayout.isVisible) {
                vm.loginWithEmail(email, passEdtv.text.toString())
            } else {
                vm.findUser(hashMapOf("verifiedemail" to email))
            }
        }

        vm.account.observer(viewLifecycleOwner) {
            when (it) {
                AccountStates.EXITS -> {
                    passwordLayout.isVisible = true
                }
            }
        }
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
        BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
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
