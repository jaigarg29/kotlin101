package com.app.rivisio.ui.home.fragments.calendar

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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    fun getTopicsForDate(date: LocalDate) {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading

            val response = handleApi {
                repository.getTopicByDate(
                    date.toString(),
                    repository.getAccessToken()!!,
                    repository.getUserId()
                )
            }

            _topics.value = response
        }
    }

}