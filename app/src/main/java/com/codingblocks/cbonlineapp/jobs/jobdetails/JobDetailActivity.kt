package com.codingblocks.cbonlineapp.jobs.jobdetails

import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.JOB_ID
import com.codingblocks.cbonlineapp.util.extensions.getSpannableSring
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.nonNull
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.Form
import com.codingblocks.onlineapi.models.JobId
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_job_detail.*
import kotlinx.android.synthetic.main.custom_form_dialog.view.*
import kotlinx.android.synthetic.main.item_job.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobDetailActivity : BaseCBActivity() {

    private val viewModel by viewModel<JobDetailViewModel>()

//    private lateinit var courseDataAdapter: CourseDataAdapter

    lateinit var jobId: String

    val formDialog by lazy {
        AlertDialog.Builder(this).create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        jobId = intent.getStringExtra(JOB_ID)

//        deadlinell.visibility = View.GONE

        viewModel.fetchJob(jobId)

//        courseDataAdapter = CourseDataAdapter()
        rvJobCourses.layoutManager = LinearLayoutManager(this)
//        rvJobCourses.adapter = courseDataAdapter

        jobDescriptionBtn.setOnClickListener {
            cardJobDescription.isVisible = !cardJobDescription.isVisible
        }
        makeForm(viewModel.formData)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.getJobById(jobId).observeOnce {
            with(it) {
                companyLogo.loadImage(company.logo)
                jobTitleTv.text = title
                supportActionBar?.title = title
                companyTv.text = company.name
//                postedAgoTv.text = timeAgo(postedOn.isotomillisecond())
                locationTv.text = getSpannableSring("Job Location: ", location)
                experienceTv.text = getSpannableSring("Experience: ", experience)
                typeTv.text = getSpannableSring("Job Type: ", type)
                ctcTv.text = getSpannableSring("CTC: ", ctc)
                jobDescriptionTv.text = description
                companyDescriptionTv.text = company.companyDescription
                eligibleTv.text = "Eligibility:    $eligibility"
                viewModel.courseIdList.postValue(courseId)
            }
        }

        viewModel.jobCourses.distinctUntilChanged().nonNull().observer(this) {
            //            courseDataAdapter.submitList(it)
        }
        viewModel.eligibleLiveData.observer(this) {
            when (it) {
                "eligible" -> statusTv.text = getString(R.string.job_eligible)
                "not eligible" -> {
                    statusTv.setTextColor(resources.getColor(R.color.salmon))
                    statusTv.text = getString(R.string.job_not_eligible)
                    addResumeBtn.isVisible = false
                }
                "Applied" -> {
                    statusTv.text = getString(R.string.job_applied)
                    addResumeBtn.isVisible = false
                }
            }
        }
    }

    private fun makeForm(formData: MutableLiveData<ArrayList<Form>>) {

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val sizeInDP = 8

        val marginInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), resources
            .displayMetrics
        ).toInt()
        params.setMargins(marginInDp, marginInDp / 2, marginInDp, marginInDp / 2)
        val formView = layoutInflater.inflate(R.layout.custom_form_dialog, null)
        val formlayout = formView.form
        formData.observer(this) {
            it.forEachIndexed { index, it ->
                if (it.type == "text-field") {
                    val inputLayout = TextInputLayout(
                        this,
                        null,
                        R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
                    )

                    inputLayout.layoutParams = params
                    inputLayout.setPadding(marginInDp, 0, 0, 0)
                    if (it.required) {
                        if (it.title.length > 30) {
                            val title = TextView(this).apply {
                                text = "${it.title}*"
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                layoutParams = params
                            }
                            formlayout.addView(title)
                        } else {
                            inputLayout.hint = "${it.title}*"
                        }
                    } else {
                        if (it.title.length > 30) {
                            val title = TextView(this).apply {
                                text = it.title
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                layoutParams = params
                            }
                            formlayout.addView(title)
                        } else {
                            inputLayout.hint = it.title
                        }
                    }
                    inputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
//                    inputLayout.setBoxCornerRadii(20f, 20f, 20f, 20f)
                    val edittext = TextInputEditText(inputLayout.context)
                    edittext.setOnFocusChangeListener { view, b ->
                    }
                    inputLayout.addView(edittext)
                    inputLayout.tag = it.name

                    formlayout.addView(inputLayout)
                } else if (it.type == "radio-group") {
                    val title = TextView(this).apply {
                        text = it.title
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        layoutParams = params
                    }
                    formlayout.addView(title)
                    val optionsArray = it.options?.split(",")

                    val rb = arrayOfNulls<RadioButton>(optionsArray!!.size)
                    val rg = RadioGroup(this) // create the RadioGroup
                    rg.layoutParams = params
                    rg.orientation = RadioGroup.VERTICAL // or RadioGroup.VERTICAL
                    for (i in rb.indices) {
                        rb[i] = RadioButton(this)
                        rb[i]?.text = optionsArray[i]
                        rg.addView(rb[i])
                    }
                    rg.tag = it.name
                    formlayout.addView(rg)
                }
            }

            formView.okBtn.setOnClickListener { view ->
                val jsonObject = JsonObject()
                it.forEach { form ->
                    if (form.type == "text-field") {
                        val inputLayout = formlayout.findViewWithTag<TextInputLayout>(form.name)
                        if (inputLayout.editText?.text.isNullOrEmpty() && form.required) {
                            inputLayout.isErrorEnabled = true
                            inputLayout.error = "Cannot Be Empty"
                        } else {
                            inputLayout.isErrorEnabled = false
                            inputLayout.error = null
                            jsonObject.addProperty(form.name, inputLayout.editText?.text.toString())
                        }
                    } else if (form.type == "radio-group") {
                        val group = formlayout.findViewWithTag<RadioGroup>(form.name)
                        val radioButton = findViewById<RadioButton>(group.checkedRadioButtonId)
                        val optionsArray = form.options?.split(",")
                        val selected_value: String = radioButton?.text?.toString()
                            ?: (optionsArray?.get(0) ?: "")
                        jsonObject.addProperty(form.name, selected_value)
                    }
                }
                formDialog.dismiss()
//                if (!BuildConfig.DEBUG)
                viewModel.applyJob(Applications(jsonObject, job = JobId(jobId)))
            }
            formView.cancelBtn.setOnClickListener { view ->
                it.forEach { form ->
                    if (form.type == "text-field") {
                        val inputLayout = formlayout.findViewWithTag<TextInputLayout>(form.name)
                        inputLayout.editText?.setText("")
                    }
                }
            }
        }

        addResumeBtn.setOnClickListener {
            formDialog.apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setView(formView)
                setCancelable(true)
                show()
                window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            }
        }
    }
}
