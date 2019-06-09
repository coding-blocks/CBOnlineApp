package com.codingblocks.cbonlineapp.bindings

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel

@BindingAdapter("setVideoPlayerAdapter")
fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

    val itemDecorator = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
    recyclerView.adapter = adapter
    itemDecorator.setDrawable(recyclerView.context.resources.getDrawable(R.drawable.divider))
    recyclerView.addItemDecoration(itemDecorator)
}

@BindingAdapter(value = ["pos", "model"])
fun bindEditText(editText: EditText, pos: Int, model: VideoPlayerViewModel) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            model.notesObservables.bodyTvText[pos].set(s.toString())
        }
    })
}
