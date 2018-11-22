package com.example.android.androidlocation1

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Speed::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun speedDao(): SpeedDao

    companion object {
        var INSTANCE: AppDatabase? = null
        fun getAppDataBase(context: Context): AppDatabase {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "myDB"
                    ).build()
                }
            }
            return INSTANCE!!
        }

        fun destroyDataBase(){

            INSTANCE = null
        }
    }
}