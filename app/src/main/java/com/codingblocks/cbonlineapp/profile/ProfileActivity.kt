package com.codingblocks.cbonlineapp.profile

import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.extensions.loadSvg
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : BaseCBActivity() {

    private val vm by viewModel<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        vm.fetchUser().observer(this) {
            graduation.setText(it.graduationyear)
            college.setText(it.college)
            email.setText(it.email)
            mobile.setText(it.mobile)
            if (it.verifiedemail != null) {
                emailLayout.endIconMode = END_ICON_CUSTOM
            }
            if (it.verifiedemail != null) {
                phoneLayout.endIconMode = END_ICON_CUSTOM
            }
            nameTv.text = "${it.firstname} ${it.lastname}"
            userNameTv.text = it.username
            userImgView.loadSvg(it.photo ?: "")
        }
    }
}
