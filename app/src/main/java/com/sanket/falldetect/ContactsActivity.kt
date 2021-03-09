package com.sanket.falldetect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class ContactsActivity : AppCompatActivity() {

    lateinit var myViewModel: MyViewModel

    lateinit var context: Context

    lateinit var name: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        context = this@ContactsActivity
        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.getLoginDetails(context)!!.observe(this, Observer {

            if (it == null) {
                Toast.makeText(context, "No Contacts Found", Toast.LENGTH_SHORT).show();
            }
            else {



            }
        })



    }

    fun insertData(name: String, email: String, address: String, phone: String){
        myViewModel.insertData(context, name, email, address, phone)
    }
}