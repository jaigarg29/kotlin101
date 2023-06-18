package com.app.rivisio.data.db.dao

import androidx.room.*
import com.app.rivisio.data.db.entity.Purchase

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long

    @Query("SELECT * FROM purchase")
    suspend fun getPurchase(): List<Purchase>

    @Delete
    fun deletePurchase(purchase: Purchase): Int

}