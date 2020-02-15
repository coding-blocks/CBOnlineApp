package com.codingblocks.cbonlineapp.mycourse.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.OnItemClickListener
import com.codingblocks.onlineapi.models.Choice
import kotlinx.android.synthetic.main.quiz_single_option.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor

class QuizChoiceAdapter(
    private var choices: ArrayList<Choice>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<QuizChoiceAdapter.ChoiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder =
        ChoiceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.quiz_single_option, parent, false)
        )

    override fun getItemCount(): Int = choices.size

    override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
        holder.bindView(choices[position], listener, position)
    }

    inner class ChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {
        fun bindView(choice: Choice, listener: OnItemClickListener, position: Int) {
            with(itemView) {
                optionTitle.text = choice.title
                choice.correct?.run {
                    if (this) {
                        // If this is correct
                        markedImgView.background = getDrawable(context, R.drawable.ic_correct_circle_small)
                        optionTitle.textColor = getColor(context, R.color.freshGreen)
                    } else if (choice.marked) {
                        // If this is not correct, but has been marked
                        markedImgView.background = getDrawable(context, R.drawable.ic_incorrect)
                        optionTitle.textColor = getColor(context, R.color.orangish)
                    }
                } ?: run {
                    if (choice.marked) {
                        // If this is marked
                        optionTitle.textColor = getColor(context, R.color.white)
                        markedImgView.background = getDrawable(context, R.drawable.ic_incorrect)
                    } else {
                        optionTitle.textColor = getColor(context, R.color.white)
                        markedImgView.background = getDrawable(context, R.drawable.ic_circle)
                    }
                }

                setOnClickListener {
                    listener.onItemClick(position, choice.id)
                    this@QuizChoiceAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
