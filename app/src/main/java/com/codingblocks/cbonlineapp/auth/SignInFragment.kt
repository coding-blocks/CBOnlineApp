package com.codingblocks.cbonlineapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignInFragment : BaseCBFragment() {

    val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        proceedBtn.setOnClickListener {
            validateEmailPassWord()
        }

        gmailBtn.setOnClickListener {
            showWebView()
        }

        fbBtn.setOnClickListener {
            showWebView()
        }
        vm.errorLiveData.observer(thisLifecycleOwner) {
            proceedBtn.isEnabled = true
        }
    }

    private fun showWebView() {
        replaceFragmentSafely(
            SocialLoginFragment(),
            tag = "SocialSignIn",
            containerViewId = R.id.loginContainer
        )
    }

    private fun validateEmailPassWord() {
        val email = numberLayout.editText?.text.toString()
        val password = passwordLayout.editText?.text.toString()
        proceedBtn.isEnabled = false
        vm.loginWithEmail(email, password)
    }
}
