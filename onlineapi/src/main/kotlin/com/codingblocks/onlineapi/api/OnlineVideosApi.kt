package com.codingblocks.onlineapi.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
   send the video_url that you receive in ContentLectureType
   fileType is the type of file you plan on downloading, start with index.m3u8 then video.m3u8 and video.key
   Store them in internal storage, inside Android/Data/com.codingblocks.cbonlineapp/files/data/{video_url}

   Hit this endpoint again by traversing through the returned ArrayList in MediaUtils.getCourseDownloadUrls()
    */
interface OnlineVideosApi {


    @GET("{videoUrl}/{fileType}")
    fun getVideoFiles(@Path("videoUrl") videoUrl: String,
                      @Path("fileType") fileName: String,
                      @Query("Key-Pair-Id") key_pair_id: String = "",
                      @Query("Signature") signature: String = "",
                      @Query("Policy") policy: String = ""): Call<ResponseBody>


}
