package com.codingblocks.cbonlineapp.mycourse.document

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.DownloadBroadcastReceiver
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import java.io.File
import kotlinx.android.synthetic.main.activity_pdf.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class PdfActivity : BaseCBActivity(), AnkoLogger {
    lateinit var pdfViewPager: PDFViewPager
    var url: String? = null
    var fileName: String? = null
    var path: String? = null
    var isDownloaded: Boolean = false
    lateinit var receiver: DownloadBroadcastReceiver
    lateinit var intentFilter: IntentFilter
    private val vm: PdfViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        if (savedInstanceState == null) {
            vm.contentId = intent.getStringExtra(CONTENT_ID)
            vm.sectionId = intent.getStringExtra(SECTION_ID)
            vm.attempId = intent.getStringExtra(RUN_ATTEMPT_ID)
        }

        url = intent.getStringExtra("fileUrl")
        fileName = intent.getStringExtra("fileName")
        setToolbar(toolbarPdfActivity, title = fileName?:"")

        if (url.isNullOrEmpty() || fileName.isNullOrEmpty()) {
            Toast.makeText(this, "Error fetching document", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (MediaUtils.checkPermission(this)) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
            var downloadedFile: File? = null
            val file = File("$path/$fileName")
            val fileDownloads = File(path)
            val filesExisting = fileDownloads.listFiles()
            if (filesExisting != null) {
                for (file1 in filesExisting) {
                    if (file1.name == file.name) {
                        downloadedFile = file
                        isDownloaded = true
                        break
                    }
                }
            }
            if (!isDownloaded)
                downloadFile()
            else if (isDownloaded) {
                showpdf(downloadedFile!!)
            }
        } else {
            if (MediaUtils.isStoragePermissionGranted(this)) {
                this.recreate()
            } else {
                onBackPressed()
            }
        }

        intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val downloadManager =
            this@PdfActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        receiver = object : DownloadBroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                super.onReceive(context, intent)
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                val file = File("$path/$fileName")

                downloadManager.addCompletedDownload(
                    fileName,
                    " ",
                    false,
                    "application/pdf",
                    path,
                    file.length(),
                    true
                )
                showpdf(file)
            }
        }

        pdfBookmarkBtn.setOnClickListener{view->
            if (pdfBookmarkBtn.isActivated)
                vm.removeBookmark()
            else {
                vm.markBookmark()
            }
        }

        vm.bookmark.observer(this){
            pdfBookmarkBtn.isActivated = if (it == null) false else it.bookmarkUid.isNotEmpty()
        }

        vm.offlineSnackbar.observer(this){
            root.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
        }

        this@PdfActivity.registerReceiver(receiver, intentFilter)
    }

    private fun showpdf(downloadedFile: File) {
        pdfViewPager = PDFViewPager(this, downloadedFile.absolutePath)
        pdfBookmarkBtn.isVisible = true

        root.addView(
            pdfViewPager,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun downloadFile() {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(fileName)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            // get download service and enqueue file
            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().log("Error Downloading Pdf: $url}")
        }
    }

    override fun onDestroy() {
        if (MediaUtils.checkPermission(this)) {
            try {
                val adapter = pdfViewPager.adapter
                if (adapter is PDFPagerAdapter)
                    adapter.close()
                pdfViewPager.adapter = null
            } catch (e: Exception) {
                finish()
            }
        }
        this@PdfActivity.unregisterReceiver(receiver)
        super.onDestroy()
    }
}
