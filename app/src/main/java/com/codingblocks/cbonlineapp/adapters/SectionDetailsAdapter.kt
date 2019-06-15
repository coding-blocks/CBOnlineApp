package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.PdfActivity
import com.codingblocks.cbonlineapp.activities.QuizActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.database.models.CourseSection
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.extensions.getDurationBreakdown
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.DownloadStarter
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.NetworkUtils
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.OnCleanDialogListener
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.Animations.collapse
import com.codingblocks.cbonlineapp.util.Animations.expand
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.RunAttemptsId
import kotlinx.android.synthetic.main.item_section.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noButton
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.textColor
import org.jetbrains.anko.yesButton
import java.io.File
import kotlin.concurrent.thread

class SectionDetailsAdapter(
    private var sectionData: ArrayList<CourseSection>?,
    private var activity: LifecycleOwner,
    private var starter: DownloadStarter,
    private var viewModel: MyCourseViewModel
) : RecyclerView.Adapter<SectionDetailsAdapter.CourseViewHolder>() {

    private lateinit var context: Context
    private var premium: Boolean = false
    private lateinit var courseStartDate: String

    private lateinit var arrowAnimation: RotateAnimation

    fun setData(sectionData: ArrayList<CourseSection>, premium: Boolean, crStart: String) {
        this.sectionData = sectionData
        this.premium = premium
        this.courseStartDate = crStart
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        sectionData?.get(position)?.let { holder.bindView(it, starter) }
    }

    override fun getItemCount(): Int {
        return sectionData?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        context = parent.context

        return CourseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section, parent, false)
        )
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: CourseSection, starter: DownloadStarter) {
            itemView.title.text = data.name
            viewModel.getContentWithSectionId(data.id).observer(activity) { courseContent ->
                val ll = itemView.findViewById<LinearLayout>(R.id.sectionContents)
                if (ll.visibility == View.VISIBLE) {
                    ll.removeAllViews()
                    expand(ll)
                } else {
                    ll.removeAllViews()
                    ll.visibility = View.GONE
                }
                itemView.lectures.text = "0/${courseContent.size} Lectures Completed"
                var duration: Long = 0
                var sectionComplete = 0
                for (content in courseContent) {

                    val factory = LayoutInflater.from(context)
                    val inflatedView =
                        factory.inflate(R.layout.item_section_detailed_info, ll, false)
                    val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
                    val downloadBtn = inflatedView.findViewById(R.id.downloadBtn) as ImageView
                    val contentType = inflatedView.findViewById(R.id.contentType) as ImageView

                    if (content.progress == "DONE") {
                        subTitle.textColor = context.resources.getColor(R.color.green)
                        downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
                        sectionComplete++
                    }
                    if (content.contentable == "lecture")
                        duration += content.contentLecture.lectureDuration
                    else if (content.contentable == "video") {
                        duration += content.contentVideo.videoDuration
                    }
                    itemView.lectureTime.text = duration.getDurationBreakdown()

                    if (!data.premium)
                        itemView.free.visibility = View.VISIBLE

                    subTitle.text = content.title

                    if (!data.premium || premium && ((courseStartDate.toLong() * 1000) < System.currentTimeMillis())) {
                        if (sectionComplete == courseContent.size) {
                            itemView.lectures.text =
                                "$sectionComplete/${courseContent.size} Lectures Completed"
                            itemView.lectures.textColor =
                                context.resources.getColor(R.color.green)
                        } else {
                            itemView.lectures.text =
                                "$sectionComplete/${courseContent.size} Lectures Completed"
                            itemView.lectures.textColor =
                                context.resources.getColor(R.color.black)
                        }
                        when {
                            content.contentable == "lecture" -> {
                                contentType.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                                if (content.contentLecture.lectureUid.isNotEmpty()) {
                                    ll.addView(inflatedView)
                                    if (content.contentLecture.isDownloaded == "false") {
                                        downloadBtn.setImageDrawable(null)
                                        downloadBtn.background =
                                            context.getDrawable(android.R.drawable.stat_sys_download)
                                        inflatedView.setOnClickListener {
                                            if (content.progress == "UNDONE") {
                                                if (content.progressId.isEmpty())
                                                    setProgress(
                                                        content.id,
                                                        content.attempt_id,
                                                        content.contentable,
                                                        data.id,
                                                        content.contentLecture.lectureContentId
                                                    )
                                                else
                                                    updateProgress(
                                                        content.id,
                                                        content.attempt_id,
                                                        content.progressId,
                                                        "DONE",
                                                        content.contentable,
                                                        data.id,
                                                        content.contentLecture.lectureContentId
                                                    )
                                            }
                                            it.context.startActivity(
                                                it.context.intentFor<VideoPlayerActivity>(
                                                    VIDEO_ID to content.contentLecture.lectureId,
                                                    RUN_ATTEMPT_ID to content.attempt_id,
                                                    CONTENT_ID to content.id,
                                                    SECTION_ID to content.section_id,
                                                    DOWNLOADED to false
                                                ).singleTop()
                                            )
                                        }
                                        downloadBtn.setOnClickListener {
                                            if (MediaUtils.checkPermission(context)) {
                                                if ((context as Activity).getPrefs().SP_WIFI) {
                                                    if (NetworkUtils.connectedToWifi(context) == true) {
                                                        startFileDownload(
                                                            content.contentLecture.lectureId,
                                                            data.id,
                                                            content.contentLecture.lectureContentId,
                                                            content.title,
                                                            content.attempt_id,
                                                            content.id,
                                                            content.section_id,
                                                            downloadBtn
                                                        )
                                                    } else {
                                                        Components.showconfirmation(
                                                            context,
                                                            "wifi"
                                                        )
                                                    }
                                                } else {
                                                    startFileDownload(
                                                        content.contentLecture.lectureId,
                                                        data.id,
                                                        content.contentLecture.lectureContentId,
                                                        content.title,
                                                        content.attempt_id,
                                                        content.id,
                                                        content.section_id,
                                                        downloadBtn
                                                    )
                                                }
                                            } else {
                                                MediaUtils.isStoragePermissionGranted(context)
                                            }
                                        }
                                    } else {
                                        downloadBtn.setOnClickListener {

                                            (context as Activity).alert("This lecture will be deleted !!!") {
                                                yesButton {
                                                    val file =
                                                        context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                                                    val folderFile = File(
                                                        file,
                                                        "/${content.contentLecture.lectureId}"
                                                    )
                                                    MediaUtils.deleteRecursive(folderFile)
                                                    viewModel.updateContent(
                                                        data.id,
                                                        content.contentLecture.lectureContentId,
                                                        "false"
                                                    )
                                                }
                                                noButton { it.dismiss() }
                                            }.show()
                                        }
                                        inflatedView.setOnClickListener {
                                            if (content.progress == "UNDONE") {
                                                if (content.progressId.isEmpty())
                                                    setProgress(
                                                        content.id,
                                                        content.attempt_id,
                                                        content.contentable,
                                                        data.id,
                                                        content.contentLecture.lectureContentId
                                                    )
                                                else
                                                    updateProgress(
                                                        content.id,
                                                        content.attempt_id,
                                                        content.progressId,
                                                        "DONE",
                                                        content.contentable,
                                                        data.id,
                                                        content.contentLecture.lectureContentId
                                                    )
                                            }
                                            it.context.startActivity(
                                                it.context.intentFor<VideoPlayerActivity>(
                                                    VIDEO_ID to content.contentLecture.lectureId,
                                                    RUN_ATTEMPT_ID to content.attempt_id,
                                                    CONTENT_ID to content.id,
                                                    SECTION_ID to data.id,
                                                    DOWNLOADED to true
                                                ).singleTop()
                                            )
                                        }
                                    }
                                }
                            }
                            content.contentable == "document" -> {
                                contentType.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                                ll.addView(inflatedView)
                                if (content.contentDocument.documentContentId.isNotEmpty() && content.contentDocument.documentPdfLink.isNotEmpty()) {
                                    inflatedView.setOnClickListener {
                                        if (content.progress == "UNDONE") {
                                            if (content.progressId.isEmpty())
                                                setProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.contentable,
                                                    data.id,
                                                    content.contentDocument.documentContentId
                                                )
                                            else
                                                updateProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.progressId,
                                                    "DONE",
                                                    content.contentable,
                                                    data.id,
                                                    content.contentDocument.documentContentId
                                                )
                                        }
                                        it.context.startActivity(
                                            it.context.intentFor<PdfActivity>(
                                                "fileUrl" to content.contentDocument.documentPdfLink,
                                                "fileName" to content.contentDocument.documentName + ".pdf"
                                            ).singleTop()
                                        )
                                    }
                                }
                            }
                            content.contentable == "video" -> {
                                contentType.setImageDrawable(context.getDrawable(R.drawable.ic_youtube_video))
                                ll.addView(inflatedView)
                                if (content.contentVideo.videoContentId.isNotEmpty() && content.contentVideo.videoUrl.isNotEmpty()) {
                                    inflatedView.setOnClickListener {
                                        if (content.progress == "UNDONE") {
                                            if (content.progressId.isEmpty())
                                                setProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.contentable,
                                                    data.id,
                                                    content.contentVideo.videoContentId
                                                )
                                            else
                                                updateProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.progressId,
                                                    "DONE",
                                                    content.contentable,
                                                    data.id,
                                                    content.contentVideo.videoContentId
                                                )
                                        }
                                        it.context.startActivity(
                                            it.context.intentFor<VideoPlayerActivity>(
                                                "videoUrl" to content.contentVideo.videoUrl,
                                                RUN_ATTEMPT_ID to content.attempt_id,
                                                CONTENT_ID to content.id
                                            ).singleTop()
                                        )
                                    }
                                }
                            }
                            content.contentable == "qna" -> {
                                contentType.setImageDrawable(context.getDrawable(R.drawable.ic_quiz))
                                ll.addView(inflatedView)
                                if (content.contentQna.qnaContentId.isNotEmpty() && content.contentQna.qnaQid.toString().isNotEmpty()) {
                                    inflatedView.setOnClickListener {
                                        if (content.progress == "UNDONE") {
                                            if (content.progressId.isEmpty())
                                                setProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.contentable,
                                                    data.id,
                                                    content.contentQna.qnaContentId
                                                )
                                            else
                                                updateProgress(
                                                    content.id,
                                                    content.attempt_id,
                                                    content.progressId,
                                                    "DONE",
                                                    content.contentable,
                                                    data.id,
                                                    content.contentQna.qnaContentId
                                                )
                                        }
                                        it.context.startActivity(
                                            it.context.intentFor<QuizActivity>(
                                                QUIZ_QNA to content.contentQna.qnaUid,
                                                RUN_ATTEMPT_ID to content.attempt_id,
                                                QUIZ_ID to content.contentQna.qnaQid.toString()
                                            ).singleTop()
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        contentType.visibility = View.GONE
                        downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_lock_outline_black_24dp))
                        ll.addView(inflatedView)
                    }

                    itemView.setOnClickListener {
                        if (itemView.title.text.contains("Challenges", true))
                            Components.showconfirmation(it.context, "unavailable")
                        else
                            showOrHide(ll, it)
                    }

                    itemView.arrow.setOnClickListener {
                        if (itemView.title.text.contains("Challenges", true))
                            Components.showconfirmation(it.context, "unavailable")
                        else
                            showOrHide(ll, it)
                    }
                }
            }
        }

        private fun startFileDownload(lectureId: String, dataId: String, lectureContentId: String, title: String, attempt_id: String, content_id: String, section_id: String, downloadBtn: ImageView) {
            if (FileUtils.checkIfCannotDownload(context)) {
                FileUtils.showIfCleanDialog(context, object : OnCleanDialogListener {
                    override fun onComplete() {
                        startDownload(
                            lectureId,
                            dataId,
                            lectureContentId,
                            title,
                            attempt_id,
                            content_id,
                            section_id,
                            downloadBtn
                        )
                    }
                })
            } else {
                startDownload(
                    lectureId,
                    dataId,
                    lectureContentId,
                    title,
                    attempt_id,
                    content_id,
                    section_id,
                    downloadBtn
                )
            }
        }

        private fun startDownload(
            videoId: String,
            id: String,
            lectureContentId: String,
            title: String,
            attemptId: String,
            contentId: String,
            sectionId: String,
            downloadBtn: ImageView
        ) {
            starter.startDownload(
                videoId,
                id,
                lectureContentId,
                title,
                attemptId,
                contentId,
                sectionId
            )
            downloadBtn.isEnabled = false
            (downloadBtn.background as AnimationDrawable).start()
        }
    }

    fun showOrHide(ll: View, itemView: View) {
        if (ll.visibility == View.GONE) {
            expand(ll)
            arrowAnimation = RotateAnimation(
                0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f
            )
            arrowAnimation.fillAfter = true
            arrowAnimation.duration = 200
            itemView.arrow.startAnimation(arrowAnimation)
        } else {
            collapse(ll)
            arrowAnimation = RotateAnimation(
                180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f
            )
            arrowAnimation.fillAfter = true
            arrowAnimation.duration = 200
            itemView.arrow.startAnimation(arrowAnimation)
        }
    }

    fun setProgress(
        id: String,
        attempt_id: String,
        contentable: String,
        sectionId: String,
        contentId: String
    ) {
        doAsync {
            val p = Progress()
            p.status = "DONE"
            p.runs = RunAttemptsId(attempt_id)
            p.content = ContentsId(id)
            Clients.onlineV2JsonApi.setProgress(p).enqueue(retrofitCallback { _, response ->

                response?.body().let {
                    val progressId = it?.id
                    when (contentable) {
                        "lecture" -> thread {
                            viewModel.updateProgressLecture(
                                sectionId, contentId, "DONE", progressId
                                ?: ""
                            )
                        }

                        "document" ->
                            thread {
                                viewModel.updateProgressDocument(
                                    sectionId, contentId, "DONE", progressId
                                    ?: ""
                                )
                            }
                        "video" ->
                            thread {
                                viewModel.updateProgressVideo(
                                    sectionId, contentId, "DONE", progressId
                                    ?: ""
                                )
                            }
                        "qna" ->
                            thread {
                                viewModel.updateProgressQna(
                                    sectionId, contentId, "DONE", progressId
                                    ?: ""
                                )
                            }
                        else -> {
                        }
                    }
                }
            })
        }
    }

    private fun updateProgress(
        id: String,
        attempt_id: String,
        progressId: String,
        status: String,
        contentable: String,
        sectionId: String,
        contentId: String
    ) {
        doAsync {
            val p = Progress()
            p.id = progressId
            p.status = status
            p.runs = RunAttemptsId(attempt_id)
            p.content = ContentsId(id)
            Clients.onlineV2JsonApi.updateProgress(progressId, p)
                .enqueue(retrofitCallback { _, response ->
                    if (response != null) {
                        if (response.isSuccessful) {
                            when (contentable) {
                                "lecture" -> thread {
                                    viewModel.updateProgressLecture(
                                        sectionId,
                                        contentId,
                                        status,
                                        progressId
                                    )
                                }

                                "document" ->
                                    thread {
                                        viewModel.updateProgressDocument(
                                            sectionId,
                                            contentId,
                                            status,
                                            progressId
                                        )
                                    }
                                "video" ->
                                    thread {
                                        viewModel.updateProgressVideo(
                                            sectionId,
                                            contentId,
                                            status,
                                            progressId
                                        )
                                    }
                                "qna" ->
                                    thread {
                                        viewModel.updateProgressQna(
                                            sectionId,
                                            contentId,
                                            status,
                                            progressId
                                        )
                                    }
                            }
                        }
                    }
                })
        }
    }
}
