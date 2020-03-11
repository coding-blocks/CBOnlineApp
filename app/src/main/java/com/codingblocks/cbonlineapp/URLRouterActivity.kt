package com.codingblocks.cbonlineapp

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.otherwise
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class URLRouterActivity : BaseCBActivity() {

    private fun fallBack(uri: Uri) {
        if (uri.pathSegments.size > 0) {
            openChrome("", uri = uri)
        } else {
            startActivity(intentFor<DashboardActivity>())
        }
    }

    private val sharedPrefs by inject<PreferenceHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sharedPrefs.SP_JWT_TOKEN_KEY.isNotEmpty()) {

            intent?.data?.let { uri ->

                if (TextUtils.isEmpty(uri.host)) fallBack(uri)
                if (!uri.host!!.contains("online.codingblocks.com")) fallBack(uri)

                val pathSegments = uri.pathSegments
                if (pathSegments.size < 2) {
                    fallBack(uri)
                    finish()
                    return
                }

                when (pathSegments[1]) {
                    "classroom" -> openRouter(uri)
                    "courses" -> openRouter(uri)
                    "player" -> openRouter(uri)
                    "tracks" -> openRouter(uri)
                    else -> fallBack(uri)
                }
                finish()
            }
                ?: finish()
        } else {
            fallBack(Uri.EMPTY)
        }
    }

    private fun openRouter(uri: Uri) {
        Router.open("activity://courseRun/$uri").otherwise { fallBack(uri) }
        finish()
    }
}
