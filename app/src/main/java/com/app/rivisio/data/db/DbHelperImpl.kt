package com.app.rivisio.data.db

import com.app.rivisio.data.db.entity.Purchase
import javax.inject.Inject

class DbHelperImpl @Inject constructor(private val appDatabase: AppDatabase) : DbHelper {
    override suspend fun insertPurchase(purchase: Purchase): Long {
        return appDatabase.purchaseDao().insertPurchase(purchase)
    }

    override suspend fun getPurchase(): List<Purchase> {
        return appDatabase.purchaseDao().getPurchase()
    }

    override fun deletePurchase(purchase: Purchase): Int {
        return appDatabase.purchaseDao().deletePurchase(purchase)
    }
}