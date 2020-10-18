package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.FileUtils.loadJsonObjectFromAsset
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.fragment_checkout_personal_details.*
import org.jetbrains.anko.AnkoLogger
import org.json.JSONArray
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutPersonalDetailsFragment : BaseCBFragment(), AnkoLogger {

    val vm by sharedViewModel<CheckoutViewModel>()

    companion object {
        const val TAG = "Personal"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_checkout_personal_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getCart()
        if (vm.map["stateId"] != null)
            checkoutBtn.isEnabled = true
        vm.cart.observer(thisLifecycleOwner) {
            finalPriceTv.text = "${getString(R.string.rupee_sign)} ${it["totalAmount"].asString}"
        }
        setUpStateAutoCompleteView()
        checkoutBtn.setOnClickListener {
            replaceFragmentSafely(
                CheckoutPaymentFragment(),
                tag = TAG,
                containerViewId = R.id.checkoutContainer,
                addToStack = true
            )
        }
    }

    private fun setUpStateAutoCompleteView() {
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
        state.setOnItemClickListener { _, _, position, _ ->
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
    }
}
