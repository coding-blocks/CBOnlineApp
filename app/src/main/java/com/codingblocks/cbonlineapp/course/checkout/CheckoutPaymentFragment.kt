package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.razorpay.Checkout
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class CheckoutPaymentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /** Call this function at the last step after applying coupon and everything.
    Razorpay will automatically call either of the methods on CheckoutActivity.kt

    override fun onPaymentSuccess(p0: String?)  - p0: is razorpay_payment_id that needs to be sent to capture payment API

    override fun onPaymentError(p0: Int, p1: String?) - Show retry payment or payment declined.

     */
    fun showRazorpayCheckoutForm() {

        val checkout = Checkout()
        val activity = activity
        try {
            val options = JSONObject()
            options.put("name", "Coding Blocks")
            options.put("description", "Online course") // Use products name
            options.put("currency", "INR")
            options.put("order_id", "order_9A33XWu170gUtm") // razorpay_order_id from API
            options.put("image", "https://codingblocks.com/assets/images/cb/cblogo.png")
            options.put("amount", 1000) // Amount in paise from carts API after applying coupon and everything
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("CheckoutFragment.kt", "Error in starting Razorpay Checkout", e)
        }


    }


}
