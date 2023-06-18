package com.app.rivisio.data.network

import com.app.rivisio.data.db.entity.Purchase
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiHelper {
    suspend fun signup(body: Map<String, String>): Response<JsonElement>
    suspend fun tags(token: String?, userId: Int): Response<JsonElement>
    suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>,
    ): Response<JsonElement>

    suspend fun uploadImages(
        userId: RequestBody,
        token: RequestBody,
        file: MultipartBody.Part,
        fileName: RequestBody
    ): Response<JsonElement>

    suspend fun addTopic(
        token: String?,
        userId: Int,
        topic: Topic
    ): Response<JsonElement>

    suspend fun getTopics(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun getTopicsData(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun getTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun editTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int,
        topicFromServer: TopicFromServer
    ): Response<JsonElement>

    suspend fun getAllTopics(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun reviseTopic(
        topicId: Int,
        body: Map<String, String>
    ): Response<JsonElement>

    suspend fun getUser(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun getUserStats(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun limitcheck(
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun getTopicByDate(
        date: String,
        token: String?,
        userId: Int
    ): Response<JsonElement>

    suspend fun saveSubscription(
        purchase: Purchase
    ): Response<JsonElement>
}