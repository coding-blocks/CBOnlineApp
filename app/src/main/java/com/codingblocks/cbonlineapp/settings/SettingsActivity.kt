package com.codingblocks.cbonlineapp.settings

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.extensions.folderSize
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.readableFileSize
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val file by lazy {
        this.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
    }

    private val stat by lazy { StatFs(Environment.getExternalStorageDirectory().path) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setToolbar(settings_toolbar)
    }

    override fun onStart() {
        super.onStart()
        wifiSwitch.isChecked = getPrefs().SP_WIFI

        wifiSwitch.setOnClickListener {
            getPrefs().SP_WIFI = wifiSwitch.isChecked
        }
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        spaceFreeTv.text = String.format("%s free", bytesAvailable.readableFileSize())
        spaceUsedTv.text = String.format("%s used", file?.let {
            folderSize(
                it
            ).readableFileSize()
        })

        deleteAllTv.setOnClickListener {
            viewModel.getDownloads().let { list ->
                list.forEach { content ->
                    val folderFile = File(file, "/${content.contentLecture.lectureId}")
                    MediaUtils.deleteRecursive(folderFile)
//                    viewModel.updateContent(
//                        content.section_id,
//                        content.contentLecture.lectureContentId,
//                        "false"
//                    )
                }
            }
        }

        seekbarLimit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    setSeekbarProgress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekbarLimit.progress = getPrefs().SP_DATA_LIMIT.minus(1).times(100).toInt()
        setSeekbarProgress(seekbarLimit.progress)

        pipSwitch.isChecked = getPrefs().SP_PIP

        pipSwitch.setOnClickListener {
            getPrefs().SP_PIP = pipSwitch.isChecked
        }
    }

    private fun setSeekbarProgress(progress: Int) {
        val size = (1 + progress / 100.toDouble()).toFloat()
        seekbarTv.text = "${size}GB"
        getPrefs().SP_DATA_LIMIT = size
    }
}
