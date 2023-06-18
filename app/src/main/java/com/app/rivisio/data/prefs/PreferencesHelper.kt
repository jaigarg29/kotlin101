package com.app.rivisio.data.prefs

interface PreferencesHelper {

    fun getAccessToken(): String?

    fun setAccessToken(accessToken: String)

    fun getUserEmail(): String?

    fun setUserEmail(email: String)

    fun getUserId(): Int

    fun setUserId(userId: Int)

    fun getName(): String?

    fun setName(name: String)
    fun getFirstName(): String?
    fun setFirstName(name: String)

    fun getLastName(): String?
    fun setLastName(name: String)

    fun getMobile(): String?
    fun setMobile(name: String)

    fun getUserLoggedIn(): Boolean

    fun setUserLoggedIn()

    fun setUserLoggedOut()

    fun getUserState(): UserState?

    fun setUserState(userState: UserState)
    fun getProfilePicture(): String?
    fun setProfilePicture(profilePictureUrl: String)
    fun isNotificationEnabled(): Boolean
    fun setNotificationSetting(enable: Boolean)
    fun getNotificationTime(): String
    fun setNotificationTime(time: String)
}