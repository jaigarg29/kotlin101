package com.app.rivisio.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.network.*
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _isUserLoggedIn = MutableLiveData<NetworkResult<JsonElement>>()
    val isUserLoggedIn: LiveData<NetworkResult<JsonElement>>
        get() = _isUserLoggedIn

    fun setUserDetails(user: User) {
        viewModelScope.launch {

            _isUserLoggedIn.value = NetworkResult.Loading

            val requestBody = createRequestBody(user)

            val networkResponse = handleApi { repository.signup(requestBody) }

            if (networkResponse is NetworkResult.Success) {

                try {
                    saveUserData(networkResponse, user)
                    repository.setUserState(UserState.LOGGED_IN)
                    repository.setUserLoggedIn()

                    _isUserLoggedIn.value = networkResponse

                } catch (e: Exception) {
                    Timber.e("Json parsing issue: ")
                    Timber.e(e)
                    _isUserLoggedIn.value = NetworkResult.Exception(e)
                }

            }

            //_isUserLoggedIn.value = true
        }
    }

    private fun saveUserData(
        networkResponse: NetworkResult.Success<JsonElement>,
        user: User
    ) {
        repository.setUserEmail(networkResponse.data.asJsonObject[EMAIL].asString)
        repository.setName(
            networkResponse.data.asJsonObject[FIRST_NAME].asString
                    + " "
                    + networkResponse.data.asJsonObject[LAST_NAME].asString
        )
        repository.setFirstName(networkResponse.data.asJsonObject[FIRST_NAME].asString)
        repository.setLastName(networkResponse.data.asJsonObject[LAST_NAME].asString)
        repository.setMobile(networkResponse.data.asJsonObject[MOBILE].asString)
        repository.setUserId(networkResponse.data.asJsonObject[ID].asInt)

        if (!networkResponse.data.asJsonObject[PROFILE_IMAGE_URL].isJsonNull) {
            repository.setProfilePicture(networkResponse.data.asJsonObject[PROFILE_IMAGE_URL].asString)
        } else {
            //to remove in future, since the API is returning null for profileImageUrl
            user.profilePictureUrl?.let { repository.setProfilePicture(it) }
        }

        repository.setAccessToken(networkResponse.data.asJsonObject[TOKEN].asString)
    }

    private fun createRequestBody(user: User): MutableMap<String, String> {
        val requestBody = mutableMapOf<String, String>()

        user.email?.let { requestBody.put(EMAIL, it) }
        requestBody[DOB] = ""
        user.firstName?.let { requestBody.put(FIRST_NAME, it) }
        user.lastName?.let { requestBody.put(LAST_NAME, it) }
        user.mobile?.let { requestBody.put(MOBILE, it) }
        user.profilePictureUrl?.let { requestBody.put(PROFILE_IMAGE_URL, it) }
        requestBody[PWD] = ""
        user.referralCode?.let { requestBody.put(REFERRALCODE, it) }

        return requestBody
    }
}