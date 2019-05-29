package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.onlineapi.models.QuizAttempt
import kotlinx.android.synthetic.main.quiz_attempt_list.view.*

class QuizAttemptListAdapter(val dataset: ArrayList<QuizAttempt>, val clickListner: (QuizAttempt) -> Unit) :
    RecyclerView.Adapter<QuizAttemptListAdapter.QuizAttemptViewHolder>() {

    class QuizAttemptViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(attempt: QuizAttempt, clickListner: (QuizAttempt) -> Unit) {
            view.numberTv.text = (adapterPosition + 1).toString()
            view.statusTv.text = attempt.status
            view.dateTv.text = formatDate(attempt.createdAt!!)
            if (attempt.result?.score != null)
                view.scoreTv.text = attempt.result?.score.toString()
            else
                view.scoreTv.text = "N/A"
            view.setOnClickListener {
                clickListner(attempt)
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
        holder.bind(dataset[position], clickListner)
    }

    // return size of the current data
    override fun getItemCount() = dataset.size
}
