package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.BR
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.NotesModel

import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel

class VideosNotesAdapter(
    private val viewModel: VideoPlayerViewModel
) : RecyclerView.Adapter<VideosNotesAdapter.NotesViewHolder>() {

    var notesData: ArrayList<NotesModel> = ArrayList()

    fun setData(notesData: ArrayList<NotesModel>) {
        this.notesData = notesData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.item_notes, parent, false)

        return NotesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notesData.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindView(viewModel, position)
    }

    fun getNoteAt(position: Int) = notesData[position]

    class NotesViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(viewModel: VideoPlayerViewModel, position: Int) {
            viewModel.fetchItemsAt(position)
            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }
    }
}
