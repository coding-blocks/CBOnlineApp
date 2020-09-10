package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.CourseFeatureNetworkModel
import com.codingblocks.onlineapi.models.CourseNetworkModel
import com.codingblocks.onlineapi.models.InstructorNetworkModel
import com.codingblocks.onlineapi.models.ProjectsNetworkModel
import com.codingblocks.onlineapi.models.SectionNetworkModel
import com.codingblocks.onlineapi.models.WishlistNetworkModel
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApiService {

    @GET("courses/{id}")
    suspend fun getCourse(
        @Path("id") id: String
    ): Response<CourseNetworkModel>

    @GET("projects/{id}")
    suspend fun getProject(
        @Path("id") id: String,
        @Query("exclude") query: String = "course.*"
    ): Response<ProjectsNetworkModel>

    @GET("courses")
    suspend fun getRecommendedCourses(
        @Query("exclude") query: String = "ratings,instructors.*,jobs,runs.*",
        @Query("filter[recommended]") recommended: String = "true",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: Int = 12,
        @Query("page[offset]") offset: Int = 0,
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Response<List<CourseNetworkModel>>

    @GET("courses")
    suspend fun getAllCourses(
        @Query("page[offset]") offset: String,
        @Query("page[limit]") limit: String = "20",
        @Query("exclude") query: String = "ratings,instructors.*,jobs,runs.*",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Response<JSONAPIDocument<List<CourseNetworkModel>>>

    @GET("courses")
    suspend fun findCourses(
        @Query("exclude") exclude: String = "ratings,instructors.*,jobs",
        @Query("filter[title][\$iLike]") query: String,
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: Int = 100,
        @Query("include") include: String = "instructors,runs"
    ): Response<List<CourseFeatureNetworkModel>>


    @GET("instructors/{id}")
    suspend fun getInstructor(
        @Path("id") id: String
    ): Response<InstructorNetworkModel>

    @GET("instructors/")
    suspend fun getAllInstructors(): Response<List<InstructorNetworkModel>>

    @GET("sections/{id}")
    suspend fun getSections(
        @Path("id") id: String,
        @Query("exclude") query: String = "contents.*",
        @Query("include") include: String = "contents",
        @Query("sort") sort: String = "content.section_content.order"
    ): Response<SectionNetworkModel>

    @GET("user_course_wishlists")
    suspend fun getWishlist(
        @Query("page[offset]") offset: String? = "0",
        @Query("exclude") exclude: String = "course.*",
        @Query("include") include: String = "course",
        @Query("page[limit]") page: String = "3"
    ): Response<JSONAPIDocument<List<WishlistNetworkModel>>>

    @POST("user_course_wishlists")
    suspend fun addWishlist(
        @Body params: WishlistNetworkModel
    ): Response<WishlistNetworkModel>

    @GET("courses/{id}/relationships/user_course_wishlist")
    suspend fun checkIfWishlisted(
        @Path("id") id: String
    ): Response<WishlistNetworkModel>

    @DELETE("user_course_wishlists/{id}")
    suspend fun removeWishlist(
        @Path("id") id: String
    ): Response<WishlistNetworkModel>
}
