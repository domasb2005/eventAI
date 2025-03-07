package com.example.eventgen

import android.app.Activity
import android.content.Intent
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

class MainActivity : ComponentActivity() {
    private val viewModel: CalViewModel by viewModels()

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setContext(this)
        checkStoragePermission()
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
    }
}

// New TextSelectionActivity to handle selected text
class TextSelectionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (selectedText.isNotEmpty()) {
            // Pass the text to CalScreen and trigger event extraction
            updateSelectedText(selectedText)

            val returnIntent = Intent()
            returnIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, selectedText)
            setResult(Activity.RESULT_OK, returnIntent)

            Toast.makeText(this, "Processing event details", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
