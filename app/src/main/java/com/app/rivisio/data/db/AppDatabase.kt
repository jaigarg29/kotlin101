package com.app.rivisio.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.rivisio.data.db.dao.PurchaseDao
import com.app.rivisio.data.db.entity.Purchase

@Database(entities = [Purchase::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun purchaseDao(): PurchaseDao

    // Function to create object of Room Database
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    )
                        .addCallback(rdc)
                        //.addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

var rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        // do something after database has been created

    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        // do something every time database is open
    }
}