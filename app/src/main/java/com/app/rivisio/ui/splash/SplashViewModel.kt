package com.app.rivisio.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _users = MutableLiveData<NetworkResult<JsonElement>>()
    val users: LiveData<NetworkResult<JsonElement>>
        get() = _users

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState>
        get() = _userState


    fun fetchUsers() {
        viewModelScope.launch {
            _users.value = NetworkResult.Loading

            //val response = handleApi { mainRepository.getUsers() }
            //_users.value = response

        }

    }

    fun getUserState() {
        viewModelScope.launch {
            delay(1000)
            _userState.value = repository.getUserState()
        }
    }
}
