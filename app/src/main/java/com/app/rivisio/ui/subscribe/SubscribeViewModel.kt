package com.app.rivisio.ui.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.db.entity.Purchase
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _insert = MutableLiveData<Long>()
    val insert: LiveData<Long>
        get() = _insert

    fun insertPurchase(purchase: Purchase) {
        viewModelScope.launch {

            purchase.userId = repository.getUserId()

            val result = repository.insertPurchase(purchase)
            _insert.value = result
        }
    }
}
