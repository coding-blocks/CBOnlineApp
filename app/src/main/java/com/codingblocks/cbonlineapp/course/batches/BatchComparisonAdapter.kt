package com.codingblocks.cbonlineapp.course.batches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Comparision
import kotlinx.android.synthetic.main.item_run_comparision.view.*

class BatchComparisonAdapter(val items: List<Comparision>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(parent.context).inflate(R.layout.item_run_comparision, parent, false)

        val sheetItem = getItem(position)
        with(sheetItem.name) {
            val item = when {
                contains("Recorded") -> R.drawable.ic_recorded
                contains("Quizzes") -> R.drawable.ic_quiz_black
                contains("Completion") -> R.drawable.ic_certificate_black
                contains("Achievement") -> R.drawable.ic_certificate_black
                contains("Resolution") -> R.drawable.ic_doubt
                contains("Support") -> R.drawable.ic_jobs
                contains("Goodies") -> R.drawable.ic_goodies
                contains("Live Mentor") -> R.drawable.ic_live_mentor
                contains("Live Doubt") -> R.drawable.ic_live_ta
                contains("Offline") -> R.drawable.ic_offline
                else -> R.drawable.ic_lite
            }
            view.titleTv.apply {
                setCompoundDrawablesWithIntrinsicBounds(item, 0, 0, 0)
                text = sheetItem.name
            }
            view.lite.isActivated = sheetItem.lite
            view.premium.isActivated = sheetItem.premium
            view.live.isActivated = sheetItem.live
            view.classroom.isActivated = sheetItem.classroom
        }
        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount() = items.size
}
