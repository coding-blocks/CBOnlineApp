package com.codingblocks.cbonlineapp.util

import com.vdocipher.aegis.player.VdoPlayer
import java.math.BigDecimal
import java.math.RoundingMode

object VideoUtils {
    fun digitalClockTime(timeInMilliSeconds: Int): String {
        val totalSeconds = timeInMilliSeconds / 1000
        val hours = totalSeconds / (60 * 60)
        val minutes = (totalSeconds - hours * 60 * 60) / 60
        val seconds = totalSeconds - hours * 60 * 60 - minutes * 60

        var timeThumb = ""
        if (hours > 0) {
            timeThumb += if (hours < 10) {
                "0$hours:"
            } else {
                "$hours:"
            }
        }
        timeThumb += if (minutes > 0) {
            if (minutes < 10) {
                "0$minutes:"
            } else {
                "$minutes:"
            }
        } else {
            "00" + ":"
        }
        if (seconds < 10) {
            timeThumb += "0$seconds"
        } else {
            timeThumb += seconds
        }
        return timeThumb
    }

    /**
     * @return index of number in provided array closest to the provided number
     */
    fun getClosestFloatIndex(refArray: FloatArray, comp: Float): Int {
        var distance = Math.abs(refArray[0] - comp)
        var index = 0
        for (i in 1 until refArray.size) {
            val currDistance = Math.abs(refArray[i] - comp)
            if (currDistance < distance) {
                index = i
                distance = currDistance
            }
        }
        return index
    }

    fun playbackStateString(playWhenReady: Boolean, playbackState: Int): String {
        val stateName: String = when (playbackState) {
            VdoPlayer.STATE_IDLE -> "STATE_IDLE"
            VdoPlayer.STATE_READY -> "STATE_READY"
            VdoPlayer.STATE_BUFFERING -> "STATE_BUFFERING"
            VdoPlayer.STATE_ENDED -> "STATE_ENDED"
            else -> "STATE_UNKNOWN"
        }
        return "playWhenReady " + (if (playWhenReady) "true" else "false") + ", " + stateName
    }

    fun getSizeString(bitsPerSec: Int, millisec: Long): String {
        val sizeMB = bitsPerSec.toDouble() / (8 * 1024 * 1024) * (millisec / 1000)
        return round(sizeMB, 2).toString() + " MB"
    }

    fun getSizeBytes(bitsPerSec: Int, millisec: Long): Long {
        return bitsPerSec / 8 * (millisec / 1000)
    }

    fun round(value: Double, places: Int): Double {
        if (places < 0) throw IllegalArgumentException()

        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}
