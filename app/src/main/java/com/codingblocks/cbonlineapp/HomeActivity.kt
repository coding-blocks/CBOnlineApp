package com.codingblocks.cbonlineapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.codingblocks.cbonlineapp.API.Client
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.jetbrains.anko.AnkoLogger


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        fab_whatsapp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "919811557517"))
            if (packageManager.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please install whatsApp", Toast.LENGTH_SHORT).show()
            }
        }
        fetchUser()
    }

    private fun fetchUser() {
        if (!prefs.SP_ACCESS_TOKEN_KEY.equals("access_token")) {
            Client.api.getMe("Bearer " + prefs.SP_ACCESS_TOKEN_KEY).enqueue(retrofitcallback { t, resp ->
                resp?.body()?.let {
                    Picasso.get().load(it.photo).into(nav_header_imageView)
                    nav_header_username_textView.text = it.username
                    nav_header_email_textView.text = it.email
                }

            })
        } else {
            nav_header_username_textView.text = "Guest"
            nav_header_email_textView.text = "Hi there!"
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
                // Handle the camera action
            }
            R.id.nav_my_courses -> {

            }
            R.id.nav_notifications -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
