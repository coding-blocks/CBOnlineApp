package com.codingblocks.cbonlineapp.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.NetworkUtils.okHttpClient
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

fun ImageView.loadSvg(svgUrl: String, onDrawableCreated: ((Drawable) -> Unit)?) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    doAsync {
        okHttpClient.newCall((Request.Builder().url(svgUrl).build()))
                .execute().body()?.let {

                    with(SVG.getFromInputStream(it.byteStream())) {
                        uiThread {
                            val picDrawable = PictureDrawable(renderToPicture(
                                    400, 400
                            ))
                            setImageDrawable(picDrawable)
                            onDrawableCreated?.let { it(picDrawable) }
                        }
                    }
                }
    }
}

fun ImageView.loadImage(imgUrl: String, scale: Boolean = false) {
    if (imgUrl.takeLast(3) == "png") {
        if (scale)
            Picasso.with(context).load(imgUrl).resize(72, 72).into(this)
        else
            Picasso.with(context).load(imgUrl).into(this)
    } else {
        loadSvg(imgUrl, null)
    }
}

fun Snackbar.config(context: Context, @ColorInt backgroundColor: Int, @ColorInt textColor: Int) {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(12, 12, 12, 12)
    this.view.layoutParams = params
    this.view.background = context.getDrawable(R.drawable.bg_snackbar)

    this.view.setBackgroundColor(backgroundColor)
    this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(textColor)

    ViewCompat.setElevation(this.view, 6f)
}
