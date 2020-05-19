package com.codingblocks.cbonlineapp.mycourse.codechallenge

import com.codingblocks.cbonlineapp.database.CodeChallengeDao
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CoeChallenge
import com.codingblocks.onlineapi.models.detailsClass
import com.codingblocks.onlineapi.models.included
import com.codingblocks.onlineapi.safeApiCall

class CodeChallengeRepository(
    private val codeDao: CodeChallengeDao) {
    suspend fun fetchCodeChallenge(codeId: Int,contestId: String) = safeApiCall { Clients.onlineV2JsonApi.getCodeChallenge(codeId,contestId) }

    fun getOfflineContent(codeId: String): CoeChallenge? {
        val model: CodeChallengeModel = codeDao.getCodeChallengeById(codeId)

        val challenge = CoeChallenge(
            model.title,
            included(
                model.difficulty,
                model.title,
                detailsClass(
                    model.constraints,
                    model.explanation,
                    model.inputFormat,
                    model.sampleInput,
                    model.outputFormat,
                    model.sampleOutput,
                    model.description
                )
            )
        )
        return challenge
    }

    fun isDownloaded(codeId: String): Boolean {
        return codeDao.getCodeChallengeById(codeId)!=null
    }

    suspend fun saveCode(codeId:String, content: CoeChallenge) {
        val newCode: CodeChallengeModel = codeId.let {
            CodeChallengeModel(
                it,
                content.content?.difficulty.toString(),
                content.name,
                content.content?.details?.constraints.toString(),
                content.content?.details?.explanation.toString(),
                content.content?.details?.input_format.toString(),
                content.content?.details?.sample_input.toString(),
                content.content?.details?.output_format.toString(),
                content.content?.details?.sample_output.toString(),
                content.content?.details?.description.toString()
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
