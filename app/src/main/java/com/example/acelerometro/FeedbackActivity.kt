package com.example.acelerometro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class FeedbackActivity : AppCompatActivity() {

    private lateinit var etComment: EditText
    private lateinit var btnSend: Button

    // Pon aquí tu URL y KEY de Supabase (igual que en SensorService)
    private val supabaseUrl = "https://uludxmbfkkpeqqymmrfu.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsdWR4bWJma2twZXFxeW1tcmZ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5Mzk3ODMsImV4cCI6MjA2NDUxNTc4M30.Uzk2t-mkbN53i_lVnCZvARJvd30rK4bYDbUYsLusxv0"
    private val tablaFeedback = "feedback" // nombre tabla feedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        etComment = findViewById(R.id.etComment)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            val comentario = etComment.text.toString().trim()
            if (comentario.isEmpty()) {
                Toast.makeText(this, "Por favor escribe un comentario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarFeedback(comentario)
        }
    }

    private fun enviarFeedback(comentario: String) {
        val nowISO = java.time.Instant.now().toString()
        val json = JSONObject().apply {
            put("comentario", comentario)
            put("fecha", nowISO) // Opcional, timestamp
        }

        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$supabaseUrl/rest/v1/$tablaFeedback")
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=minimal")
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FeedbackActivity, "Gracias por tu feedback", Toast.LENGTH_SHORT).show()
                        etComment.text.clear()
                    } else {
                        Toast.makeText(this@FeedbackActivity, "Error al enviar el feedback", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FeedbackActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
