package com.app.rivisio.ui.home.fragments.home_fragment

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
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    private val _update = MutableLiveData<NetworkResult<JsonElement>>()
    val update: LiveData<NetworkResult<JsonElement>>
        get() = _update

    private val _userStats = MutableLiveData<NetworkResult<JsonElement>>()
    val userStats: LiveData<NetworkResult<JsonElement>>
        get() = _userStats

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = repository.getName()
        }
    }

    fun getTopicsData() {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading

            val response = handleApi {
                repository.getTopicsData(
                    repository.getAccessToken()!!,
                    repository.getUserId()
                )
            }

            _topics.value = response
        }
    }

    fun reviseTopic(id: Int?, revsion: Map<String, String>) {

        viewModelScope.launch {

            _update.value = NetworkResult.Loading

            val response = handleApi {
                repository.reviseTopic(
                    id!!,
                    revsion
                )
            }
            _update.value = response

        }

    }

    fun getUserStats() {
        viewModelScope.launch {
            viewModelScope.launch {

                _userStats.value = NetworkResult.Loading

                val response = handleApi {
                    repository.getUserStats(
                        repository.getAccessToken()!!,
                        repository.getUserId()
                    )
                }
                _userStats.value = response

            }
        }
    }

}