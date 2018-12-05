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
                      @Query("Signature") signature: String = "F+oM2Bwkkky7qxDKzPl/hIFLAajrAX6QYc+54aTJSlF/P4JcyeAckqHtDhDG+8SGOoiL4K8kbp0PP4Lmlpp8buijLEynAfy+Sb5klaAL2wH03dJjxNHWud78YmXPpjfrcCrk3a3dmxlPIQFJvkut/fXmmeDzDz6ksiuo6dvnSaTTRj4uwo0iWfOYe8RQWVb1AeKkWWmDgoMhNKtFwUJO3VH/uKS2ourBT2ElFbkk9+dMOgpRzRkzenqWUn0/hFIzv6qIAXKpn0jQrq0/ODrisMscnAwgjSYrdEQipzhU25oc7YWXcLf0h4rRyAeI8iHGiCxZsCdWYiskgnITDu0EJg==",
                      @Query("Policy") policy: String = "eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9kMXFmMG96c3M0OTR4di5jbG91ZGZyb250Lm5ldC81OTQ5ZGNkYi1hMTAwLTQxNDctOTQxMS1lYjVlODAzNjg0NDFJbnRyb3RzLyoiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE1NDQwMTUwODN9fX1dfQ=="): Call<ResponseBody>
}
