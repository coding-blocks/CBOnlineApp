package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.gson.JsonObject
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.fragment_checkout_payment.*
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutPaymentFragment : Fragment() {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.cart.observer(this) { json ->
            payBtn.setOnClickListener {
                showRazorPayCheckoutForm(json)
            }
        }
    }

    /** Call this function at the last step after applying coupon and everything.
    Razorpay will automatically call either of the methods on CheckoutActivity.kt

    override fun onPaymentSuccess(p0: String?)  - p0: is razorpay_payment_id that needs to be sent to capture payment API

    override fun onPaymentError(p0: Int, p1: String?) - Show retry payment or payment declined.

     */
    private fun showRazorPayCheckoutForm(json: JsonObject) {
        val checkout = Checkout()
        val activity = activity
        try {
            val options = JSONObject()
            options.put("name", "Coding Blocks")
            options.put("description", json.get("productName")?.asString) // Use products name
            options.put("currency", "INR")
            options.put("order_id", json.get("id")?.asString) // razorpay_order_id from API
            options.put("image", "https://codingblocks.com/assets/images/cb/cblogo.png")
            options.put("amount", json.get("final_price")?.asString) // Amount in paise from carts API after applying coupon and everything
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("CheckoutFragment.kt", "Error in starting Razorpay Checkout", e)
        }
    }
}
