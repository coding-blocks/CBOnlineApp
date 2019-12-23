package com.codingblocks.cbonlineapp.util.extensions

import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.NetworkUtils.okHttpClient
import com.squareup.picasso.Picasso
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

fun ImageView.loadSvg(svgUrl: String, callback: () -> Unit = { }) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    doAsync {
        okHttpClient.newCall((Request.Builder().url(svgUrl).build()))
            .execute().body?.let {

            with(SVG.getFromInputStream(it.byteStream())) {
                uiThread {
                    val picDrawable = PictureDrawable(renderToPicture(
                        400, 400
                    ))
                    setImageDrawable(picDrawable)
                    callback()
                }
            }
        }
    }
}

fun ImageView.loadImage(imgUrl: String, scale: Boolean = false) {
    if (!imgUrl.isNullOrEmpty())
        if (imgUrl.takeLast(3) == "svg") {
            loadSvg(imgUrl)
        } else {
            if (scale)
                Picasso.get().load(imgUrl).resize(72, 72).placeholder(R.drawable.defaultavatar).into(this)
            else
                Picasso.get().load(imgUrl).placeholder(R.drawable.defaultavatar).into(this)
        }
}

