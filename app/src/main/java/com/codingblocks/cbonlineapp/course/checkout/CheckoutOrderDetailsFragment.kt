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
import com.codingblocks.cbonlineapp.util.glide.loadImage
import kotlinx.android.synthetic.main.fragment_checkout_order_details.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutOrderDetailsFragment : BaseCBFragment(), AnkoLogger {

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
                emptyCart.isVisible = false
                cartLayout.isVisible = true
                orderBtn.isEnabled = true
                finalPriceTv.text = getString(R.string.rupee_price, res["totalAmount"].asString)
                res.getAsJsonObject("cartItems")?.run {
                    courseTitleTv.text = get("productDescription")?.asString
                    courseLogoImg.loadImage(get("image_url").asString)
                    batchTileTv.text = get("productName").asString
                    val price = get("final_price").asInt.div(100)
                    val credits = get("credits_used").asInt.div(100)
                    val mrp = get("list_price").asInt.div(100)
                    val tax = get("tax").asDouble.div(100)

                    priceTv.text = getString(R.string.rupee_price, price.toString())
                    creditsTv.text = getString(R.string.rupee_price, credits.toString())
                    mrpTv.apply {
                        text = getString(R.string.rupee_price, mrp.toString())
                        paintFlags = mrpTv.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG
                    }

                    vm.map["invoice_id"] = get("invoice_id").asString
                    subTotalTv.text = getString(R.string.rupee_price, price.toString())
                    totalTv.text = getString(R.string.rupee_price, (price - credits).toString())
                    taxesTv.text = getString(R.string.rupee_price, tax.toString())
                }
                orderBtn.setOnClickListener {
                    replaceFragmentSafely(
                        CheckoutPersonalDetailsFragment(),
                        containerViewId = R.id.checkoutContainer,
                        addToStack = true
                    )
                }
                removeProduct.setOnClickListener {
                    toast("Items Removed")
                    vm.clearCart()
                    emptyCart.isVisible = true
                    cartLayout.isVisible = false
                }
            }
        }

        exploreBtn.setOnClickListener { requireActivity().finish() }
        removeProduct.setOnClickListener(null)
    }
}
