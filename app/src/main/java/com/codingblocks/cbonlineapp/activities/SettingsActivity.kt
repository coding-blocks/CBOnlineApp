package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.extensions.folderSize
import com.codingblocks.cbonlineapp.extensions.readableFileSize
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val contentDao by lazy {
        database.contentDao()
    }
    private val file by lazy {
        this.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
    }

    val stat by lazy { StatFs(Environment.getExternalStorageDirectory().path) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        wifiSwitch.isChecked = getPrefs().SP_WIFI

        wifiSwitch.setOnClickListener {
            getPrefs().SP_WIFI = wifiSwitch.isChecked
        }
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        spaceFreeTv.text = String.format("%s free", bytesAvailable.readableFileSize())
        spaceUsedTv.text = String.format("%s used", folderSize(
            file
        ).readableFileSize())

        deleteAllTv.setOnClickListener {
            contentDao.getDownloads("true").let { list ->
                list.forEach { content ->
                    val folderFile = File(file, "/${content.contentLecture.lectureId}")
                    MediaUtils.deleteRecursive(folderFile)
                    contentDao.updateContent(
                        content.section_id,
                        content.contentLecture.lectureContentId,
                        "false"
                    )
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
