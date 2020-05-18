package com.codingblocks.cbonlineapp.course.checkout

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.hideAndStop
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_checkout_order_details.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutOrderDetailsFragment : BaseCBFragment(), AnkoLogger {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_checkout_order_details, container, false)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkoutShimmer.showShimmer(true)
        vm.cart.observe(viewLifecycleOwner) {
            checkoutShimmer.hideAndStop()
            if (it == null) {
                emptyCart.isVisible = true
                cartLayout.isVisible = false
            } else {
                emptyCart.isVisible = false
                cartLayout.isVisible = true
                orderBtn.isEnabled = true
                finalPriceTv.text = "${getString(R.string.rupee_sign)} ${it["totalAmount"].asString}"
                it.getAsJsonArray("cartItems")?.get(0)?.asJsonObject?.run {

                    get("image_url")?.asString?.let { it1 -> courseLogoImg.loadImage(it1) }
                    courseTitleTv.text = get("productDescription")?.asString
                    vm.paymentMap["txnId"] = get("txnId")?.asString ?: ""
                    try {
                        vm.paymentMap["razorpay_order_id"] = get("razorpay_order_id")?.asString ?: ""
                    } catch (e: Exception) {
                        vm.isFree = true
                    }
                    batchTileTv.text = get("productName")?.asString
                    val price = get("final_price")?.asInt?.div(100)
                    val credits = get("credits_used")?.asInt?.div(100) ?: 0
                    priceTv.text = "${getString(R.string.rupee_sign)} $price"
                    creditsTv.text = "${getString(R.string.rupee_sign)} $credits"
                    mrpTv.apply {
                        text = "${getString(R.string.rupee_sign)} ${get("list_price")?.asInt?.div(100)}"
                        paintFlags = mrpTv.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    vm.map["invoice_id"] = get("invoice_id").asString
                    subTotalTv.text = "${getString(R.string.rupee_sign)} $price"
                    totalTv.text = "${getString(R.string.rupee_sign)} ${price!! - credits!!}"
                    taxesTv.text = "${getString(R.string.rupee_sign)} ${get("tax")?.asDouble?.div(100)}"
                    orderBtn.setOnClickListener {
                        replaceFragmentSafely(
                            CheckoutPersonalDetailsFragment(),
                            containerViewId = R.id.checkoutContainer,
                            addToStack = true
                        )
                    }
                    removeProduct.setOnClickListener {
                        Toast.makeText(context, "Items Removed", Toast.LENGTH_SHORT).show()
                        vm.clearCart()
                        emptyCart.isVisible = true
                        cartLayout.isVisible = false
                    }
                }
            }
        }

        exploreBtn.setOnClickListener { requireActivity().finish() }
        removeProduct.setOnClickListener(null)
    }
}
