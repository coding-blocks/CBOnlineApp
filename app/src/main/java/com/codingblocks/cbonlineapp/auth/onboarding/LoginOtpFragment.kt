package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.jetbrains.anko.support.v4.intentFor

class LoginOtpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_login_otp, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        verifyOtpBtn.setOnClickListener {
            startActivity(intentFor<CompleteProfileActivity>())
            requireActivity().finish()
        }
    }
}
