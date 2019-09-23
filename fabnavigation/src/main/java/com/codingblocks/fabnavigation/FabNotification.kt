package com.codingblocks.fabnavigation

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.annotation.ColorInt
import java.util.*

class FabNotification : Parcelable {

    var text: String? = null
        private set // can be null, so notification will not be shown

    @ColorInt
    var textColor: Int = 0
        private set // if 0 then use default value

    @ColorInt
    var backgroundColor: Int = 0
        private set // if 0 then use default value

    val isEmpty: Boolean
        get() = TextUtils.isEmpty(text)

    constructor() {
        // empty
    }

    private constructor(`in`: Parcel) {
        text = `in`.readString()
        textColor = `in`.readInt()
        backgroundColor = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(text)
        dest.writeInt(textColor)
        dest.writeInt(backgroundColor)
    }

    class Builder {
        private var text: String? = null
        @ColorInt
        private var textColor: Int = 0
        @ColorInt
        private var backgroundColor: Int = 0

        fun setText(text: String): Builder {
            this.text = text
            return this
        }

        fun setTextColor(@ColorInt textColor: Int): Builder {
            this.textColor = textColor
            return this
        }

        fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun build(): FabNotification {
            val notification = FabNotification()
            notification.text = text
            notification.textColor = textColor
            notification.backgroundColor = backgroundColor
            return notification
        }
    }

    companion object {

        fun justText(text: String): FabNotification {
            return Builder().setText(text).build()
        }

        fun generateEmptyList(size: Int): MutableList<FabNotification> {
            val notificationList = ArrayList<FabNotification>()
            for (i in 0 until size) {
                notificationList.add(FabNotification())
            }
            return notificationList
        }

        @JvmField
        val CREATOR: Parcelable.Creator<FabNotification> = object : Parcelable.Creator<FabNotification> {
            override fun createFromParcel(`in`: Parcel): FabNotification {
                return FabNotification(`in`)
            }

            override fun newArray(size: Int): Array<FabNotification?> {
                return arrayOfNulls(size)
            }
        }
    }

}
