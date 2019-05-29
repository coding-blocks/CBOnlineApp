package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Leaderboard
import org.jetbrains.anko.AnkoLogger

class LeaderboardListAdapter(internal var context: Context,
                             var list: ArrayList<Leaderboard>?
) : ArrayAdapter<Leaderboard>(context, 0), AnkoLogger {

    override fun getCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItem(i: Int): Leaderboard? {
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
            val name = view.findViewById<TextView>(R.id.statusTv)
            val college = view.findViewById<TextView>(R.id.dateTv)
            val score = view.findViewById<TextView>(R.id.scoreTv)

            val attemptViewHolder = ViewHolder(pos, name, college, score)
            view.tag = attemptViewHolder
        }
        val e = getItem(position) // to differentiate between filtered list and todo list
        val attemptViewHolder = view.tag as ViewHolder
        attemptViewHolder.posTextView.text = (position + 1).toString() + ""
        attemptViewHolder.nameTextView.text = e?.userName!!
        attemptViewHolder.collegeTextView.text = e.collegeName
        attemptViewHolder.scoreTextView.text = e.score.toString() ?: "N/a"

        return view
    }

    class ViewHolder(var posTextView: TextView, var nameTextView: TextView, var collegeTextView: TextView, var scoreTextView: TextView)
}
