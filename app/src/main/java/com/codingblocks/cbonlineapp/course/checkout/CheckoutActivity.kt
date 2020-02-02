package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : BaseCBActivity(), PaymentResultListener {

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
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
            payment.apply {
                isVisible = true
                playAnimation()
            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
    }

    override fun onPaymentSuccess(paymentId: String?) {
        vm.paymentMap["razorpay_payment_id"] = (paymentId)!!
        vm.capturePayment {
            GlobalScope.launch {
                delay(3000)
                finish()
            }
        }
        // To change body of created functions use File | Settings | File Templates.
        /** Make Network call to capture payment.
         *  Body : {
         *
         *       razorpay_payment_id: payment_ksdnvsdlv,
         *       razorpay_order_id: order_ijkdbsn,
         *       txnId: 192721,
         *       amount: 2394723 (paise)
         *  }
         */
    }
}
