package com.myhomework.finalproject

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.myhomework.finalproject.app.TheApp
import java.util.*

class AzkarService : Service(), SensorEventListener {

    private lateinit var morningAzkar: MediaPlayer
    private lateinit var eveningAzkar: MediaPlayer
    private var pausedAzkar: MediaPlayer? = null

    private lateinit var preferences: SharedPreferences

    private var morningTime = "08:00"
    private var eveningTime = "20:00"
    private var morningBrightness = 2
    private var eveningBrightness = 15

    lateinit var sensorManager: SensorManager
    lateinit var lightSensor: Sensor
    lateinit var gyroscopeSensor: Sensor
    lateinit var proximitySensor: Sensor
    lateinit var accelerometerSensor: Sensor

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        morningAzkar = MediaPlayer.create(this, R.raw.music_morning)
        eveningAzkar = MediaPlayer.create(this, R.raw.music_night)

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE)

        morningTime = preferences.getString("morningTime", "08:00")!!
        eveningTime = preferences.getString("eveningTime", "20:00")!!
        morningBrightness = preferences.getInt("morningBrightness", 20)
        eveningBrightness = preferences.getInt("eveningBrightness", 15)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        if (lightSensor != null && gyroscopeSensor != null && proximitySensor != null && accelerometerSensor != null) {
            Toast.makeText(this, "Sensors are found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some Sensors are not found", Toast.LENGTH_SHORT).show()
        }

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        stopAzkar()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val notificationIntent = Intent(this, AzkarActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(this, TheApp.CHANNEL_NAME)
            .setContentTitle("خدمة الأذكار")
            .setContentText("تم التفعيل")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .build()

        startForeground(1, notification)
        return START_REDELIVER_INTENT
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        Log.e("TAG", "onSensorChanged: ${sensorEvent?.sensor?.type}", )



        if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = sensorEvent.values[2].toInt()
            val shouldNeglectLight = z < -6

            if (shouldNeglectLight && pausedAzkar == null && isMorningTime()) {
                if (!morningAzkar.isPlaying) {
                    morningAzkar.start()
                }
            } else if (shouldNeglectLight && pausedAzkar == null && isEveningTime()) {
                if (!eveningAzkar.isPlaying) {
                    eveningAzkar.start()
                }
            }

        }


        if (sensorEvent?.sensor?.type == Sensor.TYPE_LIGHT) {
            var light = sensorEvent.values[0].toInt()

            if (isMorningTime() && light >= morningBrightness) {
                if (!morningAzkar.isPlaying) {
                    morningAzkar.start()
                }
            } else if (isEveningTime() && light <= eveningBrightness) {
                if (!eveningAzkar.isPlaying) {
                    eveningAzkar.start()
                }
            }
        }


        if (sensorEvent?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            var y = sensorEvent.values[1]
            if (y > 3 || y < -3) {
                stopAzkar()
            }
        }

        if (sensorEvent?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            var sensorValue = sensorEvent.values[0]

            if (sensorValue > 0) { //nothing is close
                if (pausedAzkar != null) {
                    pausedAzkar?.start()
                    pausedAzkar = null
                }
            } else {
                pausedAzkar = pauseAzkar()
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun isMorningTime(): Boolean {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        Log.e("TAG", "$currentHour:$currentMinute" + " ==  $morningTime")

        return "$currentHour:$currentMinute" == "$morningTime"
    }

    private fun isEveningTime(): Boolean {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        //Log.e("TAG", "$currentHour:$currentMinute" + " ==  $eveningTime" )

        return "$currentHour:$currentMinute" == "$eveningTime"
    }

    private fun stopAzkar() {
        if (morningAzkar.isPlaying) {
            morningAzkar.stop()
        } else if (eveningAzkar.isPlaying) {
            eveningAzkar.stop()
        }
    }

    private fun pauseAzkar(): MediaPlayer? {
        if (morningAzkar.isPlaying) {
            morningAzkar.pause()
            return morningAzkar
        } else if (eveningAzkar.isPlaying) {
            eveningAzkar.pause()
            return eveningAzkar
        }
        return null
    }

}