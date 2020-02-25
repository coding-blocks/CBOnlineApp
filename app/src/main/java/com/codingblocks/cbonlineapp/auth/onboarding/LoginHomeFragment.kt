package com.codingblocks.cbonlineapp.auth.onboarding

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_login_home.*

class LoginHomeFragment : BaseCBFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_login_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setfirstSpan()
        setSecondSpan()

        mobileBtn.setOnClickListener {
            replaceFragmentSafely(
                SignInFragment(),
                tag = "SignIn",
                containerViewId = R.id.loginContainer
            )
        }

        gmailBtn.setOnClickListener {
            showWebView()
        }

        fbBtn.setOnClickListener {
            showWebView()
        }
    }

    private fun showWebView() {
        replaceFragmentSafely(
            SocialLoginFragment(),
            tag = "SocialSignIn",
            containerViewId = R.id.loginContainer
        )
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

    private fun setfirstSpan() {
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
