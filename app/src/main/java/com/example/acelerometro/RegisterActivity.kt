package com.example.acelerometro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (UserStorage.userExists(username)) {
                Toast.makeText(this, "Usuario ya registrado", Toast.LENGTH_SHORT).show()
            } else {
                UserStorage.addUser(username, password)
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                finish() // Regresa a LoginActivity
            }
        }
    }
}
