/*
package com.sanket.falldetect

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt

class MyIntentService :  JobIntentService(),SensorEventListener{

    private val TAG = "ExampleIntentService"
    private var wakeLock: WakeLock? = null
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

    // This method is called when service starts instead of onHandleIntent
    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accel = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
    }

    // remove override and make onHandleIntent private.
    private fun onHandleIntent(intent: Intent?) {}

    // convenient method for starting the service.
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, MyIntentService::class.java, 1, intent)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {


        val d = sqrt((event!!.values[0] * event.values[0] +
                event.values[1] * event.values[1] + event.values[2] *
                event.values[2]).toDouble())

        if (d < 1) println("FALL$d")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}*/
