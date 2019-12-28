package com.codingblocks.cbonlineapp.auth.onboarding

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.RESOLVEHINT
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {

    lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            type = it.getString("type") ?: ""
        }
        if (!type.isNullOrEmpty())
            if (type.equals("new", true)) {
                errorDrawableTv.isVisible = true
                numberTitle.text = getString(R.string.welcome)
                numberDesc.text = getString(R.string.welcome_desc)
            }

        errorDrawableTv.setOnClickListener {
            if (errorDrawableTv.text == getString(R.string.use_email)) {
                errorDrawableTv.text = "Use Number"
                numberTitle.text = getString(R.string.email_title)
                numberDesc.text = getString(R.string.email_desc)
                numberLayout.hint = "Email ID"
                numberLayout.editText?.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                passwordLayout.isVisible = !passwordLayout.isVisible
            } else {
                errorDrawableTv.text = getString(R.string.use_email)
                numberTitle.text = getString(R.string.enter_mobile_number_for_verification)
                numberDesc.text = getString(R.string.number_desc)
                passwordLayout.isVisible = !passwordLayout.isVisible
                numberLayout.editText?.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_CLASS_PHONE
                numberLayout.hint = getString(R.string.mobile_number)
            }
        }


        requestHint()
        proceedBtn.setOnClickListener {
            replaceFragmentSafely(LoginOtpFragment(), containerViewId = R.id.loginContainer, enterAnimation = R.animator.slide_in_right, exitAnimation = R.animator.slide_out_left)
        }
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val apiClient = GoogleApiClient.Builder(requireContext())
            .addApi(Auth.CREDENTIALS_API)
            .enableAutoManage(requireActivity()) {
                Log.i("TAG", "Mobile Number: ${it.errorMessage}")
            }.build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVEHINT, null, 0, 0, 0, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val cred = data.getParcelableExtra(Credential.EXTRA_KEY) as Credential
            val unformattedPhone = cred.id
            val formatNumber = SpannableString(unformattedPhone)
            val boldSpan = StyleSpan(Typeface.BOLD)// Span to make text bold
            formatNumber.setSpan(boldSpan, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            numberLayout.editText?.setText(formatNumber)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(type: String) =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
    }


}
