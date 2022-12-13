package com.myhomework.finalproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.myhomework.finalproject.databinding.ActivityMainBinding

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        binding.btnQiblaCompass.setOnClickListener {
            startActivity(Intent(this,QiblaCompass::class.java))
        }

        binding.btnAzkarService.setOnClickListener {
            startActivity(Intent(this,AzkarActivity::class.java))
        }
    }

}