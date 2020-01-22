package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOtp()
        map["client"] = "android"
        arguments?.let {
            numberTv.append(it.getString("phone") ?: "")
            map["phone"] = it.getString("phone") ?: ""
        }
        verifyOtpBtn.setOnClickListener {
            map["otp"] = numberLayout.editText?.text.toString()
            verifyOtpBtn.isEnabled = false
            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.getJwt(map) }) {
                    is ResultWrapper.GenericError -> {
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
                            startActivity(intentFor<DashboardActivity>())
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
        fun newInstance(phone: String) =
            LoginOtpFragment().apply {
                arguments = Bundle().apply {
                    putString("phone", phone)
                }
            }
    }

    private fun getOtp() {
        val client = SmsRetriever.getClient(requireContext())
        val task: Task<Void> = client.startSmsRetriever()

        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            Log.i("SMS", "$it")
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            Log.i("SMS", "$it")
        }
    }
}
