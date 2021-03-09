package com.sanket.falldetect

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        
    }

    fun sendSMS(number : String)
    {
        val uri = Uri.parse(number)
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", "Here goes your message...")
        startActivity(intent)
    }




}