package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.dialog_coupon.view.*
import kotlinx.android.synthetic.main.fragment_checkout_payment.*
import org.jetbrains.anko.design.snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutPaymentFragment : BaseCBFragment() {

    val vm by sharedViewModel<CheckoutViewModel>()

    private lateinit var couponDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getCart()

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
        vm.errorLiveData.observer(thisLifecycleOwner) {
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
        vm.cart.observer(thisLifecycleOwner) { res ->
            vm.map.clear()
            res.getAsJsonObject("cartItems")?.run {
                vm.map["invoice_id"] = get("invoice_id").asString
                vm.cartId = get("id").asString

                payBtn.isEnabled = true

                val credits = get("credits_used").asInt.div(100)
                val totalAmount = res["totalAmount"].asString
                val wallet = res.getAsJsonObject("user").get("wallet_amount").asInt.div(100).toString()
                if (credits != 0) {
                    if (!vm.creditsApplied)
                        rootPayment.snackbar("Credits Applied Successfully")
                    vm.creditsApplied = true
                    useBalance.text = "Remove Wallet Balance"
                } else {
                    if (vm.creditsApplied)
                        rootPayment.snackbar("Credits Removed")
                    vm.creditsApplied = false
                    useBalance.text = "Use Wallet Balance"
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
                    couponDiscount.text = "- ${getString(R.string.rupee_price, "0")}"

                    numberLayout.editText?.setText("")
                    errorDrawableTv.isVisible = false
                }

                creditsTv.text = getString(R.string.rupee_price, credits.toString())
                totalTv.text = getString(R.string.rupee_price, totalAmount)
                finalPriceTv.text = getString(R.string.rupee_price, totalAmount)

                walletBal.text = getString(R.string.rupee_price, wallet)

                payBtn.setOnClickListener {
                    vm.paymentMap["amount"] = totalAmount
                    vm.paymentStart.value = true
                }
            }
        }
    }

    private fun showCouponDialog(discountPrice: String, coupon: String) {
        couponDialog = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_coupon, null)
        view.couponTv.text = coupon
        view.couponDiscountTv.text = discountPrice
        view.posBtn.setOnClickListener {
            couponDialog.dismiss()
        }
        couponDialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(view)
            setCancelable(true)
            show()
        }
    }
}
