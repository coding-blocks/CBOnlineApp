package com.codingblocks.cbonlineapp.mycourse.player

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao

class VideoPlayerRepository(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao
) {

}
