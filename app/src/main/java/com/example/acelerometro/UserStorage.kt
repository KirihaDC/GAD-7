package com.example.acelerometro

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

object UserStorage {

    private const val PREFS_NAME = "user_prefs"
    private const val USERS_KEY = "usuarios"

    private lateinit var prefs: SharedPreferences
    private val usuariosMap = mutableMapOf<String, String>()

    // Debe ser llamado en la Activity principal para inicializar prefs
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadUsers()
    }

    private fun loadUsers() {
        usuariosMap.clear()
        val jsonString = prefs.getString(USERS_KEY, null)
        jsonString?.let {
            val jsonObject = JSONObject(it)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                usuariosMap[key] = jsonObject.getString(key)
            }
        }
    }

    private fun saveUsers() {
        val jsonObject = JSONObject(usuariosMap as Map<*, *>)
        prefs.edit().putString(USERS_KEY, jsonObject.toString()).apply()
    }

    fun addUser(username: String, password: String) {
        usuariosMap[username] = password
        saveUsers()
    }

    fun userExists(username: String): Boolean {
        return usuariosMap.containsKey(username)
    }

    fun validateUser(username: String, password: String): Boolean {
        return usuariosMap[username] == password
    }
}
