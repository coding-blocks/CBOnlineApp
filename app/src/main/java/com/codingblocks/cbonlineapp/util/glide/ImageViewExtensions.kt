package com.codingblocks.cbonlineapp.util.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.NetworkUtils.okHttpClient
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
                    val picDrawable = PictureDrawable(
                        renderToPicture(
                            400, 400
                        )
                    )
                    setImageDrawable(picDrawable)
                    callback()
                }
            }
        }
    }
}

fun ImageView.loadImage(imgUrl: String, scale: Boolean = false, callback: (loaded: Boolean) -> Unit = { }) {
    if (imgUrl.isNotEmpty()) {
        createGlideRequest(Uri.parse(imgUrl), context)
            .listener(SvgSoftwareLayerSetter())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    callback(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    callback(true)
                    return false
                }
            })
            .error(createGlideRequest(Uri.parse(imgUrl), context, scale))
            .into(this)
    }
}

fun createGlideRequest(source: Uri?, context: Context, resize: Boolean = false): GlideRequest<Drawable> {
    val req = GlideApp.with(context)
        .load(source)
        .error(R.drawable.defaultavatar)
    if (resize)
        req.override(80, 80)
            .centerCrop()
    else
        req.optionalCenterCrop()
    return req
}
