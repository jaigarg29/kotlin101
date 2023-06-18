package com.app.rivisio.ui.home.fragments.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String>
        get() = _userEmail

    private val _userProfilePic = MutableLiveData<String>()
    val userProfilePic: LiveData<String>
        get() = _userProfilePic

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean>
        get() = _logout

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = repository.getName()
            _userEmail.value = repository.getUserEmail()
            _userProfilePic.value = repository.getProfilePicture()
        }
    }

    fun logout() {
        viewModelScope.launch {

            repository.setUserLoggedOut()
            _logout.value = true
        }
    }
}