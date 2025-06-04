package com.example.acelerometro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private var serviceStarted = false
    private lateinit var btnToggle: Button
    private lateinit var tvUser: TextView

    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToggle = findViewById(R.id.btnToggle)
        tvUser = findViewById(R.id.tvUser)

        currentUsername = intent.getStringExtra("username")
        tvUser.text = "Usuario: $currentUsername"

        btnToggle.text = "Iniciar Servicio"
        btnToggle.setOnClickListener {
            checkAndRequestPermissions()
        }
    }

    private fun checkAndRequestPermissions() {
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(fineLocation) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(fineLocation), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            toggleService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleService()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleService() {
        val intent = Intent(this, SensorService::class.java)
        intent.putExtra("username", currentUsername)

        if (!serviceStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            btnToggle.text = "Detener Servicio"
        } else {
            stopService(intent)
            btnToggle.text = "Iniciar Servicio"
        }
        serviceStarted = !serviceStarted
    }
}
