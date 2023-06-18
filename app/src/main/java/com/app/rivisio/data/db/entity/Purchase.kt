package com.app.rivisio.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase")
data class Purchase(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long = 0,

    @ColumnInfo(name = "userId")
    var userId: Int,

    @ColumnInfo(name = "orderId")
    var orderId: String,

    @ColumnInfo(name = "productId")
    var productId: String,

    @ColumnInfo(name = "purchaseTime")
    var purchaseTime: Long,

    @ColumnInfo(name = "isAutoRenewing")
    var isAutoRenewing: Boolean,

    @ColumnInfo(name = "basePlanId")
    var basePlanId: String,

    @ColumnInfo(name = "billingPeriod")
    var billingPeriod: String
)