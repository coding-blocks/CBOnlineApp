package com.codingblocks.cbonlineapp

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.otherwise
import com.codingblocks.onlineapi.Clients
import org.koin.android.ext.android.inject

class URLRouterActivity : BaseCBActivity() {

    private fun fallBack(uri: Uri, loggedIn: Boolean) {
        if (uri.pathSegments.size > 1) {
            openChrome("", uri = uri)
        } else {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, loggedIn))
        }
    }

    private val sharedPrefs: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val key = sharedPrefs.SP_JWT_TOKEN_KEY
        if (key.isNotEmpty() && !JWTUtils.isExpired(key)) {
            Clients.authJwt = sharedPrefs.SP_JWT_TOKEN_KEY
            Clients.refreshToken = sharedPrefs.SP_JWT_REFRESH_TOKEN
            intent?.data?.let { uri ->

                if (TextUtils.isEmpty(uri.host)) fallBack(uri, true)
                if (!uri.host!!.contains("online.codingblocks.com")) fallBack(uri, true)

                val pathSegments = uri.pathSegments
                if (pathSegments.size < 1) {
                    fallBack(uri, true)
                    finish()
                    return
                }

                when (pathSegments[0]) {
                    "classroom" -> openRouter(uri)
                    "courses" -> openRouter(uri)
                    "player" -> openRouter(uri)
                    "app" -> openRouter(uri)
                    else -> fallBack(uri, true)
                }
                finish()
            }
                ?: finish()
        } else {
            fallBack(Uri.EMPTY, false)
        }
    }

    private fun openRouter(uri: Uri) {
        Router.open("activity://courseRun/$uri").otherwise { fallBack(uri, true) }
        finish()
    }
}
