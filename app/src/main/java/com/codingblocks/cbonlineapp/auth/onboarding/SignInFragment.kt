package com.codingblocks.cbonlineapp.auth.onboarding

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWT_TOKEN
import com.codingblocks.cbonlineapp.util.REFRESH_TOKEN
import com.codingblocks.cbonlineapp.util.RESOLVEHINT
import com.codingblocks.cbonlineapp.util.extensions.getSharedPrefs
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.save
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.runOnUiThread
import android.text.style.StyleSpan as StyleSpan1

class SignInFragment : Fragment() {

    var type: String = ""
    var map = HashMap<String, String>()
    lateinit var apiClient: GoogleApiClient
    lateinit var hintRequest: HintRequest
    private val sharedPrefs by lazy { getSharedPrefs() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            type = it.getString("type") ?: ""
        }
        if (type.isNotEmpty())
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
                numberLayout.editText?.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    setText("")
                }
                passwordLayout.isVisible = !passwordLayout.isVisible
            } else {
                errorDrawableTv.text = getString(R.string.use_email)
                numberTitle.text = getString(R.string.enter_mobile_number_for_verification)
                numberDesc.text = getString(R.string.number_desc)
                passwordLayout.isVisible = !passwordLayout.isVisible
                numberLayout.editText?.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_CLASS_PHONE
                    setText("")
                }
                numberLayout.hint = getString(R.string.mobile_number)
            }
        }

        requestHint()
        proceedBtn.setOnClickListener {
            if (passwordLayout.isVisible) {
                validateEmailPassWord()
            } else {
                loginWithNumber()
            }
        }
    }

    private fun loginWithNumber() {
        val numberEditText = numberLayout.editText?.text
        if (numberEditText.isNullOrEmpty()) {
            signInRoot.showSnackbar("Number is too short", Snackbar.LENGTH_SHORT)
        } else {
            val number = if (numberEditText.length > 10) "+91-${numberEditText.substring(3)}" else "+91-$numberEditText"
            map["phone"] = number
            proceedBtn.isEnabled = false
            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.getOtp(map) }) {
                    is ResultWrapper.GenericError -> {
                        signInRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            replaceFragmentSafely(LoginOtpFragment.newInstance(map["phone"]
                                ?: ""), containerViewId = R.id.loginContainer, enterAnimation = R.animator.slide_in_right, exitAnimation = R.animator.slide_out_left, addToStack = true)
                        else
                            runOnUiThread {
                                errorDrawableTv.isVisible = true
                                signInRoot.showSnackbar("Invalid Number.Please Try Again", Snackbar.LENGTH_SHORT)
                                proceedBtn.isEnabled = true
                            }
                    }
                }
            }
        }
    }

    private fun validateEmailPassWord() {
        map["client"] = "android"
        map["username"] = numberLayout.editText?.text.toString()
        map["password"] = passwordLayout.editText?.text.toString()
        proceedBtn.isEnabled = false
        GlobalScope.launch {
            when (val response = safeApiCall { Clients.api.getJwtWithEmail(map) }) {
                is ResultWrapper.GenericError -> {
                    signInRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let {
                            with(it["jwt"].asString) {
                                Clients.authJwt = this
                                sharedPrefs.save(JWT_TOKEN, this)
                            }
                            with(it["refresh_token"].asString) {
                                Clients.refreshToken = this
                                sharedPrefs.save(REFRESH_TOKEN, this)
                            }
                        }
                        startActivity(intentFor<DashboardActivity>())
                        requireActivity().finish()
                    } else
                        runOnUiThread {
                            signInRoot.showSnackbar("Invalid Username or Password.Please Try Again", Snackbar.LENGTH_SHORT)
                            proceedBtn.isEnabled = true
                        }
                }
            }
        }
    }

    private fun requestHint() {
        hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        apiClient = GoogleApiClient.Builder(requireContext())
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
            val cred: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
            if (cred != null) {
                val unformattedPhone = cred.id
                val formatNumber = SpannableString(unformattedPhone)
                val boldSpan = StyleSpan1(Typeface.BOLD) // Span to make text bold
                formatNumber.setSpan(boldSpan, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                numberLayout.editText?.setText(formatNumber)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        apiClient.stopAutoManage(requireActivity())
        apiClient.disconnect()
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
