package com.codingblocks.cbonlineapp.activities

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.Prefs
import com.codingblocks.cbonlineapp.Utils.getPrefs
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.fragments.AllCourseFragment
import com.codingblocks.cbonlineapp.fragments.HomeFragment
import com.codingblocks.cbonlineapp.fragments.MyCoursesFragment
import com.codingblocks.cbonlineapp.utils.Components
import com.codingblocks.onlineapi.Clients
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.util.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AnkoLogger {
    private var doubleBackToExitPressedOnce = false
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getPrefs()
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        title = "Coding Blocks"
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.commit()
        if (prefs.SP_ACCESS_TOKEN_KEY != "access_token" && !isNetworkAvailable(this)) {
            transaction.replace(R.id.fragment_holder, MyCoursesFragment())
            setUser()
        } else {
            transaction.replace(R.id.fragment_holder, HomeFragment())
        }
        when {
            intent.getStringExtra("course") == "mycourses" -> transaction.replace(R.id.fragment_holder, MyCoursesFragment())
            intent.getStringExtra("course") == "allcourses" -> transaction.replace(R.id.fragment_holder, AllCourseFragment())
        }
        nav_view.getHeaderView(0).login_button.setOnClickListener {
            startActivity(intentFor<LoginActivity>().singleTop())
        }
        fetchUser()
    }

    private fun setUser() {
        Picasso.get().load(prefs.SP_USER_IMAGE).placeholder(R.drawable.defaultavatar).fit().into(nav_view.getHeaderView(0).nav_header_imageView)
        nav_view.getHeaderView(0).login_button.text = "Logout"
        if (Build.VERSION.SDK_INT >= 25) {
            createShortcut()
        }
        nav_view.getHeaderView(0).login_button.setOnClickListener {
            prefs.SP_ACCESS_TOKEN_KEY = Prefs.ACCESS_TOKEN
            prefs.SP_JWT_TOKEN_KEY = Prefs.JWT_TOKEN
            removeShortcuts()
            startActivity(intentFor<LoginActivity>().singleTop())
            finish()
        }
        val nav_menu = nav_view.menu
        nav_menu.findItem(R.id.nav_my_courses).isVisible = true
    }

    override fun onStart() {
        super.onStart()
        val data = this.intent.data
        if (data != null && data.isHierarchical) {
            if (data.getQueryParameter("code") != null) {
                fetchToken(data)
            }
        }
    }

    private fun fetchToken(data: Uri) {
        val grantCode = data.getQueryParameter("code")
        Clients.api.getToken(grantCode).enqueue(retrofitCallback { error, response ->
            if (response!!.isSuccessful) {

                val jwt = response.body()?.asJsonObject?.get("jwt")?.asString!!
                val rt = response.body()?.asJsonObject?.get("refresh_token")?.asString!!
                prefs.SP_ACCESS_TOKEN_KEY = grantCode
                prefs.SP_JWT_TOKEN_KEY = jwt
                prefs.SP_JWT_REFRESH_TOKEN = rt
                Clients.authJwt = jwt
                fetchUser()
                Toast.makeText(this@HomeActivity, "Logged In", Toast.LENGTH_SHORT).show()
            } else if (response.code() == 500 && prefs.SP_ACCESS_TOKEN_KEY == "access_token") {
                Components.showconfirmation(this, "verify")
            }

        })
    }

    private fun fetchUser() {
        if (prefs.SP_ACCESS_TOKEN_KEY != "access_token") {
            Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
            Clients.api.getMe().enqueue(retrofitCallback { t, resp ->
                resp?.body()?.let { it ->
                    if (resp.isSuccessful) {
                        try {
                            val jSONObject = it.getAsJsonObject("data").getAsJsonObject("attributes")
                            prefs.SP_USER_IMAGE = jSONObject.get("photo").asString
                        } catch (e: Exception) {

                        }
                        setUser()
                    } else {
                        nav_view.getHeaderView(0).login_button.setOnClickListener {
                            startActivity(intentFor<LoginActivity>().singleTop())

                        }
                    }

                }
                info { "login error ${t?.localizedMessage}" }

            })
        } else {
            nav_view.getHeaderView(0).login_button.setOnClickListener {
                startActivity(intentFor<LoginActivity>().singleTop())
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            Handler().postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_all_courses -> {
                changeFragment("All Courses")
            }
            R.id.nav_home -> {
                changeFragment("Home")

            }
            R.id.nav_my_courses -> {
                changeFragment("My Courses")
            }
            R.id.nav_whatsapp -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse("https://wa.me/919811557517")
                if (packageManager.resolveActivity(intent, 0) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please install whatsApp", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_preferences -> {
                startActivity(intentFor<SettingsActivity>().singleTop())
            }
            R.id.nav_contactUs -> {
                startActivity(intentFor<AboutActivity>().singleTop())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun changeFragment(filter: String) {
        val transaction = supportFragmentManager.beginTransaction()
        when (filter) {
            "All Courses" -> transaction.replace(R.id.fragment_holder, AllCourseFragment())
            "Home" -> transaction.replace(R.id.fragment_holder, HomeFragment())
            "My Courses" -> transaction.replace(R.id.fragment_holder, MyCoursesFragment())
        }

        transaction.commit()
        onBackPressed()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        //now getIntent() should always return the last received intent
    }

    @TargetApi(25)
    private fun createShortcut() {
        val sM = getSystemService(ShortcutManager::class.java)

        val intent1 = Intent(applicationContext, HomeActivity::class.java)
        intent1.action = Intent.ACTION_VIEW
        intent1.putExtra("course", "mycourses")

        val shortcut1 = ShortcutInfo.Builder(this, "shortcut1")
                .setIntent(intent1)
                .setLongLabel("My Courses")
                .setShortLabel("Open My Courses")
                .setDisabledMessage("Login to open this")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .build()

        sM.dynamicShortcuts = Arrays.asList(shortcut1)
    }

    @TargetApi(25)
    private fun removeShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        shortcutManager.disableShortcuts(Arrays.asList("shortcut1"))
        shortcutManager.removeAllDynamicShortcuts()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}
