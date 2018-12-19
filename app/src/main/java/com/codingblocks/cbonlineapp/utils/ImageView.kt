package com.codingblocks.cbonlineapp.utils

import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.uiThread

fun ImageView.loadSvg(svgUrl: String) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    doAsync {
        MediaUtils.okHttpClient.newCall((Request.Builder().url(svgUrl).build()))
            .execute().body()?.let {
                with(SVG.getFromInputStream(it.byteStream())) {
                    uiThread {
                        setImageDrawable(
                            PictureDrawable(renderToPicture())
                        )
                    }

                }
            }
    }
}