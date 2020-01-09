package com.codingblocks.cbonlineapp.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * @author pulkit-mac
 */
class StatesRecyclerViewAdapter(wrapped: ListAdapter<out Any, out RecyclerView.ViewHolder>, private val vLoadingView: View?, private val vEmptyView: View?, private val vErrorView: View?) : RecyclerViewAdapterWrapper(wrapped) {

    @IntDef(STATE_NORMAL, STATE_LOADING, STATE_EMPTY, STATE_ERROR)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class State

    @State
    private var state = STATE_NORMAL

    @State
    fun getState() = state


    fun setState(@State state: Int) {
        this.state = state
        wrappedAdapter.notifyDataSetChanged()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        when (state) {
            STATE_LOADING, STATE_EMPTY, STATE_ERROR -> return 1
        }
        return super.getItemCount()
    }


    override fun getItemViewType(position: Int): Int {
        return when (state) {
            STATE_LOADING -> TYPE_LOADING
            STATE_EMPTY -> TYPE_EMPTY
            STATE_ERROR -> TYPE_ERROR
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_LOADING -> return SimpleViewHolder(vLoadingView)
            TYPE_EMPTY -> return SimpleViewHolder(vEmptyView)
            TYPE_ERROR -> return SimpleViewHolder(vErrorView)
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (state) {
            STATE_LOADING -> onBindLoadingViewHolder(holder, position)
            STATE_EMPTY -> onBindEmptyViewHolder(holder, position)
            STATE_ERROR -> onBindErrorViewHolder(holder, position)
            else -> super.onBindViewHolder(holder, position)
        }
    }

    fun onBindErrorViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {}
    fun onBindEmptyViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {}
    fun onBindLoadingViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {}
    class SimpleViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    companion object {
        const val STATE_NORMAL = 0
        const val STATE_LOADING = 1
        const val STATE_EMPTY = 2
        const val STATE_ERROR = 3
        const val TYPE_LOADING = 1000
        const val TYPE_EMPTY = 1001
        const val TYPE_ERROR = 1002
    }

}
