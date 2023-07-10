package com.app.rivisio.data.network

import com.app.rivisio.data.db.entity.Purchase
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/users/signup")
    suspend fun signup(
        @Body body: Map<String, String>
    ): Response<JsonElement>

    @GET("/tag/all")
    suspend fun tags(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
    ): Response<JsonElement>

    @POST("/tag/add")
    suspend fun addTag(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
        @Body body: Map<String, String>,
    ): Response<JsonElement>

    @Multipart
    @POST("/users/uploadimage")
    suspend fun uploadImages(
        @Part(CUST_ID) userId: RequestBody,
        @Part(TOKEN) token: RequestBody,
        @Part file: MultipartBody.Part,
        @Part(FILENAME) fileName: RequestBody
    ): Response<JsonElement>

    @POST("/topics/add")
    suspend fun addTopic(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
        @Body topic: Topic
    ): Response<JsonElement>

    @GET("/topics/all")
    suspend fun getTopics(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int
    ): Response<JsonElement>

    // view topics data for today,missed and upcoming
    @GET("/topics/list/topicdata")
    suspend fun getTopicsData(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int
    ): Response<JsonElement>

    @GET("/topics/all")
    suspend fun getAllTopics(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int
    ): Response<JsonElement>

    @GET("/users/view")
    suspend fun getUser(
        @Query(TOKEN) token: String?,
        @Query(CUST_ID) userId: Int
    ): Response<JsonElement>

    @GET("/users/stats")
    suspend fun getUserStats(
        @Query(TOKEN) token: String?,
        @Query(CUST_ID) userId: Int
    ): Response<JsonElement>

    @GET("/users/limitcheck")
    suspend fun limitcheck(
        @Query(TOKEN) token: String?,
        @Query(CUST_ID) userId: Int
    ): Response<JsonElement>

    @GET("/topics/{topicId}")
    suspend fun getTopicDetails(
        @Path(TOPIC_ID) topicId: Int,
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int
    ): Response<JsonElement>

    @GET("/topics/allbydate")
    suspend fun getTopicByDate(
        @Query(P_DATE) date: String,
        @Query(TOKEN) token: String?,
        @Query(CUST_ID) userId: Int
    ): Response<JsonElement>

    @PUT("/topics/{topicId}")
    suspend fun editTopicDetails(
        @Path(TOPIC_ID) topicId: Int,
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
        @Body topicFromServer: TopicFromServer
    ): Response<JsonElement>

    @PUT("/topicrevision/{topicId}")
    suspend fun reviseTopic(
        @Path(TOPIC_ID) topicId: Int,
        @Body body: Map<String, String>
    ): Response<JsonElement>

    @POST("/users/subscription")
    suspend fun saveSubscription(
        @Body purchase: Purchase
    ): Response<JsonElement>

    @PUT("/topics/{topicId}/delete")
    suspend fun deleteTopic(
        @Path(TOPIC_ID) topicId: Int,
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int
    ): Response<JsonElement>

}