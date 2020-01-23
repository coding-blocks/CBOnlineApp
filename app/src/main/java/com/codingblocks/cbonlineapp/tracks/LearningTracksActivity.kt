package com.codingblocks.cbonlineapp.tracks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_learning_tracks.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LearningTracksActivity : AppCompatActivity() {

    private val tracksListAdapter = TracksListAdapter("LIST")
    private val vm by viewModel<DashboardViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_tracks)
        setToolbar(tracksToolbar)
        vm.fetchTracks()
        tracksRv.setRv(this, tracksListAdapter)
        vm.tracks.observer(this) { courses ->
            tracksListAdapter.submitList(courses)
        }
    }
}
