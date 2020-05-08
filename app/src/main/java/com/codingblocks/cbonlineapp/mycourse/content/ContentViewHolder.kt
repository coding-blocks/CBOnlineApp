package com.codingblocks.cbonlineapp.mycourse.content

import android.app.Activity
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.OnCleanDialogListener
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.VIDEO
import java.io.File
import kotlinx.android.synthetic.main.item_content.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class ContentViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)) {

    var starterListener: DownloadStarter? = null
    lateinit var contentModel: ContentModel

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(content: ContentModel, onItemClick: ((ContentModel) -> Unit)?) {
        this.contentModel = content
        with(itemView) {

            title.text = content.title
            if (content.progress == "DONE") {
                contentType.isActivated = true
                title.textColor = getColor(context, R.color.freshGreen)
            } else {
                contentType.isActivated = false
                title.textColor = getColor(context, R.color.black)
            }
            setOnClickListener {
                onItemClick?.invoke(contentModel)
            }
            downloadBtn.isVisible = false
            when (content.contentable) {
                DOCUMENT -> {
                    contentType.setImageResource(R.drawable.ic_doc)
                }

                VIDEO -> {
                    contentType.setImageResource(R.drawable.ic_video)
                }

                QNA -> {
                    contentType.setImageResource(R.drawable.ic_quiz)
                }

                CODE -> {
                    contentType.setImageResource(R.drawable.ic_code)
                }
                LECTURE -> {
                    downloadBtn.isVisible = true
                    contentType.setImageResource(R.drawable.ic_video)
                    val id = content.contentLecture.lectureUid.isEmpty()
                    val downloadStatus = if (id) false else content.contentLecture.isDownloaded
                    downloadBtn.setImageResource(R.drawable.download_states_content)
                    downloadBtn.isActivated = downloadStatus
                    downloadBtn.setOnClickListener {
                        if (!downloadStatus) {
                            if (!id)
                                checkDownloadStatus(downloadBtn)
                            else
                                context.toast("Cannot Download")
                        }
                    }
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
            starterListener?.startDownload(
                contentModel.contentLecture.lectureId,
                contentModel.ccid,
                contentModel.title,
                contentModel.attempt_id,
                contentModel.sectionId
            )
//            (downloadBtn.background as AnimationDrawable).start()
        }
    }
}
