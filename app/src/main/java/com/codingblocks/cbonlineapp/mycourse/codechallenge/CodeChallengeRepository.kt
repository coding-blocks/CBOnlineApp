package com.codingblocks.cbonlineapp.mycourse.codechallenge

import com.codingblocks.cbonlineapp.database.CodeChallengeDao
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.database.models.CodeDetailsModel
import com.codingblocks.cbonlineapp.database.models.ProblemModel
import com.codingblocks.cbonlineapp.database.models.TimeLimitsModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CoeChallenge
import com.codingblocks.onlineapi.models.CodeDetails
import com.codingblocks.onlineapi.models.Problem
import com.codingblocks.onlineapi.models.TimeLimits
import com.codingblocks.onlineapi.safeApiCall

class CodeChallengeRepository(
    private val codeDao: CodeChallengeDao) {
    suspend fun fetchCodeChallenge(codeId: Int, contestId: String) = safeApiCall { Clients.onlineV2JsonApi.getCodeChallenge(codeId, contestId) }

    fun getOfflineContent(codeId: String): CoeChallenge? {
        val model: CodeChallengeModel = codeDao.getCodeChallengeById(codeId)

        val challenge = CoeChallenge(
            model.title,
            Problem(
                model.difficulty,
                model.title,
                model.content?.image,
                model.content?.status,
                CodeDetails(
                    model.content?.details?.constraints,
                    model.content?.details?.explanation,
                    model.content?.details?.inputFormat,
                    model.content?.details?.sampleInput,
                    model.content?.details?.outputFormat,
                    model.content?.details?.sampleOutput,
                    model.content?.details?.description
                ),
                TimeLimits(
                    model.content?.timeLimits?.cpp?:"",
                    model.content?.timeLimits?.c?:"",
                    model.content?.timeLimits?.py2?:"",
                    model.content?.timeLimits?.py3?:"",
                    model.content?.timeLimits?.js?:"",
                    model.content?.timeLimits?.csharp?:"",
                    model.content?.timeLimits?.java?:""
                )
            )
        )
        return challenge
    }

    fun isDownloaded(codeId: String): Boolean {
        return codeDao.getCodeChallengeById(codeId) != null
    }

    suspend fun saveCode(codeId: String, codeChallenge: CoeChallenge) {
        val newCode: CodeChallengeModel = codeId.let {
            CodeChallengeModel(
                it,
                codeChallenge.content?.difficulty ?: "",
                codeChallenge.name,
                ProblemModel(
                    codeChallenge.content?.name ?: "",
                    codeChallenge.content?.details?.constraints ?: "",
                    codeChallenge.content?.details?.explanation ?: "",
                    CodeDetailsModel(
                        codeChallenge.content?.details?.constraints ?: "",
                        codeChallenge.content?.details?.explanation ?: "",
                        codeChallenge.content?.details?.inputFormat ?: "",
                        codeChallenge.content?.details?.sampleInput ?: "",
                        codeChallenge.content?.details?.outputFormat ?: "",
                        codeChallenge.content?.details?.sampleOutput ?: "",
                        codeChallenge.content?.details?.description ?: ""
                    ),
                    TimeLimitsModel(
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: "",
                        codeChallenge.content?.timelimits?.cpp ?: ""
                    )
                )
            )
        }


        val oldModel: CodeChallengeModel? = codeId.let { codeDao.getCodeChallengeById(it) }
        if (oldModel != null && !oldModel.sameAndEqual(newCode)) {
            codeDao.update(newCode)
        } else {
            codeDao.insertNew(
                newCode
            )
        }
    }
}
