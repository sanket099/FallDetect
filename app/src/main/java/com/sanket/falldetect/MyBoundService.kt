package com.sanket.falldetect

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.sqrt

class MyBoundService : Service(), SensorEventListener {

    private val myBinder = MyLocalBinder()

    private val TAG = "ExampleIntentService"
    private var wakeLock: PowerManager.WakeLock? = null
    private var sensorManager: SensorManager? = null
    var accel: Sensor? = null
    var fall = false

    override fun onCreate() {
        super.onCreate()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,  //wake lock: will keep cpu running on phone lock
                "ExampleApp:Wakelock"
        )
        wakeLock!!.acquire(600000000) //will keep cpu on //wake lock drains battery if timeout not specified
        //Log.d(MyIntentService.TAG, "Wakelock acquired")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //persistence notification

            val intent = Intent(this, MainActivity::class.java)

// Creating a pending intent and wrapping our intent

// Creating a pending intent and wrapping our intent
            val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            //persistence notification
            val notification = NotificationCompat.Builder(this, "ChannelId")
                .setContentTitle("Example IntentService")
                .setContentText("Running...")
                    .setAutoCancel(true)

                .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                .build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //println("hello")
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accel = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        return START_STICKY
    }

   /* override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)

    }

    // remove override and make onHandleIntent private.
    private fun onHandleIntent(intent: Intent?) {}*/


    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }
    inner class MyLocalBinder : Binder() {
        fun getService() : MyBoundService {
            return this@MyBoundService
        }
    }

    fun getFallen(): Boolean {
        return fall
    }

    override fun onSensorChanged(event: SensorEvent?) {


        val d = sqrt((event!!.values[0] * event.values[0] +
                event.values[1] * event.values[1] + event.values[2] *
                event.values[2]).toDouble())
       // println(d)

        if (d < 1) {
            println("FALL$d")
            fall = true
            val sendLevel = Intent("FALL")
            sendLevel.putExtra("Fall", fall)
            LocalBroadcastManager.getInstance(this).sendBroadcast(sendLevel)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}