package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_otp, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOtp()
        map["client"] = "android"
        map["phone"] = "+91-9582054664"
        verifyOtpBtn.setOnClickListener {
            map["otp"] = numberLayout.editText?.text.toString()
            verifyOtpBtn.isEnabled = false
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) { Clients.api.getJwt(map) }
                if (response.isSuccessful) {
                    startActivity(intentFor<DashboardActivity>())
                    requireActivity().finish()
                } else
                    runOnUiThread {
                        verifyOtpBtn.isEnabled = true
                    }
            }

        }
    }


    private fun getOtp() {
        val client = SmsRetriever.getClient(requireContext())
        val task: Task<Void> = client.startSmsRetriever()

        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
        }
    }
}
