package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ShareUtils {
    fun shareToFacebook(msg: String, context: Context) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        var facebookAppFound = false
        val matches = context.packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName)
                facebookAppFound = true
                break
            }
        }
        if (!facebookAppFound) {
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=online.codingblocks.com&quote=$msg"
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        }

        context.startActivity(intent)
    }

    fun shareToWhatsapp(msg: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        if (context.packageManager.resolveActivity(intent, 0) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Please install whatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareToTwitter(msg: String, context: Context) {
        val url = "http://www.twitter.com/intent/tweet?text=$msg"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }
}
