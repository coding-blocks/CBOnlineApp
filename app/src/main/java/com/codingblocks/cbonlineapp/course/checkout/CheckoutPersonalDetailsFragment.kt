package com.codingblocks.cbonlineapp.course.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_checkout_personal_details.*
import org.json.JSONArray
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.InputStream

class CheckoutPersonalDetailsFragment : Fragment() {

    val vm by sharedViewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_checkout_personal_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val json = loadJsonObjectFromAsset("csvjson.json")
        val refList: MutableList<String> = ArrayList()
        try {
            for (i in 0 until json?.length()!!) {
                val ref = json.getJSONObject(i).getString("state")
                refList.add(ref)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, refList)
        spinner.setAdapter(arrayAdapter)
        spinner.setOnItemClickListener { parent, view, position, id ->
            vm.map["stateId"] = json?.getJSONObject(position)?.getString("stateId") ?: "DL"
            checkoutBtn.isEnabled = true
        }
        checkoutBtn.setOnClickListener {
            vm.updateCart()
            replaceFragmentSafely(CheckoutPaymentFragment(), containerViewId = R.id.checkoutContainer, enterAnimation = R.animator.slide_in_right, exitAnimation = R.animator.slide_out_left, addToStack = true)
        }
    }

    private fun loadJsonObjectFromAsset(assetName: String): JSONArray? {
        try {
            val json = loadStringFromAsset(assetName)
            if (json != null) return JSONArray(json)
        } catch (e: Exception) {
            Log.e("JsonUtils", e.toString())
        }
        return null
    }

    private fun loadStringFromAsset(assetName: String): String? {
        val `is`: InputStream = requireContext().assets.open(assetName)
        val size: Int = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        return String(buffer)
    }
}
