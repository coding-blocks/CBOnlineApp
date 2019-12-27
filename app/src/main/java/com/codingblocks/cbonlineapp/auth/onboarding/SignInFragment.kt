package com.codingblocks.cbonlineapp.auth.onboarding

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val cred = data?.getParcelableExtra(Credential.EXTRA_KEY) as Credential
        val unformattedPhone = cred.id
        val formatNumber = SpannableString(unformattedPhone)
        val boldSpan = StyleSpan(Typeface.BOLD)// Span to make text bold
        formatNumber.setSpan(boldSpan, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        numberLayout.editText?.setText(formatNumber)
    }


}
