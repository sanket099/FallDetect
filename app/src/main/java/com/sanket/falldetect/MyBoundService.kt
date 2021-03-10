package com.sanket.falldetect

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


            //persistence notification
            val notification = NotificationCompat.Builder(this, "ChannelId")
                .setContentTitle("Example IntentService")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


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

        if (d < 1) println("FALL$d")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}