package com.sanket.falldetect

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MainActivity : AppCompatActivity() {

    private val sharedPrefFile: String = "shared"
    lateinit var sharedPreferences: SharedPreferences

    var myService: MyBoundService? = null
    var isBound = false
    var fallen : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

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
        button.setOnCheckedChangeListener { _, isChecked ->

            val intent = Intent(this, MyBoundService::class.java)

        if(isChecked){

            bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
            else{
                stopService(intent)
        }

        }

        if(fallen == true){
            //sendSMS("number")
                //println("fallen : $fallen")
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
            //fallen = myService?.getFallen()

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            unregisterReceiver(mMessageReceiver)
        }
    }



    fun sendSMS(number: String)
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

                val latitude = location.latitude
                val longitude = location.longitude

                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("latitude", latitude.toString())
                editor.putString("longitude", longitude.toString())
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
                PackageManager.PERMISSION_DENIED -> Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show()//Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    override fun onStart() {
        super.onStart()

        if(!sharedPreferences.getBoolean("ec_saved", false)){
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, getLocalIntentFilter())
        //registerReceiver(mMessageReceiver, getLocalIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(mMessageReceiver)
        }
        catch (e : IllegalArgumentException){
            println("Error $e")
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            fallen = intent.getBooleanExtra("Fall", false)

            println("FALLLLL")

             Toast.makeText(context, fallen.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocalIntentFilter(): IntentFilter {
        val iFilter = IntentFilter()
        iFilter.addAction("FALL")
        return iFilter
    }




}