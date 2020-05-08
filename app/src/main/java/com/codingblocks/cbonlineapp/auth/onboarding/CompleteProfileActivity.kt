package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_complete_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.contentView
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject

class CompleteProfileActivity : BaseCBActivity() {

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val sharedPrefs by inject<PreferenceHelper>()
    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        val id = JWTUtils.getIdentity(sharedPrefs.SP_JWT_TOKEN_KEY)
        courseResumeBtn.setOnClickListener {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, true))
            finish()
        }
        val json =
            FileUtils.loadJsonObjectFromAsset(this, "demographics.json", "obj") as JSONObject?
        val collegeArray = json?.getJSONArray("colleges")
        val branchArray = json?.getJSONArray("branches")
        val collegeList: MutableList<String> = ArrayList()
        val branchList: MutableList<String> = ArrayList()
        try {
            for (i in 0 until collegeArray?.length()!!) {
                val ref = collegeArray.getJSONObject(i)?.getString("name")
                if (ref != null) {
                    collegeList.add(ref)
                }
            }
            for (i in 0 until branchArray?.length()!!) {
                val ref = branchArray.getJSONObject(i)?.getString("name")
                if (ref != null) {
                    branchList.add(ref)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, collegeList)
        college.setAdapter(arrayAdapter)
        college.setOnItemClickListener { _, _, position, id ->
            val name = arrayAdapter.getItem(position)

            for (i in 0 until collegeArray?.length()!!) {
                val ref = collegeArray.getJSONObject(i)
                if (ref.getString("name") == name) {
                    map["collegeId"] = ref.getString("id")
                }
            }
        }

        val arrayAdapter2: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branchList)
        branch.setAdapter(arrayAdapter2)
        branch.setOnItemClickListener { _, _, position, id ->
            val name = arrayAdapter2.getItem(position)

            for (i in 0 until branchArray?.length()!!) {
                val ref = branchArray.getJSONObject(i)
                if (ref.getString("name") == name) {
                    map["branchId"] = ref.getString("id")
                }
            }
        }
        genderRadio?.setOnCheckedChangeListener { group, checkedId ->
            map["gender"] = if (R.id.radioMale == checkedId) "MALE" else "FEMALE"
        }
        apparelRadio?.setOnCheckedChangeListener { group, checkedId ->
            map["apparelGoodiesSize"] = "L"
        }
        proceedBtn.setOnClickListener {
            map["gradYear"] = graduation.text.toString()

            proceedBtn.isEnabled = false

            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.updateUser(id.toString(), map) }) {
                    is ResultWrapper.GenericError -> {
                        runOnUiThread {
                            proceedBtn.isEnabled = true
                            completeRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                        }
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful) {
                            startActivity(DashboardActivity.createDashboardActivityIntent(this@CompleteProfileActivity, true))
                            finish()
                        } else
                            runOnUiThread {
                                val errRes = response.value.errorBody()?.string()
                                val error =
                                    if (errRes.isNullOrEmpty()) "Please Try Again" else JSONObject(
                                        errRes
                                    ).getString("description")
                                completeRoot.showSnackbar(error.capitalize(), Snackbar.LENGTH_SHORT)
                                proceedBtn.isEnabled = true
                            }
                    }
                }
            }
        }

        keyboardVisibilityHelper = KeyboardVisibilityUtil(contentView!!) {
            proceedBtn.isVisible = it
        }
    }

    override fun onResume() {
        super.onResume()
        contentView!!.viewTreeObserver
            .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    override fun onPause() {
        super.onPause()
        contentView!!.viewTreeObserver
            .removeOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }
}
