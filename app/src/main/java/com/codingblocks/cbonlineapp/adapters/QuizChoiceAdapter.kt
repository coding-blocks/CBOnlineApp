package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.models.Choice
import kotlinx.android.synthetic.main.quiz_single_option.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor

class QuizChoiceAdapter(private var choices: ArrayList<Choice>, private var listener: OnItemClickListener) : RecyclerView.Adapter<QuizChoiceAdapter.ChoiceViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder = ChoiceViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.quiz_single_option, parent, false)).apply {
        context = parent.context

    }

    override fun getItemCount(): Int {
        return choices.size
    }

    override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
        holder.bindView(choices[position], listener, context, position)

    }

    class ChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {
        fun bindView(choice: Choice, listener: OnItemClickListener, context: Context, position: Int) {
            itemView.optionTitle.text = choice.title
            if (choice.correct != null) {
                if (choice.marked && choice.correct!!) {
                    itemView.numberTv.background = context.getDrawable(R.drawable.ic_status_done)
                    itemView.optionTitle.textColor = context.resources.getColor(R.color.green)
                } else {
                    itemView.numberTv.background = context.getDrawable(R.drawable.ic_youtube_video)
                    itemView.optionTitle.textColor = context.resources.getColor(R.color.colorPrimaryDark)
                }
                if (choice.correct!!) {
                    itemView.numberTv.background = context.getDrawable(R.drawable.ic_status_done)
                    itemView.optionTitle.textColor = context.resources.getColor(R.color.green)
                }
            } else if (choice.marked && choice.correct == null) {
                itemView.numberTv.background = context.getDrawable(R.drawable.youtube)
                itemView.optionTitle.textColor = context.resources.getColor(R.color.colorPrimaryDark)

            }
            itemView.numberTv.setOnClickListener {
                listener.onItemClick(position, choice.id!!)
            }

        }
    }
}
