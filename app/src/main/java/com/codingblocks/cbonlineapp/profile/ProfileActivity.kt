package com.codingblocks.cbonlineapp.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.SplashActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.JSON_TYPE_OBJECT
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.intentFor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : BaseCBActivity() {

    private val vm by viewModel<ProfileViewModel>()
    private val sharedPrefs by inject<PreferenceHelper>()
    val db: AppDatabase by inject()

    var map = HashMap<String, String>()
    val id by lazy {
        JWTUtils.getIdentity(sharedPrefs.SP_JWT_TOKEN_KEY)
    }

    companion object {
        const val FILE_NAME_PATH = "demographics.json"
        const val JSON_COLLEGES_KEY = "colleges"
        const val JSON_BRANCHES_KEY = "branches"
        const val JSON_CHILD_KEY = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setToolbar(profileToolbar)
        updateViews(false)
        setUpObserver()
        setUpClickListeners()
        setList()
    }

    private fun setUpClickListeners() {
        ediBtn.setOnClickListener {
            updateViews(true)
        }
    }

    private fun setUpObserver() {
        vm.fetchUser().observer(this) {
            graduation.setText(it.graduationyear)
            college.setText(it.college)
            email.setText(it.email)
            mobile.setText(it.mobile)
            if (it.verifiedemail != null) {
                emailLayout.endIconMode = END_ICON_CUSTOM
            }
            if (it.verifiedemail != null) {
                phoneLayout.endIconMode = END_ICON_CUSTOM
            }
            nameTv.text = "${it.firstname} ${it.lastname}"
            userNameTv.text = it.username
            userImgView.loadImage(it.photo ?: "", scale = true)
            branch.setText(it.branch ?: "Computer Science")
        }
    }

    private fun updateViews(visible: Boolean) {
        ediBtn.isVisible = !visible
        updateBtn.isVisible = visible
        logoutBtn.isVisible = !visible
        listOf(branch, college, graduation).forEach {
            it.isFocusableInTouchMode = visible
            it.isCursorVisible = visible
            it.isClickable = visible
        }
    }

    /*
    * Sets up the college and branch list for the auto complete views
    */
    private fun setList() {
        val json = FileUtils.loadJsonObjectFromAsset(this, FILE_NAME_PATH, JSON_TYPE_OBJECT) as JSONObject?
        setUpAutoCompleteViews(college, JSON_COLLEGES_KEY, json, JSON_CHILD_KEY, ::collegeClickActionHandler)
        setUpAutoCompleteViews(branch, JSON_BRANCHES_KEY, json, JSON_CHILD_KEY, ::branchClickActionHandler)
    }

    /*
    * Branch click listener being passed as lambda in the parent
    * function.
    */
    private fun branchClickActionHandler(adapter: ArrayAdapter<String>, jsonArray: JSONArray) {
        branch.setOnItemClickListener { _, _, position, _ ->
            val name = adapter.getItem(position)
            for (i in 0 until jsonArray.length()) {
                val ref = jsonArray.getJSONObject(i)
                if (ref.getString("name") == name) {
                    map["branchId"] = ref.getString("id")
                }
            }
        }
    }

    /*
    * College click listener being passed as lambda in the parent
    * function.
    */
    private fun collegeClickActionHandler(adapter: ArrayAdapter<String>, jsonArray: JSONArray) {
        college.setOnItemClickListener { _, _, position, _ ->
            val name = adapter.getItem(position)
            for (i in 0 until jsonArray?.length()!!) {
                val ref = jsonArray.getJSONObject(i)
                if (ref.getString("name") == name) {
                    map["branchId"] = ref.getString("id")
                }
            }
        }
    }

    /*
    * Sets up autocomplete listener to the views.
    * Generic autocomplete listener for branch and college autocomplete
    * can be extended to other views as well
    */
    private fun setUpAutoCompleteViews(
        view: AutoCompleteTextView,
        jsonKey: String,
        json: JSONObject?,
        jsonChildKey: String,
        mapperLambda: (adapter: ArrayAdapter<String>, jsonArray: JSONArray) -> Unit
    ) {
        json ?: return
        val jsonArray: JSONArray? = json.getJSONArray(jsonKey)
        jsonArray ?: return
        val jsonList: MutableList<String> = ArrayList()
        try {
            for (i in 0 until jsonArray.length()) {
                val ref = jsonArray.getJSONObject(i)?.getString(jsonChildKey)
                if (ref != null) {
                    jsonList.add(ref)
                }
            }
            if (jsonList.size == 0) return
            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, jsonList)
            view.setAdapter(arrayAdapter)
            mapperLambda.invoke(arrayAdapter, jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun updateProfile(view: View) {
        map["gradYear"] = graduation.text.toString()
        updateBtn.isEnabled = false
        vm.updateUser(id.toString(), map).observer(this) {
            when (it) {
                "Updated Successfully" -> {
                    updateViews(false)
                    profileRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
                }
                else -> {
                    updateBtn.isEnabled = true
                    profileRoot.showSnackbar(it, Snackbar.LENGTH_SHORT)
                }
            }
        }
    }

    fun logout(view: View) {
        showDialog(
            type = "Logout",
            image = R.drawable.ic_info,
            cancelable = false,
            primaryText = R.string.logout_dialog_title,
            secondaryText = R.string.logout_dialog_description,
            primaryButtonText = R.string.confirm,
            secondaryButtonText = R.string.cancel,
            callback = { confirmed ->
                if (confirmed) {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            getExternalFilesDirs(null).forEach {
                                try {
                                    it.deleteRecursively()
                                } catch (e: Exception) {
                                    Log.e("LOGOUT", "Error deleting files", e)
                                }
                            }
                            db.clearAllTables()
                            sharedPrefs.clearPrefs()
                        }
                        startActivity(
                            intentFor<SplashActivity>().apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                        finishAffinity()
                    }
                }
            }
        )
    }
}
