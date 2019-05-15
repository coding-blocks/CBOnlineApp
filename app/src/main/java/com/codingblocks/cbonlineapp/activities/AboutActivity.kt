package com.codingblocks.cbonlineapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.email

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        callTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18002744504"))
            startActivity(intent)
        }
        mailTv.setOnClickListener {
            email(mailTv.text.toString())
        }

        phone1.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone1.text}"))
            startActivity(intent)
        }

        phone2.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone2.text}"))
            startActivity(intent)
        }

        email1.setOnClickListener {
            email(mailTv.text.toString())
        }
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
