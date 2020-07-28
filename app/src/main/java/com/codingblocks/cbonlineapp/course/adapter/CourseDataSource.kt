package com.codingblocks.cbonlineapp.course.adapter

import androidx.paging.PageKeyedDataSource
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CourseDataSource(private val scope: CoroutineScope) :
    PageKeyedDataSource<String, Course>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Course>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getAllCourses("0", "9") }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val courses = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()

                            callback.onResult(courses ?: listOf(), currentOffSet, nextOffSet)
                        }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Course>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getAllCourses(params.key, "9") }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val courses = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (currentOffSet != nextOffSet && nextOffSet != "null") {
                                callback.onResult(courses ?: listOf(), nextOffSet)
                            }
                        }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Course>) {
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
