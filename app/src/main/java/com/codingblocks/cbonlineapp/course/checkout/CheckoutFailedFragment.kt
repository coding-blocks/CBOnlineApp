package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import kotlinx.android.synthetic.main.payment_failed_layput.*

/**
 * A simple [Fragment] subclass.
 */
class CheckoutFailedFragment : BaseCBFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.payment_failed_layput, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retryBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}
