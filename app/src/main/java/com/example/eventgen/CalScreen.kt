package com.example.eventgen

import android.util.Log
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import com.example.eventgen.ui.theme.AppColors

// Remove these color constants as they're now in AppColors
// private val PrimaryYellow = Color(0xFFE9DE20)
// private val PrimaryGreen = Color(0xFF4CD964)
// private val TextColor = Color(0xFF030303)
// private val ShadowColor = Color.Black
// private val ButtonBackground = AppColors.ContentBackground

// Remove this text style constant as it's now in AppColors
// private val MonoTextStyle = TextStyle(
//     fontFamily = FontFamily.Monospace,
//     fontSize = 14.sp,
//     color = TextColor
// )

private var selectedText by mutableStateOf("")
private var calViewModel = CalViewModel()

@Composable
fun CalScreen(viewModel: CalViewModel = calViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Set context for the ViewModel
    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }
    
    // In CalScreen function
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.DebugBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Section 1: Selected Text
        Text(
            text = "Selected Text:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextColor
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // White box for selected text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 2.dp, end = 2.dp)
        ) {
            // Shadow box - drawn first
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(AppColors.ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.ContentBackground)
                    .border(1.dp, Color.Black, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedText,
                    style = AppColors.MonoTextStyle
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Section 2: Event Details
        Text(
            text = "Event Details:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextColor
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // For UiState.Initial
        when (uiState) {
            is UiState.Initial -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    // Shadow box - drawn first
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(AppColors.ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.ContentBackground)
                            .border(1.dp, Color.Black, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No event details extracted yet",
                            style = AppColors.MonoTextStyle
                        )
                    }
                }
            }
            
            // For UiState.Loading
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    // Shadow box - drawn first
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(AppColors.ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.ContentBackground)
                            .border(1.dp, Color.Black, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Processing...",
                            style = AppColors.MonoTextStyle
                        )
                    }
                }
            }
            
            // For UiState.Success
            is UiState.Success -> {
                val result = (uiState as UiState.Success).result
                
                // White box for raw output
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    // Shadow box - drawn first
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(AppColors.ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.ContentBackground)
                            .border(1.dp, Color.Black, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = result.rawOutput,
                            style = AppColors.MonoTextStyle
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Section 3: Generated ICS
                Text(
                    text = "Generated ICS:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextColor
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // White box for ICS content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    // Shadow box - drawn first
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(AppColors.ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.ContentBackground)
                            .border(1.dp, Color.Black, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = result.icsContent,
                            style = AppColors.MonoTextStyle
                        )
                    }
                }
            }
            
            // For UiState.Error
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    // Shadow box - drawn first
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(AppColors.ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.ContentBackground)
                            .border(1.dp, Color.Black, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${(uiState as UiState.Error).errorMessage}",
                            style = AppColors.MonoTextStyle.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
// Update the function signature to accept a ViewModel parameter
fun updateSelectedText(text: String, viewModel: CalViewModel? = null) {
    Log.d("CalScreen", "updateSelectedText called with text: ${text.take(30)}...")
    selectedText = text
    
    // Use the provided viewModel if available
    if (viewModel != null) {
        Log.d("CalScreen", "Using provided ViewModel instance: ${viewModel.hashCode()}")
        viewModel.extractEventDetails(text)
    } else {
        Log.d("CalScreen", "Using default calViewModel instance: ${calViewModel.hashCode()}")
        calViewModel.extractEventDetails(text)
    }
}

@Preview(showSystemUi = true)
@Composable
fun CalScreenPreview() {
    CalScreen()
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DebugScreen(
    selectedText: String,
    eventJson: String,
    icsContent: String,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // In DebugScreen function
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryYellow)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selected Text:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextColor
            )
        )
        
        // White box for selected text with shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 2.dp, end = 2.dp)
        ) {
            // Shadow box - drawn first
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(AppColors.ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.ContentBackground)
                    .border(1.dp, Color.Black, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedText,
                    style = AppColors.MonoTextStyle
                )
            }
        }
        
        // Event Details Section
        Text(
            text = "Event Details (JSON):",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextColor
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // Event Details Section in DebugScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 2.dp, end = 2.dp)
                .padding(vertical = 8.dp)
        ) {
            // Shadow box - drawn first
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(AppColors.ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.ContentBackground)
                    .border(1.dp, Color.Black, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = eventJson,
                    style = AppColors.MonoTextStyle
                )
            }
        }
        
        // ICS Content Section
        Text(
            text = "ICS Content:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextColor
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // ICS Content Section in DebugScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 2.dp, end = 2.dp)
                .padding(vertical = 8.dp)
        ) {
            // Shadow box - drawn first
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(AppColors.ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.ContentBackground)
                    .border(1.dp, Color.Black, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = icsContent,
                    style = AppColors.MonoTextStyle
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
    }
}
