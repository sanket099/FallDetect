package com.sanket.falldetect

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {

    private val sharedPrefFile: String = "shared"
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)


    var myService: MyBoundService? = null
    var isBound = false
    var fallen : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }


       // val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

        val button: SwitchCompat = findViewById(R.id.switch_btn)
        button.setOnCheckedChangeListener { buttonView, isChecked ->

        if(isChecked){
            val intent = Intent(this, MyBoundService::class.java)
            bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
        }
            else{

        }

        }

        if(fallen == true){
            //sendSMS("number")
            button.isChecked = true

        }




    }

    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as MyBoundService.MyLocalBinder
            myService = binder.getService()
            isBound = true
            fallen = myService?.getFallen()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    fun sendSMS(number : String)
    {
        val uri = Uri.parse(number)
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("Fall Detected ", "Location Coordinates : " + sharedPreferences.getString("lattitude", null)
                + sharedPreferences.getString("longitude", null))
        startActivity(intent)
    }

    private fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationListener = object : LocationListener {



            override fun onLocationChanged(location: Location) {

                var latitude = location.latitude
                var longitude = location.longitude

                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("latitude",latitude.toString())
                editor.putString("longitude",longitude.toString())
                editor.apply()
                editor.commit()

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }



        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> Toast.makeText(this,"Please Grant Permission", Toast.LENGTH_SHORT).show()//Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }




}