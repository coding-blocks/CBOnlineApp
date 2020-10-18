package com.codingblocks.cbonlineapp.course.checkout

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.hideAndStop
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setTextWithVisibliy
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_checkout_order_details.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutOrderDetailsFragment : BaseCBFragment(), AnkoLogger {

    companion object {
        const val AMOUNT_FORMATTER = 100
    }

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_order_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkoutShimmer.showShimmer(true)
        vm.cart.observe(thisLifecycleOwner) { res ->
            checkoutShimmer.hideAndStop()
            if (res == null) {
                emptyCart.isVisible = true
                cartLayout.isVisible = false
            } else {
                handleResponse(res)
            }
        }

        exploreBtn.setOnClickListener { requireActivity().finish() }
        removeProduct.setOnClickListener(null)
    }

    private fun handleResponse(res: JsonObject) {
        emptyCart.isVisible = false
        cartLayout.isVisible = true
        orderBtn.isEnabled = true
        finalPriceTv.text = getString(R.string.rupee_price, res["totalAmount"].asString)
        res.getAsJsonObject("cartItems")?.run {
            courseTitleTv.text = get("productDescription")?.asString
            get("image_url")?.asString?.takeIf { it.isNotEmpty() }?.let {
                courseLogoImg.visibility = View.VISIBLE
                courseLogoImg.loadImage(it)
            } ?: kotlin.run {
                courseLogoImg.visibility = View.GONE
            }

            batchTileTv.setTextWithVisibliy(get("productName")?.asString)
            val price = get("final_price")?.asInt?.div(AMOUNT_FORMATTER) ?: 0
            val credits = get("credits_used")?.asInt?.div(AMOUNT_FORMATTER) ?: 0
            val mrp = get("list_price")?.asInt?.div(AMOUNT_FORMATTER)
            val tax = get("tax")?.asDouble?.div(AMOUNT_FORMATTER)

            priceTv.setTextWithVisibliy(getString(R.string.rupee_price, price.toString()))
            creditsTv.setTextWithVisibliy(getString(R.string.rupee_price, credits.toString()))
            mrpTv.apply {
                text = getString(R.string.rupee_price, mrp.toString())
                paintFlags = mrpTv.paintFlags or
                    Paint.STRIKE_THRU_TEXT_FLAG
            }

            vm.map["invoice_id"] = get("invoice_id").asString
            subTotalTv.setTextWithVisibliy(getString(R.string.rupee_price, price.toString()))
            totalTv.setTextWithVisibliy(getString(R.string.rupee_price, (price - credits).toString()))
            taxesTv.setTextWithVisibliy(getString(R.string.rupee_price, tax.toString()))
        }
        orderBtn.setOnClickListener {
            replaceFragmentSafely(
                CheckoutPersonalDetailsFragment(),
                containerViewId = R.id.checkoutContainer,
                addToStack = true
            )
        }
        removeProduct.setOnClickListener {
            toast(getString(R.string.items_removed))
            vm.clearCart()
            emptyCart.isVisible = true
            cartLayout.isVisible = false
        }
    }
}
