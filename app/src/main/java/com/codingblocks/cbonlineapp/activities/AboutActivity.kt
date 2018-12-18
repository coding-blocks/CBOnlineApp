package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.email
import org.jetbrains.anko.makeCall

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        callTv.setOnClickListener {
            makeCall("18002744504")
        }
        mailTv.setOnClickListener {
            email(mailTv.text.toString())
        }
    }
}
