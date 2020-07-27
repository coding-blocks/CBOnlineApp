package com.codingblocks.cbonlineapp.util.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target

/**
 * Listener which updates the [ImageView] to be software rendered, because [ ]/[Picture][android.graphics.Picture] can't render on a
 * hardware backed [Canvas][android.graphics.Canvas].
 */
class SvgSoftwareLayerSetter : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        if (target is ImageViewTarget<*>) {
            val view = target.view
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null)
            return false
        }
        return true
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        val view = (target as ImageViewTarget<*>).view
        view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        return false
    }
}
