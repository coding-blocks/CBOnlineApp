package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.ui.BatchesCardUi
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.OnCartItemClickListener
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Runs
import org.jetbrains.anko.AnkoContext
import java.text.SimpleDateFormat
import java.util.Date

class BatchesAdapter(private var batchesData: ArrayList<Runs>?, var listener: OnCartItemClickListener) : RecyclerView.Adapter<BatchesAdapter.BatchViewHolder>() {

    val ui = BatchesCardUi()
    lateinit var context: Context

    fun setData(batchesData: ArrayList<Runs>) {
        this.batchesData = batchesData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        context = parent.context
        return BatchViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int {
        return batchesData?.size ?: 0
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bindView(batchesData!![position])
    }

    inner class BatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(runs: Runs) {
            runs.run {
                ui.runTitle.text = description
                ui.coursePrice.text = "₹ $price"
                if (price != mrp && mrp != "") {
                    ui.courseMrp.text = "₹ $mrp"
                    ui.courseMrp.paintFlags = ui.courseMrp.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG
                }
                val sdf = SimpleDateFormat("MMM dd yyyy")
                var startDate: String? = ""
                var endDate: String? = ""
                var enrollmentDate: String? = ""
                try {
                    startDate = sdf.format(Date(start.toLong() * 1000))
                    endDate = sdf.format(Date(end.toLong() * 1000))
                    enrollmentDate = sdf.format(Date(enrollmentEnd.toLong() * 1000))
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                ui.startTv.text = startDate
                ui.endTv.text = endDate
                ui.enrollmentTv.text = "Enrollment ends $enrollmentDate"
                ui.enrollBtn.setOnClickListener {
                    listener.onItemClick(id, description)
                }
                ui.trialBtn.setOnClickListener {
                    Clients.api.enrollTrial(this.id).enqueue(retrofitCallback { throwable, response ->
                        if (response?.isSuccessful!!) {
                            Components.showconfirmation(context, "trial")
                        }
                    })
                }
            }
        }
    }
}
