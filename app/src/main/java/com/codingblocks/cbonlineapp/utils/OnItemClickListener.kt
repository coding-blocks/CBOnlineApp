package com.codingblocks.cbonlineapp.utils

interface OnItemClickListener {
    fun onItemClick(position: Int,id: String)
}

interface OnCartItemClickListener {
    fun onItemClick(id: String,name:String)
}