package com.codingblocks.cbonlineapp.course.checkout

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_checkout_order_details.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DecimalFormat

class CheckoutOrderDetailsFragment : Fragment(), AnkoLogger {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_order_details, container, false)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.cart.observer(viewLifecycleOwner) {
            orderBtn.isEnabled = true
            finalPriceTv.text = "${getString(R.string.rupee_sign)} ${it["totalAmount"].asString}"
            it.getAsJsonArray("cartItems")?.get(0)?.asJsonObject?.run {
                val df = DecimalFormat("0.00")

                get("image_url")?.asString?.let { it1 -> courseLogoImg.loadImage(it1) }
                courseTitleTv.text = get("productDescription")?.asString
                batchTileTv.text = get("productName")?.asString
                val price = get("final_price")?.asDouble?.div(100).toString()
                priceTv.text = "${getString(R.string.rupee_sign)} $price"
                mrpTv.apply {
                    text = "${getString(R.string.rupee_sign)} ${get("list_price")?.asDouble?.div(100)}"
                    paintFlags = mrpTv.paintFlags or
                        Paint.STRIKE_THRU_TEXT_FLAG
                }
                vm.map["invoice_id"] = get("invoice_id").asString
                subTotalTv.text = "${getString(R.string.rupee_sign)} $price"
                totalTv.text = "${getString(R.string.rupee_sign)} $price"
                taxesTv.text = "${getString(R.string.rupee_sign)} ${get("tax")?.asDouble?.div(100)}"
                orderBtn.setOnClickListener {
                    replaceFragmentSafely(CheckoutPersonalDetailsFragment(), containerViewId = R.id.checkoutContainer, addToStack = true)
                }
            }
        }
    }
}
