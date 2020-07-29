package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.livedata.observeOnce
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : BaseCBActivity(), PaymentResultWithDataListener, AnkoLogger {

    private val vm by viewModel<CheckoutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setToolbar(checkoutToolbar)
        Checkout.preload(applicationContext)
        vm.getCart()
        replaceFragmentSafely(
            CheckoutOrderDetailsFragment(),
            containerViewId = R.id.checkoutContainer
        )
        vm.paymentStart.observer(this) {
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.beginTransaction().remove(fragment).commitNow()
            }
            payment.apply {
                isVisible = true
                playAnimation()
            }
            vm.addOrder().observeOnce { json ->
                if (json!!.has("razorpayPayment")) {
                    showRazorPayCheckoutForm(json)
                } else {
                    json.getAsJsonObject("transaction").let {
                        vm.paymentMap["txnId"] = it["id"].asString
                    }
                    confirmPayment()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /** Call this function at the last step after applying coupon and everything.
     Razorpay will automatically call either of the methods on CheckoutActivity.kt

     override fun onPaymentSuccess(p0: String?)  - p0: is razorpay_payment_id that needs to be sent to capture payment API

     override fun onPaymentError(p0: Int, p1: String?) - Show retry payment or payment declined.

     */
    private fun showRazorPayCheckoutForm(json: JsonObject) {
        val checkout = Checkout()
        try {
            val options = JSONObject()

            json.getAsJsonObject("razorpayPayment").let {
                options.put("order_id", it["order_id"].asString) // razorpay_order_id from API
                options.put("amount", it["amount"].asString) // Amount in paise from carts API after applying coupon and everything
            }
            json.getAsJsonObject("transaction").let {
                vm.paymentMap["txnId"] = it["id"].asString
                options.put("currency", it["currency"].asString)
            }
            json.getAsJsonObject("organization").let {
                options.put("name", it["name"].asString)
                options.put("image", it["logo"].asString)
                checkout.setKeyID(it["razorpayKey"].asString)
            }
            // Prefil user info
            json.getAsJsonObject("user").let {
                options.put("prefill.name", it["firstname"].asString)
                options.put("prefill.email", it["email"].asString)
                options.put("prefill.contact", it["mobile_number"].asString)
            }
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("CheckoutFragment.kt", "Error in starting Razorpay Checkout", e)
        }
    }

    override fun onPaymentError(code: Int, description: String?, p2: PaymentData?) {
        when (code) {
            Checkout.NETWORK_ERROR -> {
                payment.apply {
                    isVisible = false
                    cancelAnimation()
                }
                showOffline()
            }
            Checkout.PAYMENT_CANCELED -> {
                payment.apply {
                    isVisible = false
                    cancelAnimation()
                }
                toast("Oops, something went wrong")
                replaceFragmentSafely(
                    CheckoutFailedFragment(),
                    containerViewId = R.id.checkoutContainer,
                    allowStateLoss = true,
                    addToStack = true
                )
            }
        }
    }

    override fun onPaymentSuccess(p0: String, payment: PaymentData) {
        // To change body of created functions use File | Settings | File Templates.
        /** Make Network call to capture payment.
         *  Body : {
         *       razorpay_payment_id: payment_ksdnvsdlv,
         *       razorpay_order_id: order_ijkdbsn,
         *       txnId: 192721,
         *       amount: 2394723 (paise),
         *       razorpay_signature:sign
         *  }
         */
        vm.paymentMap["razorpay_payment_id"] = payment.paymentId
        vm.paymentMap["razorpay_order_id"] = payment.orderId
        vm.paymentMap["razorpay_signature"] = payment.signature
        confirmPayment()
    }

    private fun confirmPayment() {
        vm.capturePayment {
            GlobalScope.launch {
                delay(3000)
                startActivity(DashboardActivity.createDashboardActivityIntent(this@CheckoutActivity, true))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 3) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } else {
            super.onBackPressed()
        }
    }
}
