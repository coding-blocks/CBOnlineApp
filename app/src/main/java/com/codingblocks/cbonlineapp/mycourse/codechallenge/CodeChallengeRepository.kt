package com.codingblocks.cbonlineapp.mycourse.codechallenge

import com.codingblocks.cbonlineapp.database.CodeChallengeDao
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Code_Challenge
import com.codingblocks.onlineapi.models.detailsClass
import com.codingblocks.onlineapi.models.included
import com.codingblocks.onlineapi.safeApiCall

class CodeChallengeRepository(
    private val codeDao: CodeChallengeDao) {
    suspend fun fetchCodeChallenge(codeId: Int,contestId: String) = safeApiCall { Clients.onlineV2JsonApi.getCodeChallenge(codeId,contestId) }

    fun getOfflineContent(codeId: String): Code_Challenge? {
        val model: CodeChallengeModel = codeDao.getCodeChallengeById(codeId)

        val challenge = Code_Challenge(
            model.title,
            included(
                model.difficulty,
                model.title,
                detailsClass(
                    model.constraints,
                    model.explanation,
                    model.input_format,
                    model.sample_input,
                    model.output_format,
                    model.sample_output,
                    model.description
                )
            )
        )
        return challenge
    }

    fun isDownloaded(codeId: String): Boolean {
        return codeDao.getCodeChallengeById(codeId)!=null
    }

    suspend fun saveCode(codeId:String, content: Code_Challenge) {
        val newCode: CodeChallengeModel = codeId.let {
            CodeChallengeModel(
                it,
                content.content!!.difficulty,
                content.name,
                content.content!!.details!!.constraints!!,
                content.content!!.details!!.explanation!!,
                content.content!!.details!!.input_format!!,
                content.content!!.details!!.sample_input!!,
                content.content!!.details!!.output_format!!,
                content.content!!.details!!.sample_output!!,
                content.content!!.details!!.description!!
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
