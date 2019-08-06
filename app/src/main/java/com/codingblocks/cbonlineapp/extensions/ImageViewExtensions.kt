package com.codingblocks.cbonlineapp.extensions

import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import com.caverock.androidsvg.SVG
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

fun ImageView.loadImage(imgUrl: String) {
    if (imgUrl.takeLast(3) == "png") {
        Picasso.with(context).load(imgUrl).into(this)
    } else {
        loadSvg(imgUrl, null)
    }
}

fun Snackbar.withColor(@ColorInt backgroundColor: Int, @ColorInt textColor: Int): Snackbar {
    this.view.setBackgroundColor(backgroundColor)
    this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(textColor)
    return this
}
