package com.codingblocks.cbonlineapp.mycourse.content.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.RoundedBottomSheetDialogFragment
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.bottom_sheet_note.*
import kotlinx.android.synthetic.main.bottom_sheet_note.view.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoBottomSheet : RoundedBottomSheetDialogFragment(), View.OnClickListener {

    private val vm: VideoPlayerViewModel by sharedViewModel()
    lateinit var type: VideoSheetType
    var notes: NotesModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_note, container, false)
        notes = arguments?.getSerializable("item") as NotesModel?
        type = arguments?.getSerializable("type") as VideoSheetType
        arguments?.let {
            when (type) {
                VideoSheetType.NOTE_EDIT -> {
                    view.bottomSheetTitleTv.text = getString(R.string.edit_note)
                    view.doubtTitleTv.isVisible = false
                }
                VideoSheetType.NOTE_CREATE -> {
                    view.bottomSheetTitleTv.text = getString(R.string.add_note)
                    view.doubtTitleTv.isVisible = false
                    view.bottoSheetDescTv.apply {
                        setText("")
                        hint = "Add a note here"
                    }
                }
                VideoSheetType.DOUBT_CREATE -> {
                    view.bottomSheetTitleTv.text = getString(R.string.ask_doubt)
                    view.doubtTitleTv.isVisible = true
                    view.bottoSheetDescTv.apply {
                        setText("")
                        hint = "Description of Doubt"
                    }
                }
            }
        }
        view.bottomSheetCancelBtn.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (type) {
            VideoSheetType.NOTE_EDIT -> {
                bottoSheetDescTv.setText(notes!!.text)
                bottomSheetSaveBtn.apply {
                    text = getString(R.string.update)
                    setOnClickListener(this@VideoBottomSheet)
                }
                bottomSheetInfoTv.text = getString(R.string.notes_info, notes!!.contentTitle, notes!!.duration.secToTime())
            }
            VideoSheetType.NOTE_CREATE -> {
                bottomSheetInfoTv.text = getString(R.string.notes_info, notes!!.contentTitle, notes!!.duration.secToTime())

                bottomSheetSaveBtn.apply {
                    text = getString(R.string.save)
                    setOnClickListener(this@VideoBottomSheet)
                }
            }
            VideoSheetType.DOUBT_CREATE -> {
                view.bottomSheetInfoTv.text = "${requireActivity().findViewById<TextView>(R.id.contentTitle).text}"
                bottomSheetSaveBtn.apply {
                    text = getString(R.string.post)
                    setOnClickListener(this@VideoBottomSheet)
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bottomSheetSaveBtn -> {
                when ((v as Button).text) {
                    "Save" -> {
                        val desc = bottoSheetDescTv.text.toString()
                        if (desc.isEmpty()) {
                            toast("Note cannot be empty!!")
                        } else {
                            val note = Note(
                                notes!!.duration,
                                desc,
                                RunAttempts(vm.attemptId.value ?: ""),
                                LectureContent(vm.currentContentId ?: "")
                            )
                            vm.createNote(note)
                            dialog?.dismiss()
                        }
                    }
                    "Update" -> {
                        vm.updateNote(
                            notes!!.apply {
                                text = bottoSheetDescTv.text.toString()
                            }
                        )
                        dialog?.dismiss()
                    }
                    "Post" -> {
                        vm.createDoubt(doubtTitleTv.text.toString(), bottoSheetDescTv.text.toString()) {
                            runOnUiThread {
                                if (it.isEmpty()) {
                                    dialog?.dismiss()
                                } else
                                    toast(it)
                            }
                        }
                    }
                }
            }
            else -> {
                dialog?.dismiss()
            }
        }
    }

    companion object {
        enum class VideoSheetType {
            NOTE_EDIT,
            NOTE_CREATE,
            DOUBT_CREATE
        }
    }
}
