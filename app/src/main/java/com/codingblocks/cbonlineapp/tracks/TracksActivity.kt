package com.codingblocks.cbonlineapp.tracks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_tracks.*

class TracksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)
        setToolbar(tracksToolbar)
    }
}
