package com.example.eventgen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import android.util.Log
import java.util.Locale
import java.util.Date
import android.widget.Toast
import java.util.TimeZone

// Modified to include both raw output and ICS content
data class EventResult(val rawOutput: String, val icsContent: String)

class CalViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel by lazy {
        _context?.let { context ->
            val apiKey = getStoredApiKey(context) ?: BuildConfig.apiKey
            // Log masked API key
            val maskedKey = apiKey.take(4) + "*".repeat(apiKey.length - 8) + apiKey.takeLast(4)
            Log.d("CalViewModel", "Using API Key: $maskedKey")
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
        } ?: throw IllegalStateException("Context not set. Call setContext() before using the ViewModel")
    }

    private var _context: Context? = null
    
    fun setContext(context: Context) {
        Log.d("CalViewModel", "Setting context: $context")
        _context = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendToCalendar(eventData: Map<String, String>) {
        _context?.let { context ->
            try {
                // Parse date and time
                val dateStr = eventData["date"] ?: return
                val startTimeStr = eventData["startTime"] ?: return
                val endTimeStr = eventData["endTime"] ?: return
                
                // Validate date and time formats
                if (dateStr == "N/A" || startTimeStr == "N/A" || endTimeStr == "N/A") {
                    throw Exception("Missing required date or time information")
                }
                
                // Get event name, with fallback
                val eventName = eventData["name"] ?: "Untitled Event"
                
                // Get timezone, with fallback to device default
                val timezone = when (val tz = eventData["timezone"]) {
                    null, "N/A" -> TimeZone.getDefault()
                    else -> {
                        if (tz.startsWith("UTC")) {
                            // Handle UTC offset format
                            val offset = tz.substring(3) // Remove "UTC" prefix
                            TimeZone.getTimeZone("GMT$offset")
                        } else {
                            // Handle IANA format
                            TimeZone.getTimeZone(tz)
                        }
                    }
                }
                
                // Validate timezone
                if (timezone.id == "GMT" && eventData["timezone"] != null && eventData["timezone"] != "N/A") {
                    Log.w("CalViewModel", "Invalid timezone format: ${eventData["timezone"]}, using device default")
                    TimeZone.getDefault()
                }

                Log.d("CalViewModel", "Event data: $eventData")
                Log.d("CalViewModel", "Event name: $eventName")
                Log.d("CalViewModel", "Using timezone: ${timezone.id}")

                // Combine date and time and convert to milliseconds
                val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'").apply {
                    timeZone = timezone
                }
                
                val startMillis = sdf.parse("${dateStr}T${startTimeStr}Z")?.time ?: return
                val endMillis = sdf.parse("${dateStr}T${endTimeStr}Z")?.time ?: return
                
                // Create calendar intent
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                    putExtra(CalendarContract.Events.TITLE, eventName)
                    putExtra(CalendarContract.Events.DESCRIPTION, eventData["description"] ?: "")
                    putExtra(CalendarContract.Events.EVENT_LOCATION, eventData["location"] ?: "")
                    putExtra(CalendarContract.Events.EVENT_TIMEZONE, timezone.id)
                    putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                }
            
                // Use FLAG_ACTIVITY_NEW_TASK for launching from non-activity context
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                
                // Launch the intent directly without a chooser
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("CalViewModel", "Calendar intent error: ${e.message}", e)
                _uiState.value = UiState.Error("Failed to create calendar event: ${e.message}")
            }
        } ?: run {
            Log.e("CalViewModel", "Context is null when trying to launch calendar")
            _uiState.value = UiState.Error("Failed to create calendar event: Context is null")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveIcsFile(icsContent: String) {
        _context?.let { context ->
            try {
                // Check if saving is enabled
                if (!getIcsSavePreference(context)) return

                // Get Downloads directory
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                // Create filename from event name with auto-incrementing number if exists
                val safeName = _lastParsedEvent?.get("name")?.replace(Regex("[^a-zA-Z0-9]"), "_")?.take(30) ?: "event"
                var counter = 0
                var filename: String
                var file: File
                do {
                    filename = if (counter == 0) "$safeName.ics" else "$safeName($counter).ics"
                    file = File(downloadsDir, filename)
                    counter++
                } while (file.exists())

                // Write the file
                FileOutputStream(file).use { output ->
                    output.write(icsContent.toByteArray())
                }

                Log.d("CalViewModel", "ICS file saved: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("CalViewModel", "Error saving ICS file: ${e.message}", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun extractEventDetails(text: String, retryCount: Int = 0) {
        Log.d("CalViewModel", "extractEventDetails called with text: ${text.take(50)}...")
        
        if (_context == null) {
            Log.e("CalViewModel", "Context is null. Stack trace: ", Exception())
            _uiState.value = UiState.Error("Internal error: Context not initialized")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get current date for context
                val today = ZonedDateTime.now()
                val currentDateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                
                val prompt = "Extract event details from this text: \"$text\". Today's date is $currentDateStr. " +
                        "Return JSON with these fields only: {\"name\": \"event name\", \"date\": \"YYYYMMDD\", " +
                        "\"startTime\": \"HHMMSS\", \"endTime\": \"HHMMSS\", \"location\": \"location\", " +
                        "\"url\": \"url\", \"description\": \"description\", \"timezone\": \"IANA format or UTC offset or N/A\"}. " +
                        "If year isn't specified, assume the nearest future date. If endTime isn't specified, " +
                        "estimate based on event type (meetings: 1h, viewings: 30m, concerts: 2-3h). " +
                        "Never return 'N/A' for date, startTime, or endTime fields."
                
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                if (response.text == null) {
                    throw Exception("Failed to get response from AI")
                }
                
                response.text?.let { jsonContent ->
                    try {
                        // Parse the JSON response
                        val eventJson = parseJsonResponse(jsonContent)
                        
                        // Store the parsed event for later use
                        _lastParsedEvent = eventJson
                        
                        // Format as ICS
                        val icsContent = formatAsIcs(eventJson)
                        
                        // Create result with both raw output and ICS content
                        val result = EventResult(jsonContent, icsContent)
                        
                        // Update UI state
                        _uiState.value = UiState.Success(result)
                        
                        // First save ICS if enabled, show toast, then launch calendar
                        viewModelScope.launch(Dispatchers.Main) {
                            _context?.let { context ->
                                // Save ICS file if enabled
                                if (getIcsSavePreference(context)) {
                                    saveIcsFile(icsContent)
                                    Toast.makeText(context, ".ics saved to downloads", Toast.LENGTH_SHORT).show()
                                }
                                // Then launch calendar intent
                                sendToCalendar(eventJson)
                            }
                        }
                    } catch (e: Exception) {
                        handleError(e, text, retryCount)
                    }
                }
            } catch (e: Exception) {
                handleError(e, text, retryCount)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleError(error: Exception, text: String, currentRetryCount: Int) {
        Log.e("CalViewModel", "Error extracting event details: ${error.message}", error)
        
        val errorMessage = when {
            error.message?.contains("Unparseable date") == true -> 
                "Failed to create calendar event: Invalid date format"
            error.message?.contains("Missing required") == true -> 
                "Failed to create calendar event: Missing date or time information"
            else -> "Try again with more text"
        }
        
        if (currentRetryCount < 3) {
            // Retry after a short delay
            viewModelScope.launch {
                kotlinx.coroutines.delay(1000) // 1 second delay between retries
                extractEventDetails(text, currentRetryCount + 1)
            }
        } else {
            _uiState.value = UiState.Error(errorMessage)
        }
    }

    // Add this property to store the last parsed event
    private var _lastParsedEvent: Map<String, String>? = null
    
    // Add this function to access the last parsed event
    fun getLastParsedEvent(): Map<String, String>? = _lastParsedEvent

    private fun parseJsonResponse(jsonContent: String): Map<String, String> {
        // Simple JSON parsing - in a real app, use a proper JSON library
        val result = mutableMapOf<String, String>()
        
        try {
            // Try to find the JSON object in the response
            val jsonPattern = "\\{.*\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
            val jsonMatch = jsonPattern.find(jsonContent)?.value ?: jsonContent
            
            // Clean up the JSON string to handle potential formatting issues
            val cleanJson = jsonMatch
                .trim()
                .removePrefix("{")
                .removeSuffix("}")
                .replace("\n", "")
                .replace("\\\"", "\"")
            
            // Debug the JSON content
            Log.d("CalViewModel", "Raw JSON: $jsonContent")
            Log.d("CalViewModel", "Extracted JSON: $jsonMatch")
            Log.d("CalViewModel", "Cleaned JSON: $cleanJson")
            
            // Split by commas that are not inside quotes
            val regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()
            val pairs = cleanJson.split(regex)
            
            for (pair in pairs) {
                try {
                    // Split by first colon that's not inside quotes
                    val colonIndex = pair.indexOf(':')
                    if (colonIndex != -1) {
                        val key = pair.substring(0, colonIndex).trim().removeSurrounding("\"")
                        val value = pair.substring(colonIndex + 1).trim().removeSurrounding("\"")
                        
                        // Store in the map
                        result[key] = value
                        Log.d("CalViewModel", "Parsed: $key = $value")
                    }
                } catch (e: Exception) {
                    Log.e("CalViewModel", "Error parsing pair: $pair", e)
                }
            }
        } catch (e: Exception) {
            Log.e("CalViewModel", "Error parsing JSON: ${e.message}", e)
        }
        
        // Store the parsed event for later use
        _lastParsedEvent = result
        
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatAsIcs(eventData: Map<String, String>): String {
        val timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        )
        
        // Validate required date fields
        val date = eventData["date"] ?: ""
        val startTime = eventData["startTime"] ?: ""
        val endTime = eventData["endTime"] ?: ""
        
        if (date == "N/A" || date.isEmpty() || 
            startTime == "N/A" || startTime.isEmpty() || 
            endTime == "N/A" || endTime.isEmpty()) {
            return "ERROR: Missing required date or time information"
        }
        
        // Get event name and timezone with fallbacks
        val eventName = eventData["name"] ?: "Untitled Event"
        val timezone = when (val tz = eventData["timezone"]) {
            null, "N/A" -> TimeZone.getDefault().id
            else -> {
                if (tz.startsWith("UTC")) {
                    // Convert UTC offset to GMT for ICS format
                    "GMT" + tz.substring(3)
                } else {
                    tz
                }
            }
        }
        
        return """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//Event Generator//EN
            BEGIN:VEVENT
            UID:${System.currentTimeMillis()}@eventgen.example.com
            DTSTAMP:$timestamp
            DTSTART;TZID=$timezone:${eventData["date"] ?: ""}T${eventData["startTime"] ?: ""}
            DTEND;TZID=$timezone:${eventData["date"] ?: ""}T${eventData["endTime"] ?: ""}
            SUMMARY:$eventName
            LOCATION:${eventData["location"] ?: ""}
            URL:${eventData["url"] ?: ""}
            DESCRIPTION:${eventData["description"] ?: ""}
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()
    }
}