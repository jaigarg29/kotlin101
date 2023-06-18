package com.app.rivisio.ui.home

import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

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

}