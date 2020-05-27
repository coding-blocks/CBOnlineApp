package com.codingblocks.cbonlineapp.auth.onboarding

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.AuthViewModel
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.MySMSBroadcastReceiver.OnSmsOTPReceivedListener
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginOtpFragment : BaseCBFragment(), OnSmsOTPReceivedListener {

    val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_otp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numberTv.append(vm.mobile)

        verifyOtpBtn.setOnClickListener {
            verifyWithOtp()
        }
    }

    private fun verifyWithOtp() {
        verifyOtpBtn.isEnabled = false
//        GlobalScope.launch(Dispatchers.Main) {
//            when (val response = safeApiCall { Clients.api.getJwt(map) }) {
//                is ResultWrapper.GenericError -> {
//                    verifyOtpBtn.isEnabled = true
//                    otpRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
//                }
//                is ResultWrapper.Success -> {
//                    if (response.value.isSuccessful) {
//                        response.value.body()?.let {
//                            with(it["jwt"].asString) {
//                                Clients.authJwt = this
//                                sharedPrefs.SP_JWT_TOKEN_KEY = this
//                            }
//                            with(it["refresh_token"].asString) {
//                                Clients.refreshToken = this
//                                sharedPrefs.SP_JWT_REFRESH_TOKEN = this
//                            }
//                        }
//                        if (map["oneauth_id"].isNullOrEmpty())
//                            navigateToActivity()
//                        else
//                            startActivity(intentFor<CompleteProfileActivity>())
//                        requireActivity().finish()
//                    } else
//                        runOnUiThread {
//                            verifyOtpBtn.isEnabled = true
//                        }
//                }
//            }
//        }
    }

    /**
     * Always call finish after setResult or startActivity otherwise you'll lose reference of previous activity
     */
    private fun navigateToActivity() {
        with(requireActivity()) {
            if (callingActivity == null) {
                startActivity(DashboardActivity.createDashboardActivityIntent(requireContext(), true))
            } else {
                setResult(Activity.RESULT_OK)
            }
            finish()
        }
    }

    override fun onSmsOTPReceieved(otp: String) {
        if (otp.isNotEmpty()) {
            numberLayout.editText?.setText(otp)
            verifyWithOtp()
        }
    }

    override fun onSmsOTPTimeout() {
        Log.w("SMS_OTP", "OTP timeout")
    }
}
