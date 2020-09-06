package com.codingblocks.cbonlineapp.mycourse.content.codechallenge

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.CodeChallengeDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.database.models.CodeDetailsModel
import com.codingblocks.cbonlineapp.database.models.ProblemModel
import com.codingblocks.cbonlineapp.database.models.TimeLimitsModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.CodeChallenge
import com.codingblocks.onlineapi.models.CodeDetails
import com.codingblocks.onlineapi.models.Problem
import com.codingblocks.onlineapi.models.TimeLimits
import com.codingblocks.onlineapi.safeApiCall

class CodeChallengeRepository(
    private val codeDao: CodeChallengeDao,
    private val bookmarkDao: BookmarkDao
) {
    suspend fun fetchCodeChallenge(codeId: Int, contestId: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getCodeChallenge(codeId, contestId) }

    suspend fun getOfflineContent(codeId: String): CodeChallenge? {
        val model: CodeChallengeModel? = codeDao.getCodeChallengeById(codeId)

        val challenge = with(model!!) {
            CodeChallenge(
                name = title,
                content = Problem(
                    difficulty,
                    title,
                    content?.image,
                    content?.status,
                    with(content?.details!!) {
                        CodeDetails(
                            constraints,
                            explanation,
                            inputFormat,
                            sampleInput,
                            outputFormat,
                            sampleOutput,
                            description
                        )
                    },
                    with(content.timeLimits) {
                        TimeLimits(
                            cpp,
                            c,
                            py2,
                            py3,
                            js,
                            csharp,
                            java
                        )
                    }
                )
            )
        }
        return challenge
    }

    suspend fun isDownloaded(codeId: String): Boolean? {
        return codeDao.getCodeChallengeById(codeId) != null
    }

    suspend fun saveCode(codeId: String, codeChallenge: CodeChallenge) {
        val newCode: CodeChallengeModel = codeId.let {

            with(codeChallenge.content!!) {
                CodeChallengeModel(
                    it,
                    this.difficulty,
                    name,
                    with(details!!) {
                        ProblemModel(
                            name,
                            image ?: "",
                            status ?: "",
                            CodeDetailsModel(
                                constraints ?: "",
                                explanation ?: "",
                                inputFormat ?: "",
                                sampleInput ?: "",
                                outputFormat ?: "",
                                sampleOutput ?: "",
                                description ?: ""
                            ),

                            with(timelimits!!) {
                                TimeLimitsModel(
                                    cpp,
                                    c,
                                    py2,
                                    py3,
                                    js,
                                    csharp,
                                    java
                                )
                            }
                        )
                    }
                )
            }
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

    suspend fun getCodeId(ccid: String) = codeDao.getCodeChallenge(ccid)

    suspend fun removeBookmark(bookmarkUid: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.deleteBookmark(bookmarkUid) }

    fun getBookmark(contentId: String) = bookmarkDao.getBookmarkById(contentId)

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(
            BookmarkModel(
                bookmark.id ?: "",
                bookmark.runAttempt?.id ?: "",
                bookmark.content?.id ?: "",
                bookmark.section?.id ?: "",
                bookmark.createdAt ?: ""
            )
        )
    }

    suspend fun addBookmark(bookmark: Bookmark) = safeApiCall { CBOnlineLib.onlineV2JsonApi.addBookmark(bookmark) }

    fun deleteBookmark(id: String) = bookmarkDao.deleteBookmark(id)
}
