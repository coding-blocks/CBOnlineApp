package com.codingblocks.cbonlineapp.settings

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.extensions.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SettingsActivity : BaseCBActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val file by lazy {
        if (getPrefs().SP_SD_CARD && checkExternalDirectory()) {
            this.getExternalFilesDirs(Environment.getDataDirectory().absolutePath)[1]
        } else {
            this.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
        }
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
        updateSpaceStats()
        deleteAllTv.setOnClickListener {
            showDialog(
                type = "Delete",
                image = R.drawable.ic_info,
                cancelable = false,
                primaryText = R.string.confirmation,
                secondaryText = R.string.delete_video_desc,
                primaryButtonText = R.string.confirm,
                secondaryButtonText = R.string.cancel,
                callback = { confirmed ->
                    if (confirmed) {
                        lifecycleScope.launch {
                            val files = viewModel.getDownloads()
                            files.forEach { content ->

                                val folderFile = File(file, "/${content.contentLecture.lectureId}")

                                withContext(Dispatchers.IO) {
                                    FileUtils.deleteRecursive(folderFile)
                                }
                                runOnUiThread { updateSpaceStats() }
                            }
                        }
                    }
                }
            )
        }
        setSeekbarMaxValue()
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
        sdcardSwitch.isChecked = checkExternalDirectory() && getPrefs().SP_SD_CARD
        sdcardSwitch.setOnClickListener {
            updateSdCardSwitch()
        }
    }

    private fun updateSdCardSwitch() {
        if (sdcardSwitch.isChecked) {
            if (checkExternalDirectory()) {
                getPrefs().SP_SD_CARD = true
            } else {
                Toast.makeText(this, "No External SD Card found.", Toast.LENGTH_LONG).show()
                sdcardSwitch.performClick()
            }
        } else {
            getPrefs().SP_SD_CARD = false
        }
    }

    private fun checkExternalDirectory(): Boolean {
        val directories = applicationContext.getExternalFilesDirs(Environment.getDataDirectory().absolutePath)
        if (directories.size > 1)
            return true
        return false
    }

    private fun updateSpaceStats() {
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        spaceFreeTv.text = String.format("%s free", bytesAvailable.readableFileSize())
        spaceUsedTv.text = String.format(
            "%s used",
            file?.let {
                folderSize(
                    it
                ).readableFileSize()
            }
        )

        val usedSpace: Double = ((file?.let { folderSize(it) }?.toDouble()?.div(1048576) ?: 0.0))
        Log.v("usedSpace", "Used Space is $usedSpace")
        storageProgress.max = bytesAvailable.toInt() / 1048576
        storageProgress.progress = usedSpace.toInt()
    }

    private fun setSeekbarProgress(progress: Int) {
        val size = (1 + progress / 100.toDouble()).toFloat()
        seekbarTv.text = "${size}GB"
        getPrefs().SP_DATA_LIMIT = size
    }

    private fun setSeekbarMaxValue() {
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        val progressValue = (100 * bytesAvailable / Math.pow(1024.0, 3.0)).toInt() - 100
        seekbarLimit.max = progressValue
    }
}
