package com.example.mms.database.inApp

import android.content.Context
import androidx.room.Room
import com.example.mms.database.converter.DateConverter

object SingletonDatabase {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .addTypeConverter(DateConverter())
                .build()
            INSTANCE = instance
            instance
        }
    }
}
