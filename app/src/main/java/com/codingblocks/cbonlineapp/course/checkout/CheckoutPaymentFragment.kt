package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.gson.JsonObject
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.dialog_coupon.view.*
import kotlinx.android.synthetic.main.fragment_checkout_payment.*
import org.jetbrains.anko.design.snackbar
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutPaymentFragment : BaseCBFragment() {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_checkout_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.getCart()
        super.onViewCreated(view, savedInstanceState)
        useBalance.setOnClickListener {
            vm.map["applyCredits"] = !vm.creditsApplied
            payBtn.isEnabled = false
            vm.updateCart()
        }

        errorDrawableTv.setOnClickListener {
            if (errorDrawableTv.text == "Apply Coupon") {
                vm.map["coupon"] = numberLayout.editText?.text.toString().toUpperCase()
            } else {
                vm.map["coupon"] = ""
                vm.map["coupon_id"] = vm.couponApplied
                vm.map["cart_id"] = vm.cartId
            }
            payBtn.isEnabled = false
            vm.updateCart()
        }
        vm.errorLiveData.observer(viewLifecycleOwner) {
            if (it.contains("coupon")) {
                vm.map.remove("coupon")
                if (it.contains("credits")) {
                    vm.map.remove("applyCredits")
                }
            } else if (it.contains("credits")) {
                vm.map.remove("applyCredits")
            }
            payBtn.isEnabled = false
            rootPayment.snackbar(it)
        }
        numberLayout.editText?.addTextChangedListener {
            if (it != null && it.length >= 3) {
                errorDrawableTv.apply {
                    isVisible = true
                    text = "Apply Coupon"
                }
            }
        }
        vm.cart.observer(viewLifecycleOwner) { json ->
            vm.map.remove("applyCredits")
            vm.map.remove("coupon")
            vm.map.remove("coupon_id")
            vm.map.remove("cart_id")
            json.getAsJsonArray("cartItems")?.get(0)?.asJsonObject?.run {
                payBtn.isEnabled = true
                vm.cartId = get("id").asString
                val credits = get("credits_used")?.asInt?.div(100) ?: 0
                if (credits != 0) {
                    if (!vm.creditsApplied)
                        rootPayment.snackbar("Credits Applied Successfully")
                    vm.creditsApplied = true
                } else {
                    if (vm.creditsApplied)
                        rootPayment.snackbar("Credits Removed")
                    vm.creditsApplied = false
                }
                vm.paymentMap["txnId"] = get("txnId")?.asString ?: ""
                try {
                    vm.paymentMap["razorpay_order_id"] = get("razorpay_order_id")?.asString ?: ""
                } catch (e: Exception) {
                    vm.isFree = true
                }
                get("coupon_code")?.let {
                    numberLayout.editText?.setText(it.asString)
                    vm.couponApplied = get("coupon_id").asString
                    val discountPrice = "${getString(R.string.rupee_sign)} ${get("discount")?.asInt?.div(100)}"
                    showCouponDialog(discountPrice, it.asString)
                    couponDiscount.text = "- $discountPrice"
                    errorDrawableTv.apply {
                        isVisible = true
                        text = "Remove Coupon"
                    }
                } ?: run {
                    couponDiscount.text = "- ${getString(R.string.rupee_sign)} 0"

                    numberLayout.editText?.setText("")
                    errorDrawableTv.isVisible = false
                }
                creditsTv.text = "- ${getString(R.string.rupee_sign)} $credits"

                if (credits == 0)
                    useBalance.text = "Use Wallet Balance"
                else {
                    useBalance.text = "Remove Wallet Balance"
                }
                totalTv.text = "${getString(R.string.rupee_sign)} ${json["totalAmount"].asString}"
                finalPriceTv.text =
                    "${getString(R.string.rupee_sign)} ${json["totalAmount"].asString}"
                walletBal.text =
                    "${getString(R.string.rupee_sign)} ${json?.get("user")?.asJsonObject?.get("wallet_amount")?.asInt?.div(100)
                        ?: 0}"

                payBtn.setOnClickListener {
                    vm.paymentMap["amount"] = json["totalAmount"].asString!!
                    vm.paymentStart.value = true
                    if (!vm.isFree)
                        showRazorPayCheckoutForm(this)
                }
            }
        }
    }

    private fun showCouponDialog(discountPrice: String, coupon: String) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_coupon, null)
        view.couponTv.text = coupon
        view.couponDiscountTv.text = discountPrice
        view.posBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(view)
            setCancelable(true)
            show()
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
            options.put(
                "order_id",
                json.get("razorpay_order_id")?.asString
            ) // razorpay_order_id from API
            options.put("image", "https://codingblocks.com/assets/images/cb/cblogo.png")
            options.put(
                "amount",
                json.get("final_price")?.asString
            ) // Amount in paise from carts API after applying coupon and everything
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("CheckoutFragment.kt", "Error in starting Razorpay Checkout", e)
        }
    }
}
