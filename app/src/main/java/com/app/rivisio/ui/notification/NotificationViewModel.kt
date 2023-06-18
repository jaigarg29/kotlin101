package com.app.rivisio.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _notificationEnabled = MutableLiveData<Boolean>()
    val notificationEnabled: LiveData<Boolean>
        get() = _notificationEnabled

    private val _notificationTimeUpdated = MutableLiveData<Boolean>()
    val notificationTimeUpdated: LiveData<Boolean>
        get() = _notificationTimeUpdated

    private val _notificationTime = MutableLiveData<String>()
    val notificationTime: LiveData<String>
        get() = _notificationTime

    fun isNotificationEnabled() {
        viewModelScope.launch {
            _notificationEnabled.value = repository.isNotificationEnabled()
        }
    }

    fun enableNotification(enable: Boolean){
        viewModelScope.launch {
            repository.setNotificationSetting(enable)
        }
    }

    fun saveTime(hour: Int, minute: Int) {
        viewModelScope.launch {

            repository.setNotificationTime("$hour:$minute")

            _notificationTimeUpdated.value  = true
            _notificationTime.value = "$hour:$minute"
        }
    }

    fun getTime(){
        viewModelScope.launch {
            _notificationTime.value = repository.getNotificationTime()
        }
    }
}