package com.codingblocks.cbonlineapp.jobs.jobdetails

import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.util.extensions.getDate
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Jobs
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author aggarwalpulkit596
 */
class JobRepository(
    private val jobsDao: JobsDao
) {
    suspend fun fetchJobs() = safeApiCall { Clients.onlineV2JsonApi.getJobs(getDate(), getDate()) }

    suspend fun insertJobs(jobs: List<Jobs>) {
        jobs.forEach { job ->
            with(job) {
                val company = withContext(Dispatchers.IO) {
                    Clients.onlineV2JsonApi.getCompany(company?.id ?: "")
                }.body()
                jobsDao.insert(JobsModel(
                    id,
                    coverImage,
                    ctc,
                    deadline,
                    description,
                    eligibility,
                    experience,
                    location,
                    postedOn,
                    type,
                    title,
                    Companies(
                        company?.id ?: "",
                        company?.name ?: "",
                        company?.logo ?: "",
                        company?.description ?: "",
                        company?.website ?: ""
                    ),
                    job.courses ?: arrayListOf()
                ))
            }
        }
    }

    fun getJobs() = jobsDao.getAllJobs()
}
