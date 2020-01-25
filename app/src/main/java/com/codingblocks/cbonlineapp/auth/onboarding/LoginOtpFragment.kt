package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login_otp.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.android.ext.android.inject

class LoginOtpFragment : Fragment() {

    var map = HashMap<String, String>()
    private val sharedPrefs by inject<PreferenceHelper>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_otp, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map["client"] = "android"
        arguments?.let {
            val phone = it.getString("phone") ?: ""
            numberTv.append(phone)
            map["phone"] = phone
            if (!it.getString("oneauth_id").isNullOrEmpty())
                map["oneauth_id"] = it.getString("oneauth_id") ?: ""
        }
        verifyOtpBtn.setOnClickListener {
            map["otp"] = numberLayout.editText?.text.toString()
            verifyOtpBtn.isEnabled = false
            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.getJwt(map) }) {
                    is ResultWrapper.GenericError -> {
                        verifyOtpBtn.isEnabled = true
                        otpRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful) {
                            response.value.body()?.let {
                                with(it["jwt"].asString) {
                                    Clients.authJwt = this
                                    sharedPrefs.SP_JWT_TOKEN_KEY = this
                                }
                                with(it["refresh_token"].asString) {
                                    Clients.refreshToken = this
                                    sharedPrefs.SP_JWT_REFRESH_TOKEN = this
                                }
                            }
                            if (map["oneauth_id"].isNullOrEmpty())
                                startActivity(intentFor<DashboardActivity>())
                            else
                                startActivity(intentFor<CompleteProfileActivity>())
                            requireActivity().finish()
                        } else
                            runOnUiThread {
                                verifyOtpBtn.isEnabled = true
                            }
                    }
                }
            }
        }
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
