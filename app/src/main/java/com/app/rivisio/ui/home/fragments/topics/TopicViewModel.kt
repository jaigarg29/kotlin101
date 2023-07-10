package com.app.rivisio.ui.home.fragments.topics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    private val _deleteTopic = MutableLiveData<NetworkResult<JsonElement>>()
    val deleteTopic: LiveData<NetworkResult<JsonElement>>
        get() = _deleteTopic

    fun getTopicsData() {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading
            Log.e("Token", repository.getAccessToken()!!)
            val response = handleApi {
                repository.getAllTopics(
                    repository.getAccessToken()!!,
                    repository.getUserId()
                )
            }

            _topics.value = response
        }
    }

    fun deleteTopic(topicId: Int) {
        viewModelScope.launch {
            _deleteTopic.value = NetworkResult.Loading
            Log.d("anything",repository.getAccessToken()!!)
            val response = handleApi {
                repository.deleteTopic(
                    topicId,
                    repository.getAccessToken()!!,
                    repository.getUserId()

                )
            }

            _deleteTopic.value = response
        }
    }
}