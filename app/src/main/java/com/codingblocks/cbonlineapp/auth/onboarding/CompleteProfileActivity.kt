package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_complete_profile.*
import kotlinx.android.synthetic.main.fragment_checkout_personal_details.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val sharedPrefs by inject<PreferenceHelper>()
    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
//        val id = JWTUtils.getIdentity(sharedPrefs.SP_JWT_TOKEN_KEY).toString()
        courseResumeBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
            finish()
        }
        val json = FileUtils.loadJsonObjectFromAsset(this, "demographics.json")
        val collegeList: MutableList<String> = ArrayList()
        val branchList: MutableList<String> = ArrayList()
        try {
            for (i in 0 until json?.length()!!) {
                val ref = json.getJSONArray(0).getJSONObject(i).getString("name")
                val ref2 = json.getJSONArray(1).getJSONObject(i).getString("name")
                branchList.add(ref2)
                collegeList.add(ref)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, collegeList)
        college.setAdapter(arrayAdapter)
        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branchList)
        branch.setAdapter(arrayAdapter2)
        proceedBtn.setOnClickListener {
            //            val name = nameLayout.editText?.text.toString().split(" ")
//            val number = if (mobileLayout.editText?.text?.length!! > 10) "+91-${mobileLayout.editText?.text?.substring(3)}" else "+91-${mobileLayout.editText?.text}"
//
//            if (name.size < 2) {
//                signUpRoot.showSnackbar("Last Name Cannot Be Empty", Snackbar.LENGTH_SHORT)
//            } else {
//                map["username"] = userNameLayout.editText?.text.toString()
//                map["mobile"] = number
//                map["firstname"] = name[0]
//                map["lastname"] = name[1]
//                map["email"] = emailLayout.editText?.text.toString()
//            }

            proceedBtn.isEnabled = false

            GlobalScope.launch {
                when (val response = safeApiCall { Clients.api.updateUser("id", map) }) {
                    is ResultWrapper.GenericError -> {
                        runOnUiThread {
                            proceedBtn.isEnabled = true
                            completeRoot.showSnackbar(response.error, Snackbar.LENGTH_SHORT)
                        }
                    }
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful) {
                            startActivity(intentFor<DashboardActivity>())
                            finish()
                        } else
                            runOnUiThread {
                                val errRes = response.value.errorBody()?.string()
                                val error = if (errRes.isNullOrEmpty()) "Please Try Again" else JSONObject(errRes).getString("description")
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
