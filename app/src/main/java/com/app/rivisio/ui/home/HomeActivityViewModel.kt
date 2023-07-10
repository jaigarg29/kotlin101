package com.app.rivisio.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _totalTopicsCreated = MutableLiveData<NetworkResult<JsonElement>>()
    val totalTopicsCreated: LiveData<NetworkResult<JsonElement>>
        get() = _totalTopicsCreated

    fun syncPurchases() {
        viewModelScope.launch(Dispatchers.IO) {
            val purchase = repository.getPurchase()
            if (purchase.isNotEmpty()) {
                val result = handleApi {
                    repository.saveSubscription(
                        purchase[0]
                    )
                }

                if (result is NetworkResult.Success) {
                    Timber.e(result.data.toString())

                    repository.deletePurchase(purchase[0])

                }
            }
        }
    }


    fun gettotalTopicsCreated() {
            viewModelScope.launch {

                _totalTopicsCreated.value = NetworkResult.Loading

                val response = handleApi {
                    repository.getUserStats(
                        repository.getAccessToken()!!,
                        repository.getUserId()
                    )
                }
                _totalTopicsCreated.value = response

            }
    }

}