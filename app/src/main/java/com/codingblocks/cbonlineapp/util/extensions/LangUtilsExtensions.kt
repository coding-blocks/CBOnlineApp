package com.codingblocks.cbonlineapp.util.extensions

import com.google.gson.Gson

infix fun Boolean.then(block: () -> Unit) = if (this) block() else null

infix fun Boolean.otherwise(block: () -> Unit) = if (this) null else block()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any, K : Any> T.sameAndEqual(n: K): Boolean = ((this.javaClass == n.javaClass) && (this == n))

inline fun <reified T : Any> String.deserializeNoteFromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

inline fun <reified T : Any> T.serializeToJson(): String {
    return Gson().toJson(this)
}
