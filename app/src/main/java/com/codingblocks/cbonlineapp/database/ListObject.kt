package com.codingblocks.cbonlineapp.database

abstract class ListObject {
    abstract fun getType(): Int

    companion object {
        val TYPE_SECTION = 0
        val TYPE_CONTENT = 1
    }
}
