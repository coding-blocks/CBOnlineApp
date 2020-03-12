package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.FileUtils.loadJsonObjectFromAsset
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_checkout_personal_details.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONArray
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutPersonalDetailsFragment : BaseCBFragment(), AnkoLogger {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_checkout_personal_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getCart()
        viewLifecycleOwnerLiveData.observer(viewLifecycleOwner) {
            info { it.lifecycle.currentState.name }
        }
        if (vm.map["stateId"] != null)
            checkoutBtn.isEnabled = true
        vm.cart.observer(viewLifecycleOwner) {
            finalPriceTv.text = "${getString(R.string.rupee_sign)} ${it["totalAmount"].asString}"
        }
        val json = loadJsonObjectFromAsset(requireContext(), "csvjson.json") as JSONArray?

        val refList: MutableList<String> = ArrayList()
        try {
            for (i in 0 until json?.length()!!) {
                val ref = json.getJSONObject(i).getString("state")
                refList.add(ref)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, refList)
        state.setAdapter(arrayAdapter)
        state.setOnItemClickListener { parent, view, position, id ->
            val name = arrayAdapter.getItem(position)
            for (i in 0 until json?.length()!!) {
                val ref = json.getJSONObject(i)
                if (ref.getString("state") == name) {
                    vm.map["stateId"] = ref.getString("stateId")
                }
            }
            checkoutBtn.isEnabled = true
            vm.updateCart()
        }
        checkoutBtn.setOnClickListener {
            replaceFragmentSafely(
                CheckoutPaymentFragment(),
                tag = "Personal",
                containerViewId = R.id.checkoutContainer,
                addToStack = true
            )
        }
    }
}
