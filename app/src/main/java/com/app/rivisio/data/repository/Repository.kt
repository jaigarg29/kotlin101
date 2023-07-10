package com.app.rivisio.data.repository

import com.app.rivisio.data.db.DbHelper
import com.app.rivisio.data.db.entity.Purchase
import com.app.rivisio.data.network.ApiHelper
import com.app.rivisio.data.prefs.PreferencesHelper
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class Repository constructor(
    private val apiHelper: ApiHelper,
    private val preferencesHelper: PreferencesHelper,
    private val dbHelper: DbHelper
) {

    fun getUserState(): UserState? {
        return preferencesHelper.getUserState()
    }

    fun setUserState(userState: UserState) {
        preferencesHelper.setUserState(userState)
    }

    fun getUserEmail(): String? {
        return preferencesHelper.getUserEmail()
    }

    fun setUserEmail(email: String) {
        preferencesHelper.setUserEmail(email)
    }

    fun getName(): String? {
        return preferencesHelper.getName()
    }

    fun setName(name: String) {
        preferencesHelper.setName(name)
    }

    fun getFirstName(): String? {
        return preferencesHelper.getFirstName()
    }

    fun setFirstName(firstName: String) {
        preferencesHelper.setFirstName(firstName)
    }

    fun getLastName(): String? {
        return preferencesHelper.getLastName()
    }

    fun setLastName(lastName: String) {
        preferencesHelper.setLastName(lastName)
    }

    fun getMobile(): String? {
        return preferencesHelper.getMobile()
    }

    fun setMobile(mobile: String) {
        preferencesHelper.setMobile(mobile)
    }

    fun getUserLoggedIn(): Boolean {
        return preferencesHelper.getUserLoggedIn()
    }

    fun setUserLoggedIn() {
        preferencesHelper.setUserLoggedIn()
    }

    fun getProfilePicture(): String? {
        return preferencesHelper.getProfilePicture()
    }

    fun setProfilePicture(profilePictureUrl: String) {
        preferencesHelper.setProfilePicture(profilePictureUrl)
    }

    fun getAccessToken(): String? {
        return preferencesHelper.getAccessToken()
    }

    fun setAccessToken(accessToken: String) {
        preferencesHelper.setAccessToken(accessToken)
    }

    fun getUserId(): Int {
        return preferencesHelper.getUserId()
    }

    fun setUserId(userId: Int) {
        preferencesHelper.setUserId(userId)
    }

    suspend fun signup(body: Map<String, String>) = apiHelper.signup(body)

    suspend fun tags(token: String?, userId: Int) = apiHelper.tags(token, userId)

    suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>,
    ) = apiHelper.addTag(token, userId, body)

    suspend fun uploadImages(
        userId: RequestBody,
        token: RequestBody,
        file: MultipartBody.Part,
        fileName: RequestBody
    ): Response<JsonElement> = apiHelper.uploadImages(userId, token, file, fileName)

    suspend fun addTopic(
        token: String?,
        userId: Int,
        topic: Topic
    ) = apiHelper.addTopic(token, userId, topic)

    suspend fun getTopics(
        token: String?,
        userId: Int
    ) = apiHelper.getTopics(token, userId)

    suspend fun getTopicsData(
        token: String?,
        userId: Int
    ) = apiHelper.getTopicsData(token, userId)

    suspend fun getAllTopics(
        token: String?,
        userId: Int
    ) = apiHelper.getAllTopics(token, userId)

    suspend fun getTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int
    ) = apiHelper.getTopicDetails(topicId, token, userId)

    suspend fun editTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int,
        topicFromServer: TopicFromServer
    ) = apiHelper.editTopicDetails(topicId, token, userId, topicFromServer)

    suspend fun reviseTopic(
        topicId: Int,
        body: Map<String, String>
    ) = apiHelper.reviseTopic(topicId, body)

    suspend fun getUser(
        token: String?,
        userId: Int
    ) = apiHelper.getUser(token, userId)

    fun setUserLoggedOut() {
        preferencesHelper.setUserLoggedOut()
    }

    fun isNotificationEnabled(): Boolean {
        return preferencesHelper.isNotificationEnabled()
    }

    fun setNotificationSetting(enable: Boolean) {
        preferencesHelper.setNotificationSetting(enable)
    }

    fun getNotificationTime(): String {
        return preferencesHelper.getNotificationTime()
    }

    fun setNotificationTime(time: String) {
        preferencesHelper.setNotificationTime(time)
    }

    suspend fun getUserStats(
        token: String?,
        userId: Int
    ) = apiHelper.getUserStats(token, userId)

    suspend fun limitcheck(
        token: String?,
        userId: Int
    ) = apiHelper.limitcheck(token, userId)

    suspend fun getTopicByDate(
        date: String,
        token: String?,
        userId: Int
    ) = apiHelper.getTopicByDate(date, token, userId)

    suspend fun insertPurchase(purchase: Purchase): Long {
        return dbHelper.insertPurchase(purchase)
    }

    suspend fun getPurchase(): List<Purchase> {
        return dbHelper.getPurchase()
    }

    fun deletePurchase(purchase: Purchase): Int {
        return dbHelper.deletePurchase(purchase)
    }

    suspend fun saveSubscription(
        purchase: Purchase
    ) = apiHelper.saveSubscription(purchase)

    suspend fun deleteTopic(topicId: Int, token: String?, userId: Int) = apiHelper.deleteTopic(topicId, token, userId)

}