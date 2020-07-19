package com.codingblocks.cbonlineapp.settings

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.extensions.*
import java.io.File
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : BaseCBActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val file by lazy {
        this.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
    }

    private val stat by lazy { StatFs(Environment.getExternalStorageDirectory().path) }

    private var isSomethingDownloaded = false

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
        updateDeleteAllTv()

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
    }

    private fun updateSpaceStats() {
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        spaceFreeTv.text = String.format("%s free", bytesAvailable.readableFileSize())
        spaceUsedTv.text = String.format("%s used", file?.let {
            folderSize(
                it
            ).readableFileSize()
        })

        val usedSpace : Double = ((file?.let { folderSize(it) }?.toDouble()?.div(1048576) ?: 0.0))
        Log.v("usedSpace","Used Space is $usedSpace")
        storageProgress.max = bytesAvailable.toInt() / 1048576
        storageProgress.progress = usedSpace.toInt()

        isSomethingDownloaded = usedSpace != 0.0
    }

    private fun updateDeleteAllTv() {
        if (isSomethingDownloaded) {
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
        } else {
            deleteAllTv.visibility = View.INVISIBLE
        }
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
