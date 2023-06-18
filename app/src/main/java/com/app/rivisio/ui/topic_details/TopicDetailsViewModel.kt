package com.app.rivisio.ui.topic_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _topicData = MutableLiveData<NetworkResult<JsonElement>>()
    val topicData: LiveData<NetworkResult<JsonElement>>
        get() = _topicData

    private val _update = MutableLiveData<NetworkResult<JsonElement>>()
    val update: LiveData<NetworkResult<JsonElement>>
        get() = _update


    fun getTopicDetails(topicId: Int) {

        viewModelScope.launch {

            _topicData.value = NetworkResult.Loading

            val response = handleApi {
                repository.getTopicDetails(
                    topicId,
                    repository.getAccessToken(),
                    repository.getUserId()
                )
            }

            _topicData.value = response

        }
    }

    fun updateTextNote(topicId: Int, topicFromServer: TopicFromServer) {

        viewModelScope.launch {

            _update.value = NetworkResult.Loading

            val response = handleApi {
                repository.editTopicDetails(
                    topicId,
                    repository.getAccessToken(),
                    repository.getUserId(),
                    topicFromServer
                )
            }

            _update.value = response

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
}