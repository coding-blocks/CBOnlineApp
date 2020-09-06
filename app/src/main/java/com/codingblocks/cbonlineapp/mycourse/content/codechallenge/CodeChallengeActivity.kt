package com.codingblocks.cbonlineapp.mycourse.content.codechallenge

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_code_challenge.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class CodeChallengeActivity : AppCompatActivity() {

    private val vm: CodeChallengeViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_challenge)
        setToolbar(toolbarCodeChallenge, title = "")
        if (savedInstanceState == null) {
            vm.contentId = intent.getStringExtra(CONTENT_ID)
            vm.sectionId = intent.getStringExtra(SECTION_ID)
        }
        vm.fetchCodeChallenge().observer(this) {
            downloadBtn.isVisible = true
            codeBookmarkBtn.isVisible = true
            title = it?.content?.name

            with(it?.content?.details!!) {
                setTextView(descriptionTv, description)
                setTextView(constraintsTv, constraints)
                setTextView(inputFormatTv, inputFormat)
                setTextView(outputFormatTv, outputFormat)
                setTextView(sampleInputTv, sampleInput)
                setTextView(sampleOutputTv, sampleOutput)
                setTextView(explaination, explanation)
            }
        }

        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    codeLayout.showSnackbar(it, Snackbar.LENGTH_SHORT) {
                        vm.fetchCodeChallenge()
                    }
                }
                ErrorStatus.TIMEOUT -> {
                    codeLayout.showSnackbar(it, Snackbar.LENGTH_INDEFINITE) {
                        vm.fetchCodeChallenge()
                    }
                }
                else -> {
                    codeLayout.showSnackbar("There was some Error fetching this challenge", Snackbar.LENGTH_SHORT, action = false)
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }

        codeBookmarkBtn.setOnClickListener { view ->
            if (codeBookmarkBtn.isActivated)
                vm.removeBookmark()
            else {
                vm.markBookmark()
            }
        }

        vm.getBookmark.observer(this) {
            codeBookmarkBtn.isActivated = it.bookmarkUid.isNotEmpty()
        }

        vm.offlineSnackbar.observer(this) {
            codeLayout.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
        }

        vm.bookmarkLiveData.observe(this) {
            codeBookmarkBtn.isActivated = it
        }

        downloadBtn.setOnClickListener {
            if (!downloadBtn.isActivated) {
                vm.saveCode()
            }
        }

        vm.downloadState.observer(this) {
            downloadBtn.isActivated = it
        }
    }

    fun setTextView(textView: TextView, string: String?) {
        if (!string.isNullOrEmpty()) {
            textView.text = string
        } else
            textView.text = "None"
    }
}
