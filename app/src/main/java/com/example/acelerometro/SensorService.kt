package com.example.acelerometro

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val supabaseUrl = "https://uludxmbfkkpeqqymmrfu.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsdWR4bWJma2twZXFxeW1tcmZ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5Mzk3ODMsImV4cCI6MjA2NDUxNTc4M30.Uzk2t-mkbN53i_lVnCZvARJvd30rK4bYDbUYsLusxv0"
    private val tabla = "acelerometro"
    private var idUsuario: String = "usuario_desconocido"

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        idUsuario = intent?.getStringExtra("username") ?: "usuario_desconocido"

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
            enviarDatos(idUsuario, x, y, z)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun enviarDatos(idUsuario: String, x: Float, y: Float, z: Float) {
        val json = JSONObject().apply {
            put("id_usuario", idUsuario)
            put("x", x)
            put("y", y)
            put("z", z)
        }

        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$supabaseUrl/rest/v1/$tabla")
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=minimal")
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    println("Error al enviar: ${response.code}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "sensor_channel_id"
        val channelName = "Sensor Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio del acelerómetro")
            .setContentText("Enviando datos en segundo plano")
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Cambia por tu ícono
            .build()
    }
}

