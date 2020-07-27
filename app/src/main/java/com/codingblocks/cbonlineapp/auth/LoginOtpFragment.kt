package com.codingblocks.cbonlineapp.auth

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.getSpannableStringSecondBold
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.util.receivers.MySMSBroadcastReceiver.OnSmsOTPReceivedListener
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.jetbrains.anko.support.v4.toast
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
        setFirstSpan()
        numberTv.append(vm.mobile)
        vm.errorLiveData.observer(thisLifecycleOwner) {
            verifyOtpBtn.isEnabled = true
        }
        otpEdtv.addTextChangedListener {
            verifyOtpBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 6)
        }
        verifyOtpBtn.setOnClickListener {
            verifyWithOtp()
        }
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun verifyWithOtp(otp: String = "") {
        verifyOtpBtn?.isEnabled = false
        if (otp.isEmpty())
            vm.verifyOtp(otpEdtv.text.toString())
        else {
            vm.verifyOtp(otp)
        }
    }

    override fun onSmsOTPReceieved(otp: String) {
        if (otp.isNotEmpty()) {
            if (otpEdtv != null)
                otpEdtv.setText(otp)
            verifyWithOtp(otp)
        }
    }

    private fun setFirstSpan() {
        val wordToSpan = getSpannableStringSecondBold("Didn't recieve OTP?    ", "RESEND")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                vm.sendOtp()
                toast("You will receive Otp on SMS shortly!!")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = false // set to false to remove underline
            }
        }
        wordToSpan.setSpan(clickableSpan, wordToSpan.length - 6, wordToSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        resendOtpTv.apply {
            text = wordToSpan
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    override fun onSmsOTPTimeout() {
        Log.w("SMS_OTP", "OTP timeout")
    }
}
