package com.codingblocks.cbonlineapp.auth.onboarding

/**
 * Represents an item in intro screen
 * @property title The title of the intro screen item string resId
 * @property description The description of intro screen item string resId
 * @property image The image drawable resId of intro screen item
 */
data class Intro(
    val title: Int,
    val description: Int,
    val image: Int
)
