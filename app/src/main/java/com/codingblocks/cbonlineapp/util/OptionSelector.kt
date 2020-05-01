package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.VideoUtils.getSizeBytes
import com.codingblocks.cbonlineapp.util.VideoUtils.getSizeString
import com.codingblocks.cbonlineapp.util.VideoUtils.round
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.offline.DownloadOptions
import java.util.Arrays
import java.util.Locale
import kotlin.collections.ArrayList

class OptionSelector(
    private val downloadOptions: DownloadOptions,
    private val durationMs: Long,
    private val optionSelectedCallback: OptionsSelectedCallback,
    private val optionStyle: OptionStyle
) : DialogInterface.OnClickListener, View.OnClickListener {

    interface OptionsSelectedCallback {
        fun onTracksSelected(downloadOptions: DownloadOptions, selectedTracks: IntArray)
    }

    enum class OptionStyle {
        SHOW_INDIVIDUAL_TRACKS,
        SHOW_HIGHEST_AND_LOWEST_QUALITY
    }

    private val selectedTracks = ArrayList<Int>()

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            selectedTracks.clear()
        } else if (which == DialogInterface.BUTTON_POSITIVE) {
            val selectionIndices = selectedTracks.toIntArray()
            Arrays.sort(selectionIndices)
            optionSelectedCallback.onTracksSelected(downloadOptions, selectionIndices)
        }
    }

    override fun onClick(v: View) {
        if (v is CheckedTextView) {
            if (optionStyle == OptionStyle.SHOW_INDIVIDUAL_TRACKS) {
                val trackIndex = v.tag as Int
                if (v.isChecked) {
                    selectedTracks.remove(Integer.valueOf(trackIndex))
                    v.isChecked = false
                } else {
                    if (!selectedTracks.contains(trackIndex)) selectedTracks.add(trackIndex)
                    v.isChecked = true
                }
            } else {
                val (audioTrackIndex, videoTrackIndex) = v.tag as Pair<Int, Int>
                if (v.isChecked) {
                    selectedTracks.remove(Integer.valueOf(audioTrackIndex))
                    selectedTracks.remove(Integer.valueOf(videoTrackIndex))
                    v.isChecked = false
                } else {
                    selectedTracks.clear()
                    selectedTracks.add(audioTrackIndex)
                    selectedTracks.add(videoTrackIndex)
                    v.isChecked = true
                }
            }
        }
    }

    fun showSelectionDialog(context: Context, title: CharSequence) {
        selectedTracks.clear()
        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(buildView(context, downloadOptions.availableTracks))
            .setPositiveButton("Download", this)
            .setNegativeButton(android.R.string.cancel, this)
            .create()
            .show()
    }

    private fun buildView(context: Context, tracks: Array<Track>): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.track_selection_dialog, null)
        val root = view.findViewById(R.id.root) as ViewGroup

        val attributeArray = context.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.selectableItemBackground)
        )
        val selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0)
        attributeArray.recycle()

        if (optionStyle === OptionStyle.SHOW_INDIVIDUAL_TRACKS) {
            buildIndividualTracksView(context, inflater, root, selectableItemBackgroundResourceId, tracks)
        } else {
            buildCombinedTrackView(context, inflater, root, selectableItemBackgroundResourceId, tracks)
        }

        return view
    }

    private fun buildIndividualTracksView(
        context: Context,
        inflater: LayoutInflater,
        root: ViewGroup,
        selectableItemBackgroundResourceId: Int,
        tracks: Array<Track>
    ) {
        // video and audio type track views
        val vidTrackIndices = getTypeIndices(tracks, Track.TYPE_VIDEO)
        Log.i(TAG, "${vidTrackIndices.size} video tracks at ${Arrays.toString(vidTrackIndices)}")
        addTypeTracksToView(inflater, root, selectableItemBackgroundResourceId, tracks, vidTrackIndices)
        root.addView(inflater.inflate(R.drawable.divider, root, false))
        val audTrackIndices = getTypeIndices(tracks, Track.TYPE_AUDIO)
        Log.i(TAG, "${audTrackIndices.size} audio tracks at ${Arrays.toString(audTrackIndices)}")
        addTypeTracksToView(inflater, root, selectableItemBackgroundResourceId, tracks, audTrackIndices)
    }

    private fun buildCombinedTrackView(
        context: Context,
        inflater: LayoutInflater,
        root: ViewGroup,
        selectableItemBackgroundResourceId: Int,
        tracks: Array<Track>
    ) {
        // we'll show two options: highest quality (audio + video) and lowest quality (audio + video)

        val highestQualityVidTrackIndex = getHighestBitrateIndex(tracks, Track.TYPE_VIDEO)
        val lowestQualityVidTrackIndex = getLowestBitrateIndex(tracks, Track.TYPE_VIDEO)

        val highestQualityAudTrackIndex = getHighestBitrateIndex(tracks, Track.TYPE_AUDIO)
        val lowestQualityAudTrackIndex = getLowestBitrateIndex(tracks, Track.TYPE_AUDIO)

        // if there is only one video track and one audio track, show only one option
        val showSingleOption =
            highestQualityVidTrackIndex == lowestQualityVidTrackIndex && highestQualityAudTrackIndex == lowestQualityAudTrackIndex
        if (showSingleOption) {
            Log.i(
                TAG,
                String.format(
                    Locale.US,
                    "High quality indices (%d, %d)",
                    highestQualityAudTrackIndex,
                    highestQualityVidTrackIndex
                )
            )
            val audVidIndexPair = Pair(highestQualityAudTrackIndex, highestQualityVidTrackIndex)
            addOptionToView("High", inflater, root, selectableItemBackgroundResourceId, tracks, audVidIndexPair)
            root.addView(inflater.inflate(R.drawable.divider, root, false))
        } else {
            Log.i(
                TAG,
                String.format(
                    Locale.US,
                    "High quality indices (%d, %d)",
                    highestQualityAudTrackIndex,
                    highestQualityVidTrackIndex
                )
            )
            val highQualityAudVidIndexPair = Pair(highestQualityAudTrackIndex, highestQualityVidTrackIndex)
            addOptionToView(
                "High",
                inflater,
                root,
                selectableItemBackgroundResourceId,
                tracks,
                highQualityAudVidIndexPair
            )
            root.addView(inflater.inflate(R.drawable.divider, root, false))

            Log.i(
                TAG,
                String.format(
                    Locale.US,
                    "Low quality indices (%d, %d)",
                    lowestQualityAudTrackIndex,
                    lowestQualityVidTrackIndex
                )
            )
            val lowQualityAudVidIndexPair = Pair(lowestQualityAudTrackIndex, lowestQualityVidTrackIndex)
            addOptionToView(
                "Low",
                inflater,
                root,
                selectableItemBackgroundResourceId,
                tracks,
                lowQualityAudVidIndexPair
            )
        }
    }

    private fun addTypeTracksToView(
        inflater: LayoutInflater,
        root: ViewGroup,
        selectableItemBackgroundResourceId: Int,
        allTracks: Array<Track>,
        typeIndices: IntArray
    ) {
        for (typeIndex in typeIndices) {
            val track = allTracks[typeIndex]
            val trackViewLayoutId = android.R.layout.simple_list_item_single_choice
            val trackView = inflater.inflate(
                trackViewLayoutId, root, false
            ) as CheckedTextView
            trackView.setBackgroundResource(selectableItemBackgroundResourceId)
            trackView.text = getDownloadItemName(track, durationMs)
            trackView.isFocusable = true
            trackView.isChecked = false
            trackView.tag = typeIndex
            trackView.setOnClickListener(this)
            root.addView(trackView)
        }
    }

    private fun addOptionToView(
        optionName: String,
        inflater: LayoutInflater,
        root: ViewGroup,
        selectableItemBackgroundResourceId: Int,
        allTracks: Array<Track>,
        audioVideoTrackIndexPair: Pair<Int, Int>
    ) {
        val optionText = makeOptionText(
            optionName, audioVideoTrackIndexPair.first,
            audioVideoTrackIndexPair.second, allTracks, durationMs
        )
        val trackViewLayoutId = android.R.layout.simple_list_item_single_choice
        val trackView = inflater.inflate(
            trackViewLayoutId, root, false
        ) as CheckedTextView
        trackView.setBackgroundResource(selectableItemBackgroundResourceId)
        trackView.text = optionText
        trackView.isFocusable = true
        trackView.isChecked = false
        trackView.tag = audioVideoTrackIndexPair
        trackView.setOnClickListener(this)
        root.addView(trackView)
    }

    companion object {
        private const val TAG = "OptionSelector"

        private fun getTypeIndices(tracks: Array<Track>, type: Int): IntArray {
            val indexList = ArrayList<Int>()
            for (i in tracks.indices) {
                if (type == tracks[i].type) {
                    indexList.add(i)
                }
            }
            val indices = IntArray(indexList.size)
            for (x in indexList.indices) {
                indices[x] = indexList[x]
            }
            return indices
        }

        private fun makeOptionText(
            optionName: String,
            audioIndex: Int,
            videoIndex: Int,
            tracks: Array<Track>,
            durationMs: Long
        ): String {
            val audioTrack = tracks[audioIndex]
            val videoTrack = tracks[videoIndex]
            val videoSizeBytes = getSizeBytes(videoTrack.bitrate, durationMs)
            val audioSizeBytes = getSizeBytes(audioTrack.bitrate, durationMs)
            val totalSizeBytes = videoSizeBytes + audioSizeBytes
            val totalSizeMB = round(totalSizeBytes.toDouble() / (1024 * 1024), 2).toString() + " MB"

            return "$optionName (${tracks[videoIndex].bitrate / 1024} kbps), $totalSizeMB"
        }

        /**
         * @return index of the track in provided array with highest bitrate, or -1 if none of type found
         */
        private fun getHighestBitrateIndex(tracks: Array<Track>, trackType: Int): Int {
            var highestBitrateTrackIndex = -1
            var highestBitrate = 0
            for (i in tracks.indices) {
                val track = tracks[i]
                if (track.type == trackType) {
                    val bitrate = track.bitrate
                    if (bitrate >= highestBitrate) {
                        highestBitrate = bitrate
                        highestBitrateTrackIndex = i
                    }
                }
            }
            return highestBitrateTrackIndex
        }

        /**
         * @return index of the track in provided array with lowest bitrate, or -1 if none of type found
         */
        private fun getLowestBitrateIndex(tracks: Array<Track>, trackType: Int): Int {
            var lowestBitrateTrackIndex = -1
            var lowestBitrate = Integer.MAX_VALUE
            for (i in tracks.indices) {
                val track = tracks[i]
                if (track.type == trackType) {
                    val bitrate = track.bitrate
                    if (bitrate <= lowestBitrate) {
                        lowestBitrate = bitrate
                        lowestBitrateTrackIndex = i
                    }
                }
            }
            return lowestBitrateTrackIndex
        }

        private fun getDownloadItemName(track: Track, durationMs: Long): String {
            val type = when {
                track.type == Track.TYPE_VIDEO -> "V"
                track.type == Track.TYPE_AUDIO -> "A"
                else -> "?"
            }
            return "$type ${track.bitrate / 1024} kbps, ${getSizeString(track.bitrate, durationMs)}"
        }
    }
}
