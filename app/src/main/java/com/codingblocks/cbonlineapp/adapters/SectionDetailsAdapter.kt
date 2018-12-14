package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.DownloadStarter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.PdfActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.activities.YoutubePlayerActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseContent
import com.codingblocks.cbonlineapp.database.CourseSection
import com.codingblocks.cbonlineapp.utils.MediaUtils
import kotlinx.android.synthetic.main.item_section.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import kotlin.concurrent.thread


class SectionDetailsAdapter(private var sectionData: ArrayList<CourseSection>?, private var activity: LifecycleOwner, private var starter: DownloadStarter) : RecyclerView.Adapter<SectionDetailsAdapter.CourseViewHolder>() {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao
    lateinit var arrowAnimation: RotateAnimation


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


        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var starter: DownloadStarter? = null


        fun bindView(data: CourseSection, starter: DownloadStarter) {

            itemView.title.text = data.name
            this.starter = starter

            contentDao.getCourseSectionContents(data.attempt_id, data.id).observe(activity, Observer<List<CourseContent>> { it ->
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
                            if (!content.contentLecture.isDownloaded) {
                                downloadBtn.setOnClickListener {
                                    if(MediaUtils.checkPermission(context)) {
                                        starter.startDownload(url, data.id, content.contentLecture.lectureContentId,content.title)
                                        downloadBtn.isEnabled = false
                                        (downloadBtn.background as AnimationDrawable).start()
                                    }else{
                                        MediaUtils.isStoragePermissionGranted(context)
                                    }
                                }
                            } else {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                                downloadBtn.background = null
                                if (content.progress == "DONE") {
                                    downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
                                }
                                inflatedView.setOnClickListener {
                                    //TODO status to be updated on server as well
                                    if (content.progress == "UNDONE")
                                        thread { contentDao.updateProgressLecture(data.id, content.contentLecture.lectureContentId, "DONE") }
                                    it.context.startActivity(it.context.intentFor<VideoPlayerActivity>("FOLDER_NAME" to url).singleTop())
                                }
                            }

                        }
                        content.contentable == "document" -> {
                            downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                            downloadBtn.background = null
                            if (content.progress == "DONE") {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                if (content.progress == "UNDONE")
                                    thread { contentDao.updateProgressDocuemnt(data.id, content.contentDocument.documentContentId, "DONE") }
                                it.context.startActivity(it.context.intentFor<PdfActivity>("fileUrl" to content.contentDocument.documentPdfLink, "fileName" to content.contentDocument.documentName + ".pdf").singleTop())

                            }
                        }
                        content.contentable == "video" -> {
                            downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_youtube_video))
                            downloadBtn.background = null
                            if (content.progress == "DONE") {
                                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                if (content.progress == "UNDONE")
                                    thread { contentDao.updateProgressVideo(data.id, content.contentVideo.videoContentId, "DONE") }
                                it.context.startActivity(it.context.intentFor<YoutubePlayerActivity>("videoUrl" to content.contentVideo.videoUrl).singleTop())

                            }
                        }
                    }

                    itemView.setOnClickListener {
                        showOrHide(ll, it)
                    }

                    itemView.arrow.setOnClickListener {
                        showOrHide(ll, itemView)
                    }
                }
            })
        }
    }

    fun showOrHide(ll: View, itemView: View) {
        if (ll.visibility == View.GONE) {
            ll.visibility = View.VISIBLE
            arrowAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
            arrowAnimation.fillAfter = true
            arrowAnimation.duration = 350
            itemView.arrow.startAnimation(arrowAnimation)
        } else {
            ll.visibility = View.GONE
            arrowAnimation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
            arrowAnimation.fillAfter = true
            arrowAnimation.duration = 350
            itemView.arrow.startAnimation(arrowAnimation)
        }
    }
}