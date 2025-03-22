package com.example.eventgen

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventgen.navigation.Screen
import com.example.eventgen.ui.theme.EventGenTheme
import androidx.activity.viewModels  // Add this import
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
private const val STORAGE_PERMISSION_CODE = 101
class MainActivity : ComponentActivity() {
    private val viewModel: CalViewModel by viewModels()

    private fun checkStoragePermission() {
        // Add logging
        Log.d("MainActivity", "Checking storage permission")
        
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Add logging before requesting permission
            Log.d("MainActivity", "Storage permission not granted, requesting...")
            
            // Request permission only if activity is in foreground
            if (!isFinishing && !isDestroyed) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                Log.w("MainActivity", "Skipping permission request - activity not in foreground")
            }
        } else {
            Log.d("MainActivity", "Storage permission already granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set context before checking permissions
        Log.d("MainActivity", "Setting ViewModel context")
        viewModel.setContext(this)
        
        // Move permission check after content is set
        setContent {
            EventGenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Main.route
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(
                                onDebugClick = {
                                    navController.navigate(Screen.Debug.route)
                                }
                            )
                        }
                        composable(Screen.Debug.route) {
                            CalScreen()
                        }
                    }
                }
            }
        }
        
        // Check permission after UI is ready
        checkStoragePermission()
    }
}

// New TextSelectionActivity to handle selected text
class TextSelectionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("TextSelectionActivity", "onCreate started")
        
        // Get ViewModel instance and set context
        val app = applicationContext as? App
        Log.d("TextSelectionActivity", "App instance: ${app?.hashCode()}")
        
        // Fix the conflicting declaration by using a different variable name
        val appViewModel = app?.retrieveViewModel()
        Log.d("TextSelectionActivity", "Retrieved viewModel instance: ${appViewModel?.hashCode()}")
        
        if (appViewModel == null) {
            Log.e("TextSelectionActivity", "Failed to get ViewModel instance")
            finish()
            return
        }
        
        // Set context before using ViewModel
        appViewModel.setContext(this)
        Log.d("TextSelectionActivity", "Context set for ViewModel: ${appViewModel.hashCode()}")

        val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (selectedText.isNotEmpty()) {
            Log.d("TextSelectionActivity", "Processing text: ${selectedText.take(50)}...")
            
            // Pass both the text AND the ViewModel instance
            updateSelectedText(selectedText, appViewModel)

            val returnIntent = Intent()
            returnIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, selectedText)
            setResult(Activity.RESULT_OK, returnIntent)

            Toast.makeText(this, "Processing event details", Toast.LENGTH_SHORT).show()
        } else {
            Log.w("TextSelectionActivity", "No text selected")
        }

        finish()
    }
}
