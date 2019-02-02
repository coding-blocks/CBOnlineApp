package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.doubt_dialog.view.*
import kotlinx.android.synthetic.main.fragment_video_doubt.view.*
import org.jetbrains.anko.layoutInflater


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VideoDoubtFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_doubt, container, false)

        view.doubtFab.setOnClickListener {
            showDialog()
        }

        return view
    }

    private fun showDialog() {
        val doubtDialog = AlertDialog.Builder(context!!).create()
        val doubtView = context!!.layoutInflater.inflate(R.layout.doubt_dialog, null)
        doubtView.cancelBtn.setOnClickListener {
            doubtDialog.dismiss()
        }
        doubtView.okBtn.setOnClickListener {
                if (doubtView.titleLayout.editText!!.text.length < 15 || doubtView.titleLayout.editText!!.text.isEmpty()) {
                    doubtView.titleLayout.error = "Title length must be atleast 15 characters."
                    return@setOnClickListener
                }else if(doubtView.descriptionLayout.editText!!.text.length < 20 || doubtView.descriptionLayout.editText!!.text.isEmpty()) {
                    doubtView.descriptionLayout.error = "Description length must be atleast 20 characters."
                    doubtView.titleLayout.error = ""
                }else{
                    doubtView.descriptionLayout.error = ""

                }
            }

        doubtDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        doubtDialog.setView(doubtView)
        doubtDialog.setCancelable(false)
        doubtDialog.show()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                VideoDoubtFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
