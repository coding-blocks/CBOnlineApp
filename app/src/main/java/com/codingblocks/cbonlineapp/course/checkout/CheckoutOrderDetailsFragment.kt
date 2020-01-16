package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.codingblocks.cbonlineapp.debug.R
/**
 * A simple [Fragment] subclass.
 */
class CheckoutOrderDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout_order_details, container, false)
    }

}
