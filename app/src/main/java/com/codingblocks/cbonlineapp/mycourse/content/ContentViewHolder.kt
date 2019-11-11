package com.codingblocks.cbonlineapp.mycourse.content

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.PdfActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.quiz.QuizActivity
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.Components.showConfirmation
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.OnCleanDialogListener
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import kotlinx.android.synthetic.main.item_content.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noButton
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.textColor
import org.jetbrains.anko.yesButton
import java.io.File

class ContentViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)) {

    var starterListener: DownloadStarter? = null
    lateinit var contentModel: ContentModel

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(content: ContentModel, expired: Boolean) {
        this.contentModel = content
        with(itemView) {

            title.text = content.title

            if (content.progress == "DONE") {
                title.textColor = resources.getColor(R.color.green)
                downloadBtn.setImageResource(R.drawable.ic_status_done)
            } else {
                downloadBtn.setImageResource(0)
                title.textColor = resources.getColor(R.color.black)
            }
            downloadBtn.background = null
            downloadBtn.setOnClickListener(null)
            when (content.contentable) {
                DOCUMENT -> {
                    contentType.setImageResource(R.drawable.ic_document)
                }

                VIDEO -> {
                    contentType.setImageResource(R.drawable.ic_youtube_video)
                }

                QNA -> {
                    contentType.setImageResource(R.drawable.ic_quiz)
                }

                CODE -> {
                    contentType.setImageResource(R.drawable.ic_code)
                }
                LECTURE -> {
                    contentType.setImageResource(R.drawable.ic_lecture)
                    val downloadStatus = !FileUtils.checkDownloadFileExists(CBOnlineApp.mInstance, content.contentLecture.lectureId)
                    if (downloadStatus) {
                        downloadBtn.setImageDrawable(null)
                        downloadBtn.background =
                            itemView.context.getDrawable(android.R.drawable.stat_sys_download)
                    }
                    downloadBtn.setOnClickListener {
                        it as ImageView
                        if (downloadStatus) {
                            checkDownloadStatus(it)
                        } else {
                            deletFile()
                        }
                    }
                }
            }

            setOnClickListener {
                if (expired && content.premium) {
                    showConfirmation(it.context, "expired")
                } else if (content.contentable != CODE) {
                    when (content.contentable) {
                        DOCUMENT -> context.startActivity(
                            context.intentFor<PdfActivity>(
                                "fileUrl" to content.contentDocument.documentPdfLink,
                                "fileName" to content.contentDocument.documentName + ".pdf"
                            ).singleTop()
                        )
                        VIDEO -> context.startActivity(
                            context.intentFor<VideoPlayerActivity>(
                                "videoUrl" to content.contentVideo.videoUrl,
                                RUN_ATTEMPT_ID to content.attempt_id,
                                CONTENT_ID to content.ccid
                            ).singleTop()
                        )

                        QNA -> context.startActivity(
                            context.intentFor<QuizActivity>(
                                QUIZ_QNA to content.contentQna.qnaUid,
                                RUN_ATTEMPT_ID to content.attempt_id,
                                QUIZ_ID to content.contentQna.qnaQid.toString()
                            ).singleTop()
                        )

                        LECTURE -> context.startActivity(
                            context.intentFor<VideoPlayerActivity>(
                                VIDEO_ID to content.contentLecture.lectureId,
                                RUN_ATTEMPT_ID to content.attempt_id,
                                CONTENT_ID to content.ccid,
                                SECTION_ID to content.sectionId,
                                DOWNLOADED to (content.contentLecture.isDownloaded || FileUtils.checkDownloadFileExists(CBOnlineApp.mInstance, content.contentLecture.lectureId))
                            ).singleTop()
                        )
                    }
                    starterListener?.updateProgress(content.ccid, content.progressId)
                } else {
                    showConfirmation(it.context, "unavailable")
                }
            }
        }
    }

    private fun checkDownloadStatus(it: ImageView) {
        if (FileUtils.checkIfCannotDownload(itemView.context)) {
            FileUtils.showIfCleanDialog(itemView.context, object : OnCleanDialogListener {
                override fun onComplete() {
                    downloadFile(it)
                }
            })
        } else {
            downloadFile(it)
        }
    }

    fun deletFile() {
        (itemView.context as Activity).alert("This lecture will be deleted !!!") {
            yesButton {
                val file =
                    itemView.context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                val folderFile = File(
                    file,
                    "/${contentModel.contentLecture.lectureId}"

                )
                MediaUtils.deleteRecursive(folderFile)
            }
            noButton { it.dismiss() }
        }.show()
    }

    private fun downloadFile(downloadBtn: ImageView) {
        if (MediaUtils.checkPermission(itemView.context)) {
            downloadBtn.isEnabled = false
            starterListener?.startDownload(
                contentModel.contentLecture.lectureId,
                contentModel.ccid,
                contentModel.title,
                contentModel.attempt_id,
                contentModel.sectionId
            )
            (downloadBtn.background as AnimationDrawable).start()
        }
    }
}
