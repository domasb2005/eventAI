package com.example.eventgen

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



// Add these color constants to match those in MainScreen.kt
private val PrimaryYellow = Color(0xFFE9DE20)
private val PrimaryGreen = Color(0xFF4CD964)
private val TextColor = Color(0xFF030303)
private val ShadowColor = Color.Black
private val ButtonBackground = Color.White

// Add this text style constant to match MainScreen.kt
private val MonoTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 14.sp,
    color = TextColor
)

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
            .background(PrimaryYellow)
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
                color = TextColor
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // White box for selected text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // This is key - it makes the Box take the height of its content
                .padding(bottom = 2.dp, end = 2.dp) // Add padding for shadow
        ) {
            // Shadow box - drawn first
            Box(
                modifier = Modifier
                    .fillMaxSize() // Now this will match the parent Box which has IntrinsicSize.Min
                    .offset(x = 2.dp, y = 2.dp)
                    .background(ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, TextColor, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedText,
                    style = MonoTextStyle
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
                color = TextColor
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
                            .background(ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(1.dp, TextColor, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No event details extracted yet",
                            style = MonoTextStyle
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
                            .background(ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(1.dp, TextColor, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Processing...",
                            style = MonoTextStyle
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
                            .background(ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(1.dp, TextColor, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = result.rawOutput,
                            style = MonoTextStyle
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
                        color = TextColor
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
                            .background(ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(1.dp, TextColor, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = result.icsContent,
                            style = MonoTextStyle
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
                            .background(ShadowColor)
                    )
                    
                    // Actual content box with border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(1.dp, TextColor, RectangleShape)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${(uiState as UiState.Error).errorMessage}",
                            style = MonoTextStyle.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun updateSelectedText(text: String) {
    selectedText = text
    calViewModel.extractEventDetails(text)
}

@Preview(showSystemUi = true)
@Composable
fun CalScreenPreview() {
    CalScreen()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun StyledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PrimaryYellow
) {
    Box(
        modifier = modifier
            .width(158.dp)
            .height(38.dp)
    ) {
        // Shadow box behind the button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(ShadowColor)
        )
        
        // Actual button with border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = ShadowColor,
                    shape = RectangleShape
                )
                .background(backgroundColor)
                .clickable { onClick() }
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = TextColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
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
            .background(PrimaryYellow)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selected Text:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TextColor
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
                    .background(ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, TextColor, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedText,
                    style = MonoTextStyle
                )
            }
        }
        
        // Event Details Section
        Text(
            text = "Event Details (JSON):",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TextColor
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
                    .background(ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, TextColor, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = eventJson,
                    style = MonoTextStyle
                )
            }
        }
        
        // ICS Content Section
        Text(
            text = "ICS Content:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TextColor
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
                    .background(ShadowColor)
            )
            
            // Content box - drawn second (on top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, TextColor, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = icsContent,
                    style = MonoTextStyle
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Back button
        StyledButton(
            text = "BACK",
            onClick = onBackClick,
            backgroundColor = ButtonBackground
        )
    }
}
