package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.models.Choice
import kotlinx.android.synthetic.main.quiz_single_option.view.*

class QuizChoiceAdapter(private var choices: ArrayList<Choice>, private var listener: OnItemClickListener) : RecyclerView.Adapter<QuizChoiceAdapter.ChoiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder = ChoiceViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.quiz_single_option, parent, false))

    override fun getItemCount(): Int {
        return choices.size
    }

    override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
        holder.bindView(choices[position], listener)

    }

    class ChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(choice: Choice, listener: OnItemClickListener) {
            itemView.optionTitle.text = choice.title
            itemView.numberTv.setOnClickListener {
                listener.onItemClick(position, choice.id!!)
            }
        }
    }
}
