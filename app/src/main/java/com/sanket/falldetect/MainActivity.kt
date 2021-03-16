package com.sanket.falldetect

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


class MainActivity : AppCompatActivity() {

    private val sharedPrefFile: String = "shared"
    lateinit var sharedPreferences: SharedPreferences

    var myService: MyBoundService? = null
    var isBound = false
    var fallen : Boolean? = false
    lateinit var myViewModel: MyViewModel
    lateinit var myContactList : List<ContactClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS),
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.getLoginDetails(this)!!.observe(this, Observer {

            if (it == null) {
                Toast.makeText(this, "No Contacts Found", Toast.LENGTH_SHORT).show()
            } else {
                if (it.isEmpty()) {
                    Toast.makeText(this, "No Contacts Found", Toast.LENGTH_SHORT).show()
                } else {

                    myContactList = it
                    /*_binding.name.setText(it[0].name)
                    _binding.email.setText(it[0].email)
                    _binding.address.setText(it[0].address)
                    _binding.phn.setText(it[0].phone)

                    _binding.name2.setText(it[1].name)
                    _binding.email2.setText(it[1].email)
                    _binding.address2.setText(it[1].address)
                    _binding.phn2.setText(it[1].phone)

                    _binding.name.setText(it[2].name)
                    _binding.email3.setText(it[2].email)
                    _binding.address3.setText(it[2].address)
                    _binding.phn3.setText(it[2].phone)
*/
                }

            }
        })


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

    fun onProviderEnabled(provider: String) {}

    fun onProviderDisabled(provider: String) {}

    fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}



    /*fun sendSMS(number: String)
    {
        val uri = Uri.parse("smsto:$number")

        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("Fall Detected ", "Location Coordinates : " + sharedPreferences.getString("lattitude", null)
                + sharedPreferences.getString("longitude", null))
        startActivity(intent)
    }
*/
    private fun sendSMS(number : String) {
        val smsManager = SmsManager.getDefault()
        val lat = sharedPreferences.getString("lattitude", null)
        val longi = sharedPreferences.getString("longitude", null)

        val message = "http://maps.google.com/maps?saddr=$lat,$longi"

        if (!number.isEmpty()) {
            smsManager.sendTextMessage(number, null, message, null, null)
            Toast.makeText(this@MainActivity, "sent sos sms to  $number", Toast.LENGTH_LONG).show()
        }

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
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS),
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
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
        catch (e: IllegalArgumentException){
            println("Error $e")
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            fallen = intent.getBooleanExtra("Fall", false)

            println("Fall Detected")

             //Toast.makeText(context, fallen.toString(), Toast.LENGTH_SHORT).show()
            if(myContactList.isNotEmpty()){
                sendSMS(myContactList[0].phone)
                /*sendSMS(myContactList[1].phone) // enable
                sendSMS(myContactList[2].phone)*/
            }

        }
    }

    private fun getLocalIntentFilter(): IntentFilter {
        val iFilter = IntentFilter()
        iFilter.addAction("FALL")
        return iFilter
    }




}