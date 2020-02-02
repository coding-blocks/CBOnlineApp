package com.codingblocks.cbonlineapp.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.email

class AboutActivity : BaseCBActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        callTv.setOnClickListener {
            dialPhone(callTv.text.toString())
        }
        mailTv.setOnClickListener {
            email(mailTv.text.toString())
        }

        phone1_1.setOnClickListener {
            dialPhone(phone1_1.text.toString())
        }

        phone1_2.setOnClickListener {
            dialPhone(phone1_2.text.toString())
        }

        email1_1.setOnClickListener {
            email(mailTv.text.toString())
        }
    }

    fun dialPhone(s: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$s"))
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
