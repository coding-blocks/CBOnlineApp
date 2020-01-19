package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_checkout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    private val vm by viewModel<CheckoutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setToolbar(checkoutToolbar)
        Checkout.preload(applicationContext)

        replaceFragmentSafely(CheckoutOrderDetailsFragment(), containerViewId = R.id.checkoutContainer)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onPaymentSuccess(paymentId: String?) {
        vm.capturePayment()
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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
