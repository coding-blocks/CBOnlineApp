package com.codingblocks.cbonlineapp.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.PdfActivity
import com.codingblocks.cbonlineapp.activities.QuizActivity
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.Components.showconfirmation
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.DownloadStarter
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import kotlinx.android.synthetic.main.item_content.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.textColor

class ContentViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)) {

    var starterListener: DownloadStarter? = null
    lateinit var contentModel: ContentModel

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(content: ContentModel) {
        this.contentModel = content
        with(itemView) {
            if (content.progress == "DONE") {
                title.textColor = resources.getColor(R.color.green)
                downloadBtn.setImageDrawable(context.getDrawable(R.drawable.ic_status_done))
            } else {
                title.textColor = resources.getColor(R.color.black)
            }
            title.text = content.title

            when (content.contentable) {
                LECTURE -> {
                    contentType.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                    downloadBtn.setOnClickListener {
                        checkDownloadStatus()
                    }
                }
                DOCUMENT -> {
                    contentType.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                }

                VIDEO -> {
                    contentType.setImageDrawable(context.getDrawable(R.drawable.ic_youtube_video))
                }

                QNA -> {
                    contentType.setImageDrawable(context.getDrawable(R.drawable.ic_quiz))
                }

                CODE -> {
                    contentType.setImageDrawable(context.getDrawable(R.drawable.ic_code))
                }
            }

            setOnClickListener {
                if (content.contentable != CODE) {
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
                                DOWNLOADED to content.contentLecture.isDownloaded
                            ).singleTop()
                        )
                    }
                    starterListener?.updateProgress(content.ccid, content.progressId)
                } else {
                    showconfirmation(it.context, "unavailable")
                }
            }
        }
    }

    private fun checkDownloadStatus() {
        if (MediaUtils.checkPermission(itemView.context)) {
            starterListener?.startDownload(
                contentModel.contentLecture.lectureId,
                contentModel.ccid,
                contentModel.title,
                contentModel.attempt_id
            )
        }
    }
}
