package com.codingblocks.cbonlineapp.auth

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.getSpannableStringSecondBold
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.receivers.MySMSBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.fragment_login_home.*
import org.jetbrains.anko.design.snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginHomeFragment : BaseCBFragment() {

    val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ccp.registerCarrierNumberEditText(numberedtv)
        ccp.setTypeFace(Typeface.createFromAsset(requireContext().assets, "fonts/gilroy_bold.ttf"))
        setFirstSpan()
        setSecondSpan()

        mobileBtn.setOnClickListener {
            if (ccp.isValidFullNumber) {
                vm.mobile = numberedtv.text.toString().replace(" ", "")
                vm.dialCode = ccp.selectedCountryCodeWithPlus
                vm.sendOtp()
                val otpFragment = LoginOtpFragment()
                SmsRetriever.getClient(requireActivity()).startSmsRetriever() // start retriever
                replaceFragmentSafely(otpFragment, containerViewId = R.id.loginContainer)
                MySMSBroadcastReceiver.register(requireActivity(), otpFragment) // the new fragment will listen for otp
            } else {
                loginHomeRoot.snackbar("Invalid Number !!")
            }
        }

        socialBtn.setOnClickListener {
            replaceFragmentSafely(
                SignInFragment(),
                tag = "SignIn",
                containerViewId = R.id.loginContainer
            )
        }
    }

    private fun setSecondSpan() {
        val policySpan = SpannableString(
            "By logging in you agree to Coding Blocks’s\n" +
                "Privacy Policy & Terms of Service"
        )
        val privacySpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                requireContext().openChrome("https://codingblocks.com/privacypolicy.html")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = true
            }
        }

        val tosSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                requireContext().openChrome("https://codingblocks.com/tos.html")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = true
            }
        }

        policySpan.setSpan(privacySpan, 41, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        policySpan.setSpan(tosSpan, 59, policySpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        policyTv.apply {
            text = policySpan
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun setFirstSpan() {
        val wordToSpan = getSpannableStringSecondBold("New here? ", "Create an account")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                replaceFragmentSafely(SignUpFragment(), containerViewId = R.id.loginContainer)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = false // set to false to remove underline
            }
        }
        wordToSpan.setSpan(clickableSpan, 9, wordToSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        createAccTv.apply {
            text = wordToSpan
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}
