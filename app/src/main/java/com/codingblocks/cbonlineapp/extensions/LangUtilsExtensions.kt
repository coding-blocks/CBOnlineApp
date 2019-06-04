package com.codingblocks.cbonlineapp.extensions

infix fun Boolean.then(block: () -> Unit) = if (this) block() else null

infix fun Boolean.otherwise(block: () -> Unit) = if (this) null else block()
