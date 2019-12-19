package com.codingblocks.cbonlineapp.dashboard.mycourses

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.safeApiCall
import kotlin.collections.ArrayList

class DashboardMyCoursesRepository {

    suspend fun fetchMyCourses() = safeApiCall {
        Clients.onlineV2JsonApi.getMyCourses()
    }


    suspend fun insertCourses(courses: List<Runs>) {
        courses.forEach {
            //            doubtsDao.insert(DoubtsModel(
//                dbtUid = it.id,
//                title = it.title,
//                body = it.body,
//                contentId = it.content?.id ?: "",
//                status = it.status,
//                runAttemptId = it.runAttempt?.id ?: "",
//                discourseTopicId = it.discourseTopicId,
//                conversationId = it.conversationId,
//                createdAt = it.createdAt
//            ))
        }
    }

//    fun getDoubtsByCourseRun(type: String): LiveData<List<DoubtsModel>> {
//        return when (type) {
//            LIVE -> doubtsDao.getLiveDoubts("44872")
//            RESOLVED -> doubtsDao.getResolveDoubts("44872")
//            else -> doubtsDao.getDoubts("44872")
//        }
//
//    }
}
