package com.codingblocks.cbonlineapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.backBtn
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpFragment : BaseCBFragment() {

    val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        vm.errorLiveData.observer(thisLifecycleOwner) {
            proceedBtn.isEnabled = true
        }
        emailLayout.editText?.setText(vm.email)
        mobileLayout.editText?.setText("${vm.dialCode}-${vm.mobile}")
        proceedBtn.setOnClickListener {

            val name = nameLayout.editText?.text.toString().split(" ")
            val username = userNameLayout.editText?.text.toString()
            when {
                name.size < 2 -> {
                    signUpRoot.showSnackbar("Last Name Cannot Be Empty", Snackbar.LENGTH_SHORT)
                }
                username.isEmpty() -> {
                    signUpRoot.showSnackbar("Username Cannot Be Empty", Snackbar.LENGTH_SHORT)
                }
                else -> {
                    proceedBtn.isEnabled = false
                    vm.createUser(name, username)
                }
            }
        }
    }
}
