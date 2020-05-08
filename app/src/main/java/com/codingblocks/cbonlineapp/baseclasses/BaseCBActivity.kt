package com.codingblocks.cbonlineapp.baseclasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.getLoadingDialog
import kotlinx.android.synthetic.main.offline_page.view.*

/**
 * Created by championswimmer on 2020-02-02.
 */
abstract class BaseCBActivity : AppCompatActivity(), BaseLifecycleComponent {
    override val thisLifecycleOwner: LifecycleOwner get() = this

    val loadingDialog by lazy {
        getLoadingDialog()
    }

    init {
        lifecycle.addObserver(this)
    }

    lateinit var topView: View

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
        lifecycle.removeObserver(this)
    }

    fun showOffline() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        topView = inflater.inflate(R.layout.offline_page, null)
        topView.offlineBtn.setOnClickListener {
            // check connectivity
            topView.isVisible = false
        }
        addContentView(topView, ViewGroup.LayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)))
    }
}
