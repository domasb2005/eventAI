package com.example.eventgen

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ApiKeyStatus {
    object Initial : ApiKeyStatus()
    object Validating : ApiKeyStatus()
    object Valid : ApiKeyStatus()
    data class Invalid(val message: String) : ApiKeyStatus()
}

private const val API_KEY_PREFS = "api_key_prefs"
private const val PERSONAL_API_KEY = "personal_api_key"

suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val testModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
        
        // Make a simple test prompt
        val response = testModel.generateContent("Test")
        return@withContext response.text != null
    } catch (e: Exception) {
        throw e
    }
}

fun saveApiKey(context: Context, apiKey: String) {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(PERSONAL_API_KEY, apiKey).apply()
}

fun getStoredApiKey(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    return sharedPreferences.getString(PERSONAL_API_KEY, null)
}

private const val API_CHOICE = "api_choice"

fun saveApiChoice(context: Context, choice: String) {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(API_CHOICE, choice).apply()
}

fun getStoredApiChoice(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    return sharedPreferences.getString(API_CHOICE, null)
}

// Add this function to clear the stored API key
fun clearStoredApiKey(context: Context) {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    sharedPreferences.edit().remove(PERSONAL_API_KEY).apply()
}

private const val ICS_SAVE_PREF = "ics_save_preference"

fun saveIcsSavePreference(context: Context, shouldSave: Boolean) {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean(ICS_SAVE_PREF, shouldSave).apply()
}

fun getIcsSavePreference(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences(API_KEY_PREFS, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(ICS_SAVE_PREF, false)
}