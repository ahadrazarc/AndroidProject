package com.example.stepcounter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private var isPaused = false
    private var stepsAtReset = 0
    private var lastStepCount = 0
    private var currentDistance = 0.0

    private lateinit var stepsText: TextView
    private lateinit var distanceText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var circleProgress: ProgressBar
    private lateinit var pauseButton: MaterialButton
    private lateinit var resetButton: MaterialButton

    private val STEP_LENGTH_METERS = 0.78
    private val CALORIES_PER_STEP = 0.04

    private val REQUEST_CODE_PERMISSIONS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsIfNeeded()

        // Initialize UI components
        stepsText = findViewById(R.id.stepsText)
        distanceText = findViewById(R.id.distanceText)
        caloriesText = findViewById(R.id.caloriesText)
        circleProgress = findViewById(R.id.circleProgress)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Toast.makeText(this, "Step Counter Sensor Not Available!", Toast.LENGTH_LONG).show()
        }

        pauseButton.setOnClickListener {
            isPaused = !isPaused
            if (isPaused) {
                pauseButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
                pauseButton.text = "Resume"
                Toast.makeText(this, "Step counter paused", Toast.LENGTH_SHORT).show()
            } else {
                pauseButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
                pauseButton.text = "Pause"
                Toast.makeText(this, "Step counter resumed", Toast.LENGTH_SHORT).show()
            }
        }

        resetButton.setOnClickListener {
            stepsAtReset = lastStepCount
            updateStepUI(0)
            Toast.makeText(this, "Steps reset to zero", Toast.LENGTH_SHORT).show()
        }

    }

    private fun requestPermissionsIfNeeded() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions denied, app may not work correctly.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER && !isPaused) {
            lastStepCount = event.values[0].toInt()
            val stepsSinceReset = lastStepCount - stepsAtReset
            updateStepUI(stepsSinceReset)
        }
    }

    private fun updateStepUI(steps: Int) {
        stepsText.text = "Steps: $steps"
        circleProgress.progress = steps.coerceAtMost(10000)

        val distance = steps * STEP_LENGTH_METERS / 1000
        distanceText.text = "Distance: %.2f km".format(distance)

        val calories = steps * CALORIES_PER_STEP
        caloriesText.text = "Calories: %.1f kcal".format(calories)

        currentDistance = distance
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}
