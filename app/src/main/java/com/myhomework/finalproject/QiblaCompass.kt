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

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.myhomework.finalproject.databinding.ActivityQiblaCompassBinding


class QiblaCompass : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var AMSensor: Sensor
    private lateinit var MFSensor: Sensor
    private lateinit var binding: ActivityQiblaCompassBinding

    lateinit var vibrator:Vibrator

    private var PERMISSION_ID = 44
    private val kaabaLocation = Location("service Provider")
    private var userLocation: Location? = Location("service Provider")
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var rotationValues = FloatArray(9)
    private var AMValues = FloatArray(3)
    private var MFValues = FloatArray(3)

    private var finalValues = FloatArray(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQiblaCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the VIBRATOR_SERVICE system service
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        kaabaLocation.latitude = 21.422487
        kaabaLocation.longitude = 39.826206

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        AMSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        MFSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (AMValues != null && MFValues != null) {
            Toast.makeText(this, "Sensors are found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sensors are not found", Toast.LENGTH_SHORT).show()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // method to get the location
        getLastLocation()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            MFValues = sensorEvent.values
        } else if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            AMValues = sensorEvent.values
        }
        SensorManager.getRotationMatrix(rotationValues, null, AMValues, MFValues)
        SensorManager.getOrientation(rotationValues, finalValues)

        var northDegree = Math.toDegrees(finalValues[0].toDouble()).toInt()
        if (northDegree < 0) {
            northDegree += 360
        }

        var kaabaDegree = 0f
        if (userLocation != null) {
            kaabaDegree = userLocation?.bearingTo(kaabaLocation)!!
        }

        if (kaabaDegree < 0) {
            kaabaDegree += 360
        }

        var direction: Float = kaabaDegree - northDegree
        if (direction < 0) {
            direction += 360
        }

        binding.text.text =
            "Qibla Angle: $direction"
        binding.img.rotation = direction

        if(direction.toInt() == 0){
            handleVibration()
        }



    }

    private fun handleVibration(){
        val vibrationEffect1: VibrationEffect

        // this is the only type of the vibration which requires system version Oreo (API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // this effect creates the vibration of default amplitude for 1000ms(1 sec)
            vibrationEffect1 =
                VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)

            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect1)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                mFusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        userLocation = task.result
                        if (userLocation == null) {
                            requestNewLocationData()
                        }
//                        else {
//                            binding.userLocation.text =
//                                "latitude: ${userLocation?.latitude}\nlongitiue: ${userLocation?.longitude}"
//                        }
                    }
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            userLocation = locationResult.lastLocation
//            binding.userLocation.text =
//                "latitude: ${userLocation?.latitude}\nlongitiue: ${userLocation?.longitude}"
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Animation.RELATIVE_TO_SELF
        sensorManager.registerListener(this, AMSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, MFSensor, SensorManager.SENSOR_DELAY_GAME)

        if (checkPermissions()) {
            getLastLocation();
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

}
