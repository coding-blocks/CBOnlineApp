package com.codingblocks.cbonlineapp.util.extensions

infix fun Boolean.then(block: () -> Unit) = if (this) block() else null

infix fun Boolean.otherwise(block: () -> Unit) = if (this) null else block()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any, K : Any> T.sameAndEqual(n: K): Boolean = ((this.javaClass == n.javaClass) && (this == n))
