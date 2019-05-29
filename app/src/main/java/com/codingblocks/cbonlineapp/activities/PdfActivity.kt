package com.codingblocks.cbonlineapp.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.DownloadBroadcastReceiver
import com.codingblocks.cbonlineapp.util.MediaUtils
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import kotlinx.android.synthetic.main.activity_pdf.root
import org.jetbrains.anko.AnkoLogger
import java.io.File

class PdfActivity : AppCompatActivity(), AnkoLogger {
    lateinit var pdfViewPager: PDFViewPager
    lateinit var url: String
    lateinit var fileName: String
    var path: String? = null
    var isDownloaded: Boolean = false
    lateinit var receiver: DownloadBroadcastReceiver
    lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        url = intent.getStringExtra("fileUrl")
        fileName = intent.getStringExtra("fileName")

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

        this@PdfActivity.registerReceiver(receiver, intentFilter)
    }

    private fun showpdf(downloadedFile: File) {
        pdfViewPager = PDFViewPager(this, downloadedFile.absolutePath)

        root.addView(
            pdfViewPager,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun downloadFile() {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        // get download service and enqueue file
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MediaUtils.checkPermission(this)) {
            try {
                if (pdfViewPager.adapter != null)
                    (pdfViewPager.adapter as PDFPagerAdapter).close()
            } catch (e: Exception) {
                onBackPressed()
            }
        } else {
        }
        this@PdfActivity.unregisterReceiver(receiver)
    }
}
