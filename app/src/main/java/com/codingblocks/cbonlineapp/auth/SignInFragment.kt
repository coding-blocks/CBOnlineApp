package com.codingblocks.cbonlineapp.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
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
            showReCaptcha()
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

    private fun showReCaptcha() {
        SafetyNet.getClient(requireContext())
            .verifyWithRecaptcha(BuildConfig.RECAPTCHA_KEY)
            .addOnSuccessListener { response ->
                // Indicates communication with reCAPTCHA service was
                // successful.
                val userResponseToken = response.tokenResult
                Log.d("TAG", "Result: $userResponseToken")
                validateEmailPassWord(userResponseToken)

            }.addOnFailureListener { e ->
                proceedBtn.isEnabled = true
                if (e is ApiException) {
                    // An error occurred when communicating with the
                    // reCAPTCHA service. Refer to the status code to
                    // handle the error appropriately.
                    log("Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    // A different, unknown type of error occurred.
                    Log.d("TAG", "Error: ${e.message}").also {
                        log("Error: ${e.message}")
                    }
                }
            }
    }

    private fun showWebView() {
        replaceFragmentSafely(
            SocialLoginFragment(),
            tag = "SocialSignIn",
            containerViewId = R.id.loginContainer
        )
    }

    private fun validateEmailPassWord(userResponseToken: String) {
        val email = numberLayout.editText?.text.toString()
        val password = passwordLayout.editText?.text.toString()
        proceedBtn.isEnabled = false
        vm.loginWithEmail(email, password, userResponseToken)
    }
}
