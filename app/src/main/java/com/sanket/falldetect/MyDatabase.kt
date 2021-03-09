package com.sanket.falldetect

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactClass::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun contactsDao() : MyDAO

    companion object {

        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDataseClient(context: Context) : MyDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, MyDatabase::class.java, "MyDatabase")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}