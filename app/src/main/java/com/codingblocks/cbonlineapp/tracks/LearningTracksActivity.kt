package com.codingblocks.cbonlineapp.tracks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.course.ItemClickListener
import com.codingblocks.cbonlineapp.course.batches.BatchListAdapter
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.PROFESSIONAL
import com.codingblocks.cbonlineapp.util.STUDENT
import com.codingblocks.cbonlineapp.util.extensions.*
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Professions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_learning_tracks.*
import kotlinx.android.synthetic.main.bottom_sheet_batch.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class LearningTracksActivity : BaseCBActivity() {

    private val tracksListAdapter = TracksListAdapter("LIST")
    private val vm by viewModel<TrackViewModel>()
    private val dialog by lazy { BottomSheetDialog(this) }
    private val batchListAdapter = BatchListAdapter()

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(this@LearningTracksActivity, TrackActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@LearningTracksActivity,
                        logo,
                        ViewCompat.getTransitionName(logo)!!
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_tracks)
        setToolbar(tracksToolbar)
        setUpBottomSheet()
        setChips()
        activityLearningShimmer.showShimmer(true)
        tracksRv.setRv(this, tracksListAdapter)

        vm.fetchTracks().observer(this) { courses ->
            tracksListAdapter.submitList(courses)
        }
        vm.fetchProfessions().observer(this) {
            it?.takeIf { it.isNotEmpty() }?.get(0)?.let { it1 -> setProfession(it1) }
            batchListAdapter.submitList(it)
            activityLearningShimmer.hideAndStop()
        }


        needHelp.setOnClickListener {
            showHelpDialog(type = "Track") { b: Boolean, name: String, number: String ->
                vm.generateLead(name, number)
                root.showSnackbar("Your response has been submitted successfully", action = false)
            }
        }
        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showOffline()
                }
            }
        }

        tracksListAdapter.onItemClick = itemClickListener
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_batch, null)
        sheetDialog.run {
            batchRv.setRv(this@LearningTracksActivity, batchListAdapter)
        }
        batchListAdapter.onItemClick = {

            setProfession(it as Professions)
            dialog.dismiss()
        }
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
        trackBtn.setOnClickListener {
            dialog.show()
        }
    }

    private fun setProfession(professions: Professions) {
        trackBtn.text = professions.title
        getTrackBtn.setOnClickListener {
            try {
                vm.getRecommendedTrack(professions.id).nonNull().observeOnce {
                    startActivity(intentFor<TrackActivity>(COURSE_ID to it.id))
                }
            } catch (e: Exception) {
                toast("No Track Found !!!")
            }
        }
    }

    private fun setChips() {
        studentBtn.setOnClickListener {
            vm.type.value = STUDENT
        }

        professionBtn.setOnClickListener {
            vm.type.value = PROFESSIONAL
        }

        vm.type.observer(this) {
            when (it) {
                STUDENT -> {
                    studentBtn.isActivated = true
                    professionBtn.isActivated = false
                }
                PROFESSIONAL -> {
                    studentBtn.isActivated = false
                    professionBtn.isActivated = true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }
}
