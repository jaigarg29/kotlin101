package com.app.rivisio.ui.refer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _userData = MutableLiveData<NetworkResult<JsonElement>>()
    val userData: LiveData<NetworkResult<JsonElement>>
        get() = _userData

    private val _limitData = MutableLiveData<NetworkResult<JsonElement>>()
    val limitData: LiveData<NetworkResult<JsonElement>>
        get() = _limitData


    fun getUserDetails() {
        viewModelScope.launch {

            _userData.value = NetworkResult.Loading

            val networkResponse = handleApi {
                repository.getUser(
                    repository.getAccessToken(),
                    repository.getUserId()
                )
            }

            _userData.value = networkResponse
        }
    }

    fun limitcheck() {
        viewModelScope.launch {

            _limitData.value = NetworkResult.Loading

            val networkResponse = handleApi {
                repository.limitcheck(
                    repository.getAccessToken(),
                    repository.getUserId()
                )
            }

            _limitData.value = networkResponse
        }
    }


}