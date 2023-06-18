package com.app.rivisio.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.login.User
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _userData = MutableLiveData<NetworkResult<JsonElement>>()
    val userData: LiveData<NetworkResult<JsonElement>>
        get() = _userData

    fun getUserDetails() {
        viewModelScope.launch {

            _userData.value = NetworkResult.Loading

            val networkResponse = handleApi { repository.getUser(
                repository.getAccessToken(),
                repository.getUserId()
            ) }

            _userData.value = networkResponse
        }
    }
}