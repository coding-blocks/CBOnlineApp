package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var navController: NavController

    private lateinit var navigationView: NavigationView

    private var currentFragmentId: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true

        drawer_layout.closeDrawers()

        val id = menuItem.itemId

        when (id) {

//            R.id.first -> navController.navigate(R.id.firstFragment)
//
//            R.id.second -> navController.navigate(R.id.secondFragment)
//
//            R.id.third -> navController.navigate(R.id.thirdFragment)
        }
        return true
    }
}
