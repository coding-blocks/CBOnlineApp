package com.codingblocks.cbonlineapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.fragments.AllCourseFragment
import com.codingblocks.cbonlineapp.fragments.HomeFragment
import com.codingblocks.cbonlineapp.fragments.MyCoursesFragment
import com.codingblocks.onlineapi.Clients
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AnkoLogger {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        title = "Coding Blocks"
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, AllCourseFragment())
        transaction.commit()

        fetchUser()
    }


    private fun fetchUser() {
        if (!prefs.SP_ACCESS_TOKEN_KEY.equals("access_token")) {
            info { prefs.SP_ACCESS_TOKEN_KEY }
            Clients.api.getMe("JWT " + prefs.SP_JWT_TOKEN_KEY).enqueue(retrofitcallback { t, resp ->
                resp?.body()?.let {
                    if (resp.isSuccessful) {
                        val jSONObject = resp.body()!!.getAsJsonObject("data").getAsJsonObject("attributes")
                        Picasso.get().load(jSONObject.get("photo").asString).into(nav_header_imageView)
//                nav_header_username_textView.text = jSONObject.get("firstname").asString
                        login_button.text = "Logout"
                        login_button.setOnClickListener {
                            prefs.SP_ACCESS_TOKEN_KEY = prefs.ACCESS_TOKEN
                            prefs.SP_JWT_TOKEN_KEY = prefs.JWT_TOKEN
                            startActivity(intentFor<LoginActivity>().singleTop())
                            finish()
                        }
                        val nav_menu = nav_view.menu
                        nav_menu.findItem(R.id.nav_my_courses).setVisible(true)
                    } else {
                        nav_view.getHeaderView(0).login_button.setOnClickListener {
                            startActivity(intentFor<LoginActivity>().singleTop())
                        }
                    }

                }

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
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
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
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun changeFragment(filter: String) {
        val transaction = supportFragmentManager.beginTransaction()
        if (filter == "All Courses")
            transaction.replace(R.id.fragment_holder, AllCourseFragment())
        else if (filter == "Home")
            transaction.replace(R.id.fragment_holder, HomeFragment())
        else if (filter == "My Courses")
            transaction.replace(R.id.fragment_holder, MyCoursesFragment())

        transaction.commit()
        onBackPressed()
    }
}
