package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import org.koin.android.ext.android.inject

class SignUpFragment : BaseCBFragment() {

    var map = HashMap<String, String>()
    private val sharedPrefs by inject<PreferenceHelper>()
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        proceedBtn.setOnClickListener {
            val name = nameLayout.editText?.text.toString().split(" ")
            val number =
                if (mobileLayout.editText?.text?.length!! > 10) "+91-${mobileLayout.editText?.text?.takeLast(
                    10
                )}" else "+91-${mobileLayout.editText?.text}"

            if (name.size < 2) {
                signUpRoot.showSnackbar("Last Name Cannot Be Empty", Snackbar.LENGTH_SHORT)
            } else {
                map["username"] = userNameLayout.editText?.text.toString()
                map["mobile"] = number
                map["firstname"] = name[0]
                map["lastname"] = name[1]
                map["email"] = emailLayout.editText?.text.toString()
            }

            proceedBtn.isEnabled = false

            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.createUser(map) }) {
                    is ResultWrapper.GenericError -> {
                        runOnUiThread {
                            proceedBtn.isEnabled = true
                            signUpRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                        }
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful) {
                            response.value.body()?.let {
                                sendOtp(it["oneauth_id"].asString)
                            }
                        } else
                            runOnUiThread {
                                var error = ""
                                error = try {
                                    val errRes = response.value.errorBody()?.string()
                                    if (errRes?.contains("Cannot")!!) "Please Try Again" else JSONObject(
                                        errRes
                                    ).getString("description")
                                } catch (e: Exception) {
                                    "All Fields are required"
                                } finally {
                                    signUpRoot.showSnackbar(
                                        error.capitalize(),
                                        Snackbar.LENGTH_SHORT
                                    )
                                    proceedBtn.isEnabled = true
                                }
                            }
                    }
                }
            }
        }
        keyboardVisibilityHelper = KeyboardVisibilityUtil(view) {
            proceedBtn.isVisible = it
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

    private fun sendOtp(id: String) {
        val otpMap = HashMap<String, String>()
        otpMap["oneauth_id"] = id
        GlobalScope.launch {
            when (val response = safeApiCall { Clients.api.getOtp(otpMap) }) {
                is ResultWrapper.GenericError -> {
                    runOnUiThread {
                        proceedBtn.isEnabled = true
                        signUpRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                    }
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        replaceFragmentSafely(
                            LoginOtpFragment.newInstance(
                                map["mobile"]
                                    ?: "", id
                            ), containerViewId = R.id.loginContainer
                        )
                    else
                        runOnUiThread {
                            signUpRoot.showSnackbar(
                                "Invalid Number.Please Try Again",
                                Snackbar.LENGTH_SHORT
                            )
                            proceedBtn.isEnabled = true
                        }
                }
            }
        }
    }
}
