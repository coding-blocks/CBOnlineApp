package com.codingblocks.cbonlineapp.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan as StyleSpan1
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.CREDENTIAL_PICKER_REQUEST
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
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


    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(requireActivity())
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            CREDENTIAL_PICKER_REQUEST,
            null, 0, 0, 0, null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val cred: Credential? = data.getParcelableExtra(Credential.EXTRA_KEY)
            if (cred != null) {
                val unformattedPhone = cred.id
                val formatNumber = SpannableString(unformattedPhone)
                val boldSpan = StyleSpan1(Typeface.BOLD) // Span to make text bold
                formatNumber.setSpan(boldSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                numberLayout.editText?.setText(formatNumber)
            }
        }
    }
}
