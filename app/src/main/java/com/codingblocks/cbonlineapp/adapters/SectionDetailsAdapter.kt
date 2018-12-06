package com.codingblocks.cbonlineapp.adapters

import android.content.Context
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.PdfActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.VideoPlayerActivity
import com.codingblocks.cbonlineapp.YoutubePlayerActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseContent
import com.codingblocks.cbonlineapp.database.CourseSection
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.item_section.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.io.*

class SectionDetailsAdapter(private var sectionData: ArrayList<CourseSection>?, private var activity: LifecycleOwner) : RecyclerView.Adapter<SectionDetailsAdapter.CourseViewHolder>(), AnkoLogger {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao


    fun setData(sectionData: ArrayList<CourseSection>) {
        this.sectionData = sectionData
        info { sectionData.size }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(sectionData!![position])
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

        fun bindView(data: CourseSection) {

            itemView.title.text = data.name

            contentDao.getCourseSectionContents(data.attempt_id, data.id).observe(activity, Observer<List<CourseContent>> { it ->
                val ll = itemView.findViewById<LinearLayout>(R.id.sectionContents)
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
                    info { "hour$hour   minute$minute" }

                    if (minute >= 1 && hour == 0L)
                        itemView.lectureTime.text = ("$minute Min")
                    else if (hour >= 1) {
                        itemView.lectureTime.text = ("$hour Hours")
                    } else
                        itemView.lectureTime.text = ("---")

                    val factory = LayoutInflater.from(context)
                    val inflatedView = factory.inflate(R.layout.item_section_content_info, ll, false)
                    val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
                    val subDuration = inflatedView.findViewById(R.id.textView16) as TextView
                    val contentImg = inflatedView.findViewById(R.id.imageView3) as ImageView
                    subTitle.text = content.title
                    subDuration.text = content.progress
                    when {
                        content.contentable == "lecture" -> {
                            contentImg.setImageDrawable(context.getDrawable(R.drawable.video_green_dark))
                            val url = content.contentLecture.lectureUrl.substring(38, (content.contentLecture.lectureUrl.length - 11))
                            contentImg.setOnClickListener { view ->
                                view.context.startActivity(view.context.intentFor<VideoPlayerActivity>("FOLDER_NAME" to url).singleTop())
                            }
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                // download lecture index.m3u8,video.key and video.m3u8
                                //TODO : Error handling
                                //No need to nest every call within one another, we can start the larger downloads sequentially once the smaller
                                //downloads (m3u8 and key) have been completed
                                Clients.initiateDowload(url, "index.m3u8").enqueue(retrofitCallback { _, response ->
                                    response?.body()?.let { indexResponse ->
                                        writeResponseBodyToDisk(indexResponse, url, "index.m3u8")
                                    }
                                })

                                Clients.initiateDowload(url, "video.m3u8").enqueue(retrofitCallback { throwable, response ->
                                    response?.body()?.let { videoResponse ->
                                        writeResponseBodyToDisk(videoResponse, url, "video.m3u8")
                                    }
                                })

                                Clients.initiateDowload(url, "video.key").enqueue(retrofitCallback { throwable, response ->
                                    response?.body()?.let { keyResponse ->
                                        writeResponseBodyToDisk(keyResponse, url, "video.key")
                                        val videoChunks = MediaUtils.getCourseDownloadUrls(url, context)
                                        videoChunks.forEach { videoName: String ->
                                            Clients.initiateDowload(url, videoName).enqueue(retrofitCallback { throwable, response ->
                                                writeResponseBodyToDisk(response?.body()!!, url, videoName)
                                            })
                                        }
                                    }
                                })
                            }
                        }
                        content.contentable == "document" -> {
                            contentImg.setImageDrawable(context.getDrawable(R.drawable.file_green_dark))
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                it.context.startActivity(it.context.intentFor<PdfActivity>("fileUrl" to content.contentDocument.documentPdfLink, "fileName" to content.contentDocument.documentName + ".pdf").singleTop())

                            }
                        }
                        content.contentable == "video" -> {
                            contentImg.setImageDrawable(context.getDrawable(R.drawable.code_green_dark))
                            ll.addView(inflatedView)
                            inflatedView.setOnClickListener {
                                it.context.startActivity(it.context.intentFor<YoutubePlayerActivity>("videoUrl" to content.contentVideo.videoUrl).singleTop())

                            }
                        }
                    }
                    var arrowAnimation: RotateAnimation

                    itemView.arrow.setOnClickListener {
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
            })
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, videoUrl: String?, fileName: String): Boolean {
        try {

            val file = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
            val folderFile = File(file, "/$videoUrl")
            val dataFile = File(file, "/$videoUrl/$fileName")
            if (!folderFile.exists()) {
                folderFile.mkdir()
            }
            // todo change the file location/name according to your needs

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(dataFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()
                    info { "file download: $fileSizeDownloaded of $fileSize" }
                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }

                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            return false
        }

    }
}