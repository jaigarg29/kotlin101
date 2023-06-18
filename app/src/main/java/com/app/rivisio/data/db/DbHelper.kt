package com.app.rivisio.data.db

import com.app.rivisio.data.db.entity.Purchase

interface DbHelper {

    suspend fun insertPurchase(purchase: Purchase): Long

    suspend fun getPurchase(): List<Purchase>

    fun deletePurchase(purchase: Purchase): Int

}