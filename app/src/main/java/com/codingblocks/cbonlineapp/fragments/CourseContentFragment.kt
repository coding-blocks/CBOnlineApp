package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.DownloadStarter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.SectionDetailsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.CourseSection
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.google.firebase.analytics.FirebaseAnalytics
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.DownloadOptions
import com.vdocipher.aegis.offline.DownloadRequest
import com.vdocipher.aegis.offline.DownloadSelections
import com.vdocipher.aegis.offline.DownloadStatus
import com.vdocipher.aegis.offline.OptionsDownloader
import com.vdocipher.aegis.offline.VdoDownloadManager
import kotlinx.android.synthetic.main.fragment_course_content.view.rvExpendableView
import kotlinx.android.synthetic.main.fragment_course_content.view.sectionProgressBar
import kotlinx.android.synthetic.main.fragment_course_content.view.swiperefresh
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.File

private const val ARG_ATTEMPT_ID = "attempt_id"

class CourseContentFragment : Fragment(), AnkoLogger, DownloadStarter, VdoDownloadManager.EventListener {
    override fun onChanged(p0: String?, p1: DownloadStatus?) {
        info {"changed" + p0}
    }

    override fun onDeleted(p0: String?) {
        info {"deleted" + p0}
    }

    override fun onFailed(p0: String?, p1: DownloadStatus?) {
        info {"failed" + p1?.reasonDescription}
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
        info {"queued" + p0}
    }

    override fun onCompleted(p0: String?, p1: DownloadStatus?) {
        info {"complete" + p0}
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            CourseContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                }
            }
    }

    lateinit var attemptId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }
    private val courseDao by lazy {
        database.courseRunDao()
    }

    override fun startDownload(videoId: String, id: String, lectureContentId: String, title: String, attemptId: String, contentId: String, section_id: String) {
        Clients.api.getVideoDownloadKey(videoId, section_id, attemptId).enqueue(retrofitCallback { throwable, response ->
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let {
                        val mOtp = it.get("otp")?.asString
                        val mPlaybackInfo = it.get("playbackInfo")?.asString
                        initializeDownload("20160313versASE313WAGCdGbRSkojp0pMJpESFT9RVVrbGSnzwVOr2ANUxMrfZ5", "eyJ2aWRlb0lkIjoiNjYxZjY4NjFkNTIxYTI0Mjg4ZDYwODkyM2QyYzczZjkifQ==", videoId)
                    }
                }
            }
        })
    }

    private fun initializeDownload(mOtp: String?, mPlaybackInfo: String?, videoId: String) {
        val optionsDownloader = OptionsDownloader()
        // assuming we have otp and playbackInfo
        optionsDownloader.downloadOptionsWithOtp(mOtp, mPlaybackInfo, object : OptionsDownloader.Callback {
            override fun onOptionsReceived(options: DownloadOptions) {
                // we have received the available download options
                options.availableTracks.forEach {
                    info { it }
                }
                val selectionIndices = intArrayOf(0, 1)
                val downloadSelections = DownloadSelections(options, selectionIndices)
                val file = context?.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                val folderFile = File(file, "/$videoId")
                if (!folderFile.exists()) {
                    folderFile.mkdir()
                }
                val request = DownloadRequest.Builder(downloadSelections, folderFile.absolutePath).build()
                val vdoDownloadManager = VdoDownloadManager.getInstance(context)
                // enqueue request to VdoDownloadManager for download
                try {
                    vdoDownloadManager.enqueue(request)
                    vdoDownloadManager.addEventListener(this@CourseContentFragment)
                } catch (e: IllegalArgumentException) {
                } catch (e: IllegalStateException) {
                }
            }

            override fun onOptionsNotReceived(errDesc: ErrorDescription) {
                // there was an error downloading the available options
                val errMsg = "onOptionsNotReceived : $errDesc"
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_content, container, false)
        view.swiperefresh.setOnRefreshListener {
            try {
                (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
            } catch (cce: ClassCastException) {
            }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val sectionDao = database.sectionDao()
        val sectionsList = ArrayList<CourseSection>()
        val sectionAdapter = SectionDetailsAdapter(sectionsList, activity!!, this)
        view.rvExpendableView.layoutManager = LinearLayoutManager(context)
        view.rvExpendableView.adapter = sectionAdapter
        view.sectionProgressBar.show()
        sectionDao.getCourseSection(attemptId).observe(this, Observer<List<CourseSection>> {
            if (it.isNotEmpty()) {
                view.sectionProgressBar.hide()
            }
            courseDao.getRunByAtemptId(attemptId).observe(this, Observer<CourseRun> { courseRun ->
                sectionAdapter.setData(it as ArrayList<CourseSection>, courseRun.premium, courseRun.crStart)
            })
            if (view.swiperefresh.isRefreshing) {
                view.swiperefresh.isRefreshing = false
            }
        })

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseContent")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
