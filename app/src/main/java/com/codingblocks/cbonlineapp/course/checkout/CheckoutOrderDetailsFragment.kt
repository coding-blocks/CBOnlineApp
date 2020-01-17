package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.fragment_checkout_order_details.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutOrderDetailsFragment : Fragment() {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_order_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.response.value = false

    }
}
