package com.codingblocks.cbonlineapp.mycourse.overview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.home.loadData
import com.codingblocks.cbonlineapp.dashboard.home.setGradientColor
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.DIALOG_TYPE
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.livedata.observeOnce
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.util.showConfirmDialog
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.item_certificate.*
import kotlinx.android.synthetic.main.item_hb_performance.*
import kotlinx.android.synthetic.main.item_performance.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.util.*

class OverviewFragment : BaseCBFragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()
    private val leaderBoardListAdapter = LeaderBoardListAdapter()
    var confirmDialog: AlertDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseLeaderboardRv.setRv(requireContext(), leaderBoardListAdapter)
        viewModel.run?.distinctUntilChanged()?.observer(thisLifecycleOwner) { courseAndRun ->
            if (courseAndRun.runAttempt.paused) {
                showPauseDialog(courseAndRun.runAttempt.pauseTimeLeft)
            }
            viewModel.premiumRun = courseAndRun.runAttempt.premium
            viewModel.runStartEnd =
                Pair(courseAndRun.runAttempt.end.toLong() * 1000, courseAndRun.run.crStart.toLong())
            viewModel.runId = (courseAndRun.run.crUid)
            viewModel.courseId = courseAndRun.course.cid
            val progressValue = courseAndRun.getProgress()
            homeProgressTv.text = getString(R.string.progress, progressValue.toInt())
            homeProgressView.setGradientColor(progressValue)
            certificate_descTv.apply {
                text = getString(R.string.certificate_desc, courseAndRun.run.completionThreshold)
            }
            progressTv.apply {
                text = getString(R.string.thresholdcompletion, courseAndRun.run.completionThreshold)
                isActivated = courseAndRun.run.completionThreshold < progressValue
            }
            mentorApprovalTv.apply {
                isActivated = courseAndRun.runAttempt.approvalRequested
                val status =
                    if (courseAndRun.runAttempt.approvalRequested) "Requested" else "Pending"
                text = getString(R.string.mentorapproval, status)
            }
            if (progressTv.isActivated && mentorApprovalTv.isActivated && courseAndRun.runAttempt.certificateApproved) {
                requestCertificateBtn.apply {
                    isEnabled = true
                    text = getString(R.string.download_share)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_share, 0)
                    setOnClickListener {
                        downloadCertificate(
                            requireContext(),
                            courseAndRun.runAttempt.certificateUrl,
                            "${viewModel.name}.pdf"
                        )
                    }
                }
            } else if (progressTv.isActivated && !mentorApprovalTv.isActivated) {
                requestCertificateBtn.apply {
                    isEnabled = true
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    setOnClickListener {
                        isEnabled = false
                        text = getString(R.string.requested)
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.brownish_grey
                            )
                        )
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0)
                        viewModel.requestMentorApproval()
                    }
                }
            }
            courseAndRun.run.whatsappLink?.let {
                if (it.isNotEmpty()) {
                    setWhatsappCard(it, courseAndRun.runAttempt.premium)
                }
            }
            //TODO : Add Goodies Card
//            if (courseAndRun.run.crStart > "1574985600") {
//                if (courseAndRun.run.crPrice > 10.toString() && courseAndRun.runAttempt.premium && RUNTIERS.LITE.name != courseAndRun.runAttempt.runTier)
//               setGoodiesCard(courseAndRun.run.goodiesThreshold, progressValue)
//            }
            viewModel.getLeaderboard().observer(thisLifecycleOwner) { leaderboard ->

                val currUserLeaderboard = leaderboard.find { it.id == viewModel.prefs.SP_USER_ID }
                currUserLeaderboard?.let {
                    it.id = (leaderboard.indexOf(currUserLeaderboard) + 1).toString()
                    if (leaderboard.size > 5) {
                        courseLeaderboardll.isVisible = true
                        leaderBoardListAdapter.submitList(
                            mutableListOf(currUserLeaderboard) + leaderboard.subList(
                                0,
                                5
                            )
                        )
                    }
                }
            }
        }

        viewModel.performance?.observer(thisLifecycleOwner) {
            homePerformanceTv.text = it.remarks
            homePercentileTv.text = it.percentile.toString()
            chart1.loadData(it.averageProgress, it.userProgress)
        }

        viewModel.getHackerBlocksPerformance().distinctUntilChanged().observe(
            thisLifecycleOwner,
            Observer {
                if (it != null) {
                    hbRankContainer.isVisible = true
                    currentOverallRank.text = it.currentOverallRank.toString()
                    currentMonthScore.text =
                        requireContext().getString(R.string.points, it.currentMonthScore)
                    val rankDelta = it.currentOverallRank - it.previousOverallRank
                    val pointsDelta = it.currentMonthScore - it.previousMonthScore
                    previousRank.apply {
                        isActivated = rankDelta >= 0
                        text = context.getString(R.string.ranks, kotlin.math.abs(rankDelta))
                    }
                    previousMonthlyScore.apply {
                        isActivated = pointsDelta >= 0
                        text = context.getString(R.string.points, pointsDelta)
                    }
                }
            }
        )

        confirmReset.setOnClickListener {
            CustomDialog.showConfirmation(requireContext(), "reset") {
                if (it) {
                    viewModel.resetProgress.observer(thisLifecycleOwner) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun showPauseDialog(pauseTimeLeft: String?) {
        if (confirmDialog == null)
            confirmDialog = showConfirmDialog(DIALOG_TYPE.PAUSED) {
                cancelable = false
                val time =
                    PrettyTime().format(Date(System.currentTimeMillis() + pauseTimeLeft!!.toLong()))
                desc.text = getString(R.string.pause_time_left, time)
                positiveBtnClickListener {
                    viewModel.unPauseCourse().observeOnce {
                        dialog?.dismiss()
                    }
                }
                negativeBtnClickListener { requireActivity().finish() }
            }
        confirmDialog?.show()
    }

    private fun downloadCertificate(context: Context, certificateUrl: String, name: String) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name)
        if (file.exists()) {
            val fileUri =
                FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            context.grantUriPermission(
                requireContext().packageName,
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val intentShareFile = Intent(Intent.ACTION_SEND)
            intentShareFile.type = "application/pdf"
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
            startActivity(Intent.createChooser(intentShareFile, "Share Certificate"))
        } else {
            viewModel.downloadCertificateAndShow(context, certificateUrl, name)
        }
    }

    private fun setGoodiesCard(goodiesThreshold: Int, progress: Double) {
        goodiesContainer.isVisible = true
        val canRequest = progress > goodiesThreshold
        goodiesRequestTv.isActivated = canRequest
        goodiesTv.apply {
            text = if (canRequest)
                getString(R.string.goodiedesc, goodiesThreshold)
            else
                getString(R.string.goodiedesclocked, goodiesThreshold)
        }
        goodiesContainer.setOnClickListener {
            toast("Will be added soon")

//            if (canRequest) {
//                showGoodiesForm()
//            }
        }
    }

    private fun showGoodiesForm() {
        val goodiesRequestFragment = GoodiesRequestFragment()
        goodiesRequestFragment.show(parentFragmentManager, "goodiesRequestFragment")
    }

    private fun setWhatsappCard(link: String, premium: Boolean) {
        whatsappContainer.apply {
            isVisible = premium
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse(link)
                if (requireContext().packageManager.resolveActivity(intent, 0) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Please install whatsApp", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
