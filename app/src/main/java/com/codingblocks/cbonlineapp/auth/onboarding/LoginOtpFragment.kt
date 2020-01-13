package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWT_TOKEN
import com.codingblocks.cbonlineapp.util.REFRESH_TOKEN
import com.codingblocks.cbonlineapp.util.extensions.getSharedPrefs
import com.codingblocks.cbonlineapp.util.extensions.save
import com.codingblocks.onlineapi.Clients
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login_otp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.runOnUiThread

class LoginOtpFragment : Fragment() {

    var map = HashMap<String, String>()
    private val sharedPrefs by lazy { getSharedPrefs() }

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
            map["phone"] = it.getString("phone") ?: ""
        }
        verifyOtpBtn.setOnClickListener {
            map["otp"] = numberLayout.editText?.text.toString()
            verifyOtpBtn.isEnabled = false
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) { Clients.api.getJwt(map) }
                if (response.isSuccessful) {
                    response.body()?.let {
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
                        verifyOtpBtn.isEnabled = true
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
