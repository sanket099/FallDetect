package com.sanket.falldetect

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    var liveDataLogin: LiveData<List<ContactClass>>? = null

    fun insertData(context: Context, name: String, email: String, address: String, phone: String) {
        MyRepo.insertData(context, name, email,address, phone)
    }

    fun getLoginDetails(context: Context) : LiveData<List<ContactClass>>? {
        liveDataLogin = MyRepo.getLoginDetails(context)
        return liveDataLogin
    }

}