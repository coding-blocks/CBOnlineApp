package com.codingblocks.cbonlineapp.mycourse.codechallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_code_challenge.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class CodeChallenge : AppCompatActivity() {

    private val vm: CodeChallengeViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_challenge)
        setToolbar(toolbarCodeChallenge)
        title=""
        if (savedInstanceState == null) {
            vm.contentId = intent.getStringExtra(CONTENT_ID)
            vm.sectionId = intent.getStringExtra(SECTION_ID)
            vm.contestId = intent.getStringExtra(CONTEST_ID)
            vm.codeId = intent.getStringExtra(CODE_ID)
        }
        vm.fetchCodeChallenge()

        vm.content.observer(this){
            downloadBtn.isVisible = true
            title = it.content?.name
            setTextView(description, it.content?.details?.description)
            setTextView(constraints, it.content?.details?.constraints)
            setTextView(inputFormat, it.content?.details?.input_format)
            setTextView(outputFormat, it.content?.details?.output_format)
            setTextView(sampleInput, it.content?.details?.sample_input)
            setTextView(sampleOutput, it.content?.details?.sample_output)
            setTextView(explaination, it.content?.details?.explanation)
        }

        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    codeLayout.showSnackbar(it, Snackbar.LENGTH_SHORT){
                        vm.fetchCodeChallenge()
                    }
                }
                ErrorStatus.TIMEOUT -> {
                    codeLayout.showSnackbar(it, Snackbar.LENGTH_INDEFINITE) {
                        vm.fetchCodeChallenge()
                    }
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                    }
                }
                else -> {
                    codeLayout.showSnackbar(it, Snackbar.LENGTH_SHORT)
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }

        downloadBtn.setOnClickListener {
            if (!downloadBtn.isActivated){
                vm.saveCode()
            }
        }

        vm.downloadState.observer(this){
            downloadBtn.isActivated = it
        }
    }

    fun setTextView(textView: TextView, string:String?){
        if(!string.isNullOrEmpty()){
            textView.text = string
        }else
            textView.text = "None"
    }
}
