package com.codingblocks.cbonlineapp.auth.onboarding

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan as StyleSpan1
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.util.CREDENTIAL_PICKER_REQUEST
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.MySMSBroadcastReceiver
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.android.ext.android.inject

class SignInFragment : BaseCBFragment() {

    val db: AppDatabase by inject()
    var map = HashMap<String, String>()
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil

    private val sharedPrefs by inject<PreferenceHelper>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        keyboardVisibilityHelper = KeyboardVisibilityUtil(view) {
            proceedBtn.isVisible = it
        }
    }

    private fun loginWithNumber() {
        val numberEditText = numberLayout.editText?.text
        if (numberEditText.isNullOrEmpty() || numberEditText.length < 10) {
            signInRoot.showSnackbar("Number is too short", Snackbar.LENGTH_SHORT)
        } else {
            val number = if (numberEditText.length > 10) "+91-${numberEditText.takeLast(10)}" else "+91-$numberEditText"
            map["phone"] = number
            proceedBtn.isEnabled = false
            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.getOtp(map) }) {
                    is ResultWrapper.GenericError -> {
                        runOnUiThread {
                            proceedBtn.isEnabled = true
                            signInRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                        }
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful) {
                            activity?.let {
                                val otpFragment = LoginOtpFragment.newInstance(map["phone"] ?: "")
                                SmsRetriever.getClient(it).startSmsRetriever() // start retriever
                                replaceFragmentSafely(
                                    otpFragment,
                                    containerViewId = R.id.loginContainer
                                )
                                MySMSBroadcastReceiver.register(it, otpFragment) // the new fragment will listen for otp
                            }
                        } else {
                            runOnUiThread {
                                errorDrawableTv.isVisible = true
                                signInRoot.showSnackbar("Number Not Verified.Please Try Again !!", Snackbar.LENGTH_SHORT)
                                proceedBtn.isEnabled = true
                            }
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
                            if (sharedPrefs.SP_JWT_TOKEN_KEY.isNotEmpty() && sharedPrefs.SP_JWT_TOKEN_KEY != it["jwt"].asString) {
                                withContext(Dispatchers.IO) { db.clearAllTables() }
                                saveKeys(it)
                            } else {
                                saveKeys(it)
                            }
                        }
                        navigateToActivity()
                    } else
                        runOnUiThread {
                            signInRoot.showSnackbar("Invalid Username or Password.Please Try Again", Snackbar.LENGTH_SHORT)
                            proceedBtn.isEnabled = true
                        }
                }
            }
        }
    }

    /**
     * Always call finish after setResult or startActivity otherwise you'll lose reference of previous activity
     */
    private fun navigateToActivity() {
        with(requireActivity()) {
            if (callingActivity == null) {
                startActivity(DashboardActivity.createDashboardActivityIntent(requireContext(), true))
            } else {
                setResult(RESULT_OK)
            }
            finish()
        }
    }

    private fun saveKeys(it: JsonObject) {
        with(it["jwt"].asString) {
            Clients.authJwt = this
            sharedPrefs.SP_JWT_TOKEN_KEY = this
        }
        with(it["refresh_token"].asString) {
            Clients.refreshToken = this
            sharedPrefs.SP_JWT_REFRESH_TOKEN = this
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().viewTreeObserver
            .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    override fun onPause() {
        super.onPause()
        requireView().viewTreeObserver
            .removeOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
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
