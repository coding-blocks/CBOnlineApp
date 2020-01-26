package com.codingblocks.cbonlineapp.util.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.GlideApp
import com.codingblocks.cbonlineapp.GlideRequest
import com.codingblocks.cbonlineapp.util.NetworkUtils.okHttpClient
import kotlinx.android.synthetic.main.tour_layout.view.*
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

fun ImageView.loadImage(imgUrl: String, scale: Boolean = false, callback: () -> Unit = { }) {
    if (imgUrl.isNotEmpty())
        createGlideRequest(Uri.parse(imgUrl), context)
            .listener(SvgSoftwareLayerSetter1())
            .centerCrop()
            .error(createGlideRequest(Uri.parse(imgUrl), context))
            .into(this)
    callback()
}

class SvgSoftwareLayerSetter1 : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: com.bumptech.glide.request.target.Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        if (target is ImageViewTarget<*>) {
            val view = target.view
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null)
            return false
        }
        return true
    }

    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        val view = (target as ImageViewTarget<*>).view
        view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        return false
    }
}

private fun createGlideRequest(source: Uri?, context: Context): GlideRequest<Drawable> {
    return GlideApp.with(context)
        .load(source)
        .dontAnimate() // will load image
}
