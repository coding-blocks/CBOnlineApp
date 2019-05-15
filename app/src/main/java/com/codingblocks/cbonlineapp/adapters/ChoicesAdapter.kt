package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Question
import kotlinx.android.synthetic.main.choice_numbers_layout.view.*
import kotlinx.android.synthetic.main.item_carousel.view.*

class ChoicesAdapter(private val listener:ChoiceClickListener) : RecyclerView.Adapter<ChoicesAdapter.MyViewHolder>() {

    lateinit var context : Context
    private var numbers = ArrayList<Int>()
    private var question = ArrayList<Question>()

    fun setdata(question: ArrayList<Question>, numbers:ArrayList<Int>){
        this.numbers = numbers
        this.question = question
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.choice_numbers_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return question.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.numberbtn.text = (position+1).toString()

        Log.d("marks_rv","${numbers.size}")

        holder.itemView.numberbtn.setOnClickListener {
            Log.d("marked_rv","${position}")
            listener.onChoiceClicked(position)
        }

        for (i in numbers){
            if (position == i){
                holder.itemView.numberbtn.background = context.getDrawable(R.drawable.button_rounded_green_background)
            }
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface ChoiceClickListener{
        fun onChoiceClicked(pos: Int)
    }

}
