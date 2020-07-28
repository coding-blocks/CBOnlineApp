package com.codingblocks.cbonlineapp.auth

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.regex.Pattern

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
            if (isValidEmail(email)) {
                emailLayout.error = ""
                if (passwordLayout.isVisible) {
                    vm.loginWithEmail(email, passEdtv.text.toString())
                } else {
                    vm.findUser(hashMapOf("verifiedemail" to email))
                }
            } else {
                emailLayout.error = "Email is not valid"
            }
        }

        vm.account.observer(viewLifecycleOwner) {
            when (it) {
                AccountStates.EXITS -> {
                    passwordLayout.isVisible = true
                }
                AccountStates.DO_NOT_EXIST -> {
                    dialog?.dismiss()
                }
                else -> {
                    // Todo - handle this
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}
