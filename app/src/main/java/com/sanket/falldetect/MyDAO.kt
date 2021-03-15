package com.sanket.falldetect

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface MyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contactClass: ContactClass)

    @Query("SELECT * FROM Contact")
    fun getContacts() : LiveData<List<ContactClass>>
}