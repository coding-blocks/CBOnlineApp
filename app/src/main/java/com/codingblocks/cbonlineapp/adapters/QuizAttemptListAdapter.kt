package com.codingblocks.cbonlineapp.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.onlineapi.models.QuizAttempt

class QuizAttemptListAdapter(private val dataset: ArrayList<QuizAttempt>) :
    RecyclerView.Adapter<QuizAttemptListAdapter.QuizAttemptViewHolder>() {

    var onItemClick: ((QuizAttempt)->Unit) ?= null

    inner class QuizAttemptViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var numberTextView: TextView? = null
        var dateTextView: TextView? = null
        var scoreTextView: TextView? = null
        var statusTextView: TextView? = null

        init {
            numberTextView = view.findViewById(R.id.numberTv)
            dateTextView = view.findViewById(R.id.dateTv)
            scoreTextView = view.findViewById(R.id.scoreTv)
            statusTextView = view.findViewById(R.id.statusTv)
            view.setOnClickListener {
                onItemClick?.invoke(dataset[adapterPosition])
            }
        }
    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizAttemptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quiz_attempt_list, parent, false)
        return QuizAttemptViewHolder(view)
    }

    // Replace the contents of view
    override fun onBindViewHolder(holder: QuizAttemptViewHolder, position: Int) {
        val currentAttempt = dataset[position]
        holder.numberTextView?.text = (position + 1).toString()
        holder.statusTextView?.text = currentAttempt.status
        holder.dateTextView?.text = formatDate(currentAttempt.createdAt!!)
        if (currentAttempt.result?.score != null)
            holder.scoreTextView?.text = currentAttempt.result?.score.toString()
        else
            holder.scoreTextView?.text = "N/A"
    }

    // return size of the current data
    override fun getItemCount() = dataset.size
}
