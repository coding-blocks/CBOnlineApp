package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.MySMSBroadcastReceiver.OnSmsOTPReceivedListener
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import kotlinx.android.synthetic.main.activity_login2.*
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginOtpFragment : BaseCBFragment(), OnSmsOTPReceivedListener {

    private val sharedPrefs by inject<PreferenceHelper>()
    val vm by sharedViewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_otp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val phone = it.getString("phone") ?: ""
            numberTv.append(phone)
//            if (!it.getString("oneauth_id").isNullOrEmpty())
//                map["oneauth_id"] = it.getString("oneauth_id") ?: ""
        }
        verifyOtpBtn.setOnClickListener {
            verifyWithOtp(numberLayout.editText?.text.toString())
        }
        vm.errorLiveData.observer(this) {
            verifyOtpBtn.isEnabled = true
        }
    }

    private fun verifyWithOtp(otp: String) {
        verifyOtpBtn.isEnabled = false
        vm.verifyOtp(otp) {

        }
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
//                            startActivity(intentFor<DashboardActivity>())
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

    override fun onSmsOTPReceieved(otp: String) {
        if (otp.isNotEmpty()) {
            numberLayout.editText?.setText(otp)
            verifyWithOtp(otp)
        }
    }

    override fun onSmsOTPTimeout() {
        Log.w("SMS_OTP", "OTP timeout")
    }

    companion object {
        @JvmStatic
        fun newInstance(phone: String, oneAuthId: String = "") =
            LoginOtpFragment().apply {
                arguments = Bundle().apply {
                    putString("phone", phone)
                    putString("oneauth_id", oneAuthId)
                }
            }
    }
}
