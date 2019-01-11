package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.DownloadStarter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.activities.PdfActivity
import com.codingblocks.cbonlineapp.activities.QuizActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.activities.YoutubePlayerActivity
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.MyRunAttempts
import com.codingblocks.onlineapi.models.Progress
import kotlinx.android.synthetic.main.item_section.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import kotlin.concurrent.thread


class SectionDetailsAdapter(private var sectionData: ArrayList<CourseSection>?,
                            private var activity: LifecycleOwner,
                            private var starter: DownloadStarter
) : RecyclerView.Adapter<SectionDetailsAdapter.CourseViewHolder>(), AnkoLogger {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao
    private lateinit var sectionWithContentDao: SectionWithContentsDao

    fun setData(sectionData: ArrayList<CourseSection>) {
        this.sectionData = sectionData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(sectionData!![position], starter)
    }


    override fun getItemCount(): Int {

        return sectionData!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()
        sectionWithContentDao = database.sectionWithContentsDao()


        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var starter: DownloadStarter? = null


        fun bindView(data: CourseSection, starter: DownloadStarter) {

            itemView.title.text = data.name
            this.starter = starter

            sectionWithContentDao.getContentWithSectionId(data.id).observe(activity, Observer<List<CourseContent>> { it ->
                val ll = itemView.findViewById<LinearLayout>(R.id.sectionContents)
                if (ll.childCount != 0)
                    showOrHide(ll, itemView)
                ll.removeAllViews()
                ll.orientation = LinearLayout.VERTICAL
                ll.visibility = View.GONE
                itemView.lectures.text = "${it.size} Lectures"
                var duration: Long = 0
                for (content in it) {
                    if (content.contentable == "lecture")
                        duration += content.contentLecture.lectureDuration
                    else if (content.contentable == "video") {
                        duration += content.contentVideo.videoDuration
                    }
                    val hour = duration / (1000 * 60 * 60) % 24
                    val minute = duration / (1000 * 60) % 60

                    if (minute >= 1 && hour == 0L)
                        itemView.lectureTime.text = ("$minute Min")
                    else if (hour >= 1) {
                        itemView.lectureTime.text = ("$hour Hours")
                    } else
                        itemView.lectureTime.text = ("---")

                    val factory = LayoutInflater.from(context)
                    val inflatedView = factory.inflate(R.layout.item_section_detailed_info, ll, false)
                    val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
                    val downloadBtn = inflatedView.findViewById(R.id.downloadBtn) as ImageView

                    subTitle.text = content.title
                    when {
                        content.contentable == "lecture" -> {
                            val url = content.contentLecture.lectureUrl.substring(38, (content.contentLecture.lectureUrl.length - 11))
                            ll.addView(inflatedView)
                            if (content.contentLecture.isDownloaded == "false") {
                                inflatedView.setOnClickListener {
                                    if (MediaUtils.checkPermission(context)) {
                                        starter.startDownload(url, data.id, content.contentLecture.lectureContentId, content.title)
                                        downloadBtn.isEnabled = false
                                        (downloadBtn.background as AnimationDrawable).start()
                                    } else {
                                        MediaUtils.isStoragePermissionGranted(context)
                                    }
                                }
                            } else {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                                downloadBtn.background = null
                                if (content.progress == "DONE") {
                                    downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
//                                    downloadBtn.setOnClickListener {
//                                        updateProgress(content.id, content.attempt_id, content.progressId, "UNDONE", content.contentable, data.id, content.contentLecture.lectureContentId)
//                                    }
                                }
                                inflatedView.setOnClickListener {
                                    if (content.progress == "UNDONE") {
                                        if (content.progressId.isEmpty())
                                            setProgress(content.id, content.attempt_id, content.contentable, data.id, content.contentLecture.lectureContentId)
                                        else
                                            updateProgress(content.id, content.attempt_id, content.progressId, "DONE", content.contentable, data.id, content.contentLecture.lectureContentId)
                                    }
                                    it.context.startActivity(it.context.intentFor<VideoPlayerActivity>("FOLDER_NAME" to url).singleTop())
                                }
                            }

                        }
                        content.contentable == "document" -> {
                            downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                            downloadBtn.background = null
                            if (content.progress == "DONE") {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
//                                downloadBtn.setOnClickListener {
//                                    updateProgress(content.id, content.attempt_id, content.progressId, "UNDONE", content.contentable, data.id, content.contentDocument.documentContentId)
//                                }
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                if (content.progress == "UNDONE") {
                                    if (content.progressId.isEmpty())
                                        setProgress(content.id, content.attempt_id, content.contentable, data.id, content.contentDocument.documentContentId)
                                    else
                                        updateProgress(content.id, content.attempt_id, content.progressId, "DONE", content.contentable, data.id, content.contentDocument.documentContentId)
                                }
                                it.context.startActivity(it.context.intentFor<PdfActivity>("fileUrl" to content.contentDocument.documentPdfLink, "fileName" to content.contentDocument.documentName + ".pdf").singleTop())

                            }
                        }
                        content.contentable == "video" -> {
                            downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_youtube_video))
                            downloadBtn.background = null
                            if (content.progress == "DONE") {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
//                                downloadBtn.setOnClickListener {
//                                    updateProgress(content.id, content.attempt_id, content.progressId, "UNDONE", content.contentable, data.id, content.contentVideo.videoContentId)
//                                }
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                info { "resp" + content.progress + content.progressId }
                                if (content.progress == "UNDONE") {
                                    if (content.progressId.isEmpty())
                                        setProgress(content.id, content.attempt_id, content.contentable, data.id, content.contentVideo.videoContentId)
                                    else
                                        updateProgress(content.id, content.attempt_id, content.progressId, "DONE", content.contentable, data.id, content.contentVideo.videoContentId)
                                }
                                it.context.startActivity(it.context.intentFor<YoutubePlayerActivity>("videoUrl" to content.contentVideo.videoUrl).singleTop())

                            }
                        }
                        content.contentable == "qna" -> {
                            downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_quiz))
                            downloadBtn.background = null
                            if (content.progress == "DONE") {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
                                downloadBtn.setOnClickListener {
                                    updateProgress(content.id, content.attempt_id, content.progressId, "UNDONE", content.contentable, data.id, content.contentLecture.lectureContentId)
                                }
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                if (content.progress == "UNDONE") {
                                    if (content.progressId.isEmpty())
                                        setProgress(content.id, content.attempt_id, content.contentable, data.id, content.contentQna.qnaContentId)
                                    else
                                        updateProgress(content.id, content.attempt_id, content.progressId, "DONE", content.contentable, data.id, content.contentLecture.lectureContentId)
                                }
                                it.context.startActivity(it.context.intentFor<QuizActivity>("quizId" to content.contentQna.qnaQid.toString(), "attemptId" to content.attempt_id).singleTop())

                            }
                        }

                    }

                    itemView.setOnClickListener {
                        showOrHide(ll, it)
                    }

//                    itemView.arrow.setOnClickListener {
//                        showOrHide(ll, itemView)
//                    }
                }
            })
        }
    }


    fun showOrHide(ll: View, itemView: View) {
        if (ll.visibility == View.GONE) {
            ll.visibility = View.VISIBLE
//            arrowAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                    0.5f)
//            arrowAnimation.fillAfter = true
//            arrowAnimation.duration = 350
//            itemView.arrow.startAnimation(arrowAnimation)
        } else {
            ll.visibility = View.GONE
//            arrowAnimation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                    0.5f)
//            arrowAnimation.fillAfter = true
//            arrowAnimation.duration = 350
//            itemView.arrow.startAnimation(arrowAnimation)
        }
    }

    fun setProgress(id: String, attempt_id: String, contentable: String, sectionId: String, contentId: String) {
        val p = Progress()
        val runAttempts = MyRunAttempts()
        val contents = Contents()
        runAttempts.id = attempt_id
        contents.id = id
        p.status = "DONE"
        p.runs = runAttempts
        p.content = contents
        Clients.onlineV2JsonApi.setProgress(p).enqueue(retrofitCallback { throwable, response ->
            info { "resp" + response?.code() }

            if (response?.isSuccessful!!) {

                val progressId = response.body()?.id
                when (contentable) {
                    "lecture" -> thread {
                        contentDao.updateProgressLecture(sectionId, contentId, "DONE", progressId
                                ?: "")
                    }

                    "document" ->
                        thread {
                            contentDao.updateProgressDocuemnt(sectionId, contentId, "DONE", progressId
                                    ?: "")
                        }
                    "video" ->
                        thread {
                            contentDao.updateProgressVideo(sectionId, contentId, "DONE", progressId
                                    ?: "")
                        }
                    "qna" ->
                        thread {
                            contentDao.updateProgressQna(sectionId, contentId, "DONE", progressId
                                    ?: "")
                        }
                }

            }
        })
    }

    private fun updateProgress(id: String, attempt_id: String, progressId: String, status: String, contentable: String, sectionId: String, contentId: String) {
        val p = Progress()
        val runAttempts = MyRunAttempts()
        val contents = Contents()
        runAttempts.id = attempt_id
        contents.id = id
        p.id = progressId
        p.status = status
        p.runs = runAttempts
        p.content = contents
        Clients.onlineV2JsonApi.updateProgress(progressId, p).enqueue(retrofitCallback { throwable, response ->
            if (response != null) {
                if (response.isSuccessful) {
                    when (contentable) {
                        "lecture" -> thread {
                            contentDao.updateProgressLecture(sectionId, contentId, status, progressId)
                        }

                        "document" ->
                            thread {
                                contentDao.updateProgressDocuemnt(sectionId, contentId, status, progressId)
                            }
                        "video" ->
                            thread {
                                contentDao.updateProgressVideo(sectionId, contentId, status, progressId)
                            }
                        "qna" ->
                            thread {
                                contentDao.updateProgressQna(sectionId, contentId, status, progressId)
                            }
                    }


                }
            }
        })
    }
}