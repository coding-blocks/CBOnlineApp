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
                      @Query("Key-Pair-Id") key_pair_id: String = "APKAIX3JJRW7RHDSNHGA",
                      @Query("Signature") signature: String = "cKCAUOlaWIqV3xdJNB50k2pj/XImjKZcH3fqm26MjtQ810AJ0X0nvnXI862YoWAS4plaEq7CxCUjT7CFueAu27+P4vi362wd9Od8FfBD29o+EaIVYQwu4audv3AGqR9nhOn1wqHyhAgTZqBZRf5kJXysuGb5kRT+O2okm79CtVJFOoXEtAVlrJEiQE1g1DLZ8sH5J406qMwQsM6ItscehWYqfbfH2D71ijqVNlu66Rn+6oz2eqUVE0Y54Mu5Cg17aQ95bWIYTXAlWC5D0p/jBGwjTa3L+0Ed4gaVaHjsOlpmSZjNC812cq1NQmVJvKnDytvDXfkfAc5D9n+7FKYMtQ==",
                      @Query("Policy") policy: String = "eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9kMXFmMG96c3M0OTR4di5jbG91ZGZyb250Lm5ldC8xZGIyZTM3Yy0wZmM3LTQzOGYtYjk4NS0wY2IwNzU4MThjYjVWaWV3Z3JvdXBtcDQvKiIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTU0NDA0Mjc3Mn19fV19"): Call<ResponseBody>



}
