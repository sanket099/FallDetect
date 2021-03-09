package com.sanket.falldetect

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MyRepo {

    companion object {

        var contactDatabase: MyDatabase? = null

        var contactClass: LiveData<List<ContactClass>>? = null

        fun initializeDB(context: Context) : MyDatabase {
            return MyDatabase.getDataseClient(context)
        }

        fun insertData(context: Context, name: String, email: String, address: String, phone: String) {

            contactDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val loginDetails = ContactClass(name,email,address, phone)
                contactDatabase!!.contactsDao().insertContact(loginDetails)
            }

        }

        fun getLoginDetails(context: Context) : LiveData<List<ContactClass>>? {

            contactDatabase = initializeDB(context)

            contactClass = contactDatabase!!.contactsDao().getContacts()

            return contactClass
        }

    }
}