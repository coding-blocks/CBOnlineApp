package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.models.QuizAttempt
import org.jetbrains.anko.AnkoLogger
import java.util.*

class QuizAttemptListAdapter(internal var context: Context,
                             var list: ArrayList<QuizAttempt>?,
                             var listener: OnItemClickListener
) : ArrayAdapter<QuizAttempt>(context, 0), AnkoLogger {

    override fun getCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItem(i: Int): QuizAttempt? {
        return list!![i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.quiz_attempt_list, parent, false)

            val pos = view!!.findViewById<TextView>(R.id.numberTv)
            val status = view.findViewById<TextView>(R.id.statusTv)
            val time = view.findViewById<TextView>(R.id.dateTv)
            val score = view.findViewById<TextView>(R.id.scoreTv)

            val attemptViewHolder = AttemptViewHolder(pos, status, time, score)
            view.tag = attemptViewHolder
        }
        val e = getItem(position)//to differentiate between filtered list and todo list
        val attemptViewHolder = view.tag as AttemptViewHolder
        attemptViewHolder.posTextView.text = (position + 1).toString() + ""
        attemptViewHolder.statusTextView.text = e?.status!!
        attemptViewHolder.timeTextView.text = e.createdAt
        if (e.result?.score != null) {
            attemptViewHolder.scoreTextView.text = e.result?.score.toString()
        } else {
            attemptViewHolder.scoreTextView.text = "N/A"

        }
        view.setOnClickListener {
            listener.onItemClick(position, e.id!!)
        }

        return view
    }

    class AttemptViewHolder(var posTextView: TextView, var statusTextView: TextView, var timeTextView: TextView, var scoreTextView: TextView)
}
