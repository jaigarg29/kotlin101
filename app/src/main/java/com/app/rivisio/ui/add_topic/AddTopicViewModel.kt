package com.app.rivisio.ui.add_topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.network.HEX_CODE
import com.app.rivisio.data.network.NAME
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTopicViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _tags = MutableLiveData<NetworkResult<JsonElement>>()
    val tags: LiveData<NetworkResult<JsonElement>>
        get() = _tags

    private val _addTags = MutableLiveData<NetworkResult<JsonElement>>()
    val addTags: LiveData<NetworkResult<JsonElement>>
        get() = _addTags

    fun getTopics() {
        viewModelScope.launch {
            _tags.value = NetworkResult.Loading
            val networkResponse = handleApi {
                repository.tags(
                    repository.getAccessToken(),
                    repository.getUserId()
                )
            }
            _tags.value = networkResponse
        }
    }

    fun addTag(tagName: String, tagColor: String) {
        viewModelScope.launch {
            _addTags.value = NetworkResult.Loading

            val requestBody = mutableMapOf<String, String>()
            requestBody[HEX_CODE] = tagColor.trim()
            requestBody[NAME] = tagName.trim()

            val networkResponse = handleApi {
                repository.addTag(
                    repository.getAccessToken(),
                    repository.getUserId(),
                    requestBody
                )
            }

            _addTags.value = networkResponse
        }
    }
}