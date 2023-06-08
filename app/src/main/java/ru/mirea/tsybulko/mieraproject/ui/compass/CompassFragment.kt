package ru.mirea.tsybulko.mieraproject.ui.compass

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import ru.mirea.tsybulko.mieraproject.MainActivity
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentCompassBinding
import ru.mirea.tsybulko.mieraproject.ui.compass.CompassHelper.calculateHeading
import ru.mirea.tsybulko.mieraproject.ui.compass.CompassHelper.convertRadtoDeg
import ru.mirea.tsybulko.mieraproject.ui.compass.CompassHelper.map180to360


class CompassFragment : Fragment(), SensorEventListener {
    private lateinit var binding: FragmentCompassBinding

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magneticSensor: Sensor

    private lateinit var compassImage: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private var heading = 0f
    private var oldHeading = 0f
    private var longitude = 0.0
    private var latitude = 0.0
    private var altitude = 0.0
    private var magneticDeclination = 0f
    private var trueHeading = 0f

    private var isLocationRetrieved = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_compass, container, false)
        binding = FragmentCompassBinding.bind(rootFragmentView)

        compassImage = binding.compassImage

        sensorManager = this.context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        //check if we have permission to access location
        if (ContextCompat.checkSelfPermission(
                this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            //fine location permission already granted
            getLocation()
        } else {
            //if permission is not granted, request location permissions from user
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 200)
        }

        return rootFragmentView
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                isLocationRetrieved = true
                latitude = it.latitude
                longitude = it.longitude
                altitude = it.altitude
                magneticDeclination = CompassHelper.calculateMagneticDeclination(
                    latitude,
                    longitude,
                    altitude
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // to stop the listener and save battery
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            //make sensor readings smoother using a low pass filter
            CompassHelper.lowPassFilter(event.values.clone(), accelerometerReading)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            //make sensor readings smoother using a low pass filter
            CompassHelper.lowPassFilter(event.values.clone(), magnetometerReading)
        }
        updateHeading()
    }

    private fun updateHeading() {
        //oldHeading required for image rotate animation
        oldHeading = heading
        heading = calculateHeading(accelerometerReading, magnetometerReading)
        heading = convertRadtoDeg(heading)
        heading = map180to360(heading)
        if (isLocationRetrieved) {
            trueHeading = heading + magneticDeclination
            if (trueHeading > 360) { //if trueHeading was 362 degrees for example, it should be adjusted to be 2 degrees instead
                trueHeading -= 360
            }
        }
        val rotateAnimation = RotateAnimation(
            -oldHeading,
            -heading,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 500
            fillAfter = true
            binding.compassImage.startAnimation(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}