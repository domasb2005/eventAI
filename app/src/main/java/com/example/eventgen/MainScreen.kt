package com.example.eventgen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ListItem
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.runtime.CompositionLocalProvider
// Color constants
private val PrimaryYellow = Color(0xFFE9DE20)
private val PrimaryGreen = Color(0xFF4CD964)
private val TextColor = Color(0xFF030303)
private val ShadowColor = Color.Black
private val ButtonBackground = Color.White

// Text style constants
private val MonoTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 14.sp,
    color = TextColor
)

@Composable
fun MainScreen(onDebugClick: () -> Unit) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onSettingsClick = { navController.navigate("settings") },
                onDebugClick = onDebugClick
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onDebugClick = onDebugClick
            )
        }
    }
}


@Composable
fun HomeScreen(onSettingsClick: () -> Unit, onDebugClick: () -> Unit) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ButtonBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add more space at the top
        Spacer(modifier = Modifier.height(180.dp))
        
        Text(
            text = "Event AI",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                color = TextColor
            )
        )
        
        // More space between title and instructions
        Spacer(modifier = Modifier.height(80.dp))
        
        // Instructions as simple text items with proper spacing
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Select a portion of text about the event",
                style = MonoTextStyle
            )
            Text(
                "Tap on 'Create event 📅'",
                style = MonoTextStyle
            )
            Text(
                "Wait for AI to process (internet required)",
                style = MonoTextStyle
            )
            Text(
                "Save the event to your calendar app",
                style = MonoTextStyle
            )
        }
        
        // Replace the weight-based spacer with a fixed height spacer
        Spacer(modifier = Modifier.height(100.dp))
        
        StyledButton(
            text = "SETTINGS",
            onClick = onSettingsClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // GitHub link with monospace font
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { 
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/domasb2005/eventAI"))
                context.startActivity(intent)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "GitHub Repository",
                tint = TextColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "VIEW ON GITHUB",
                style = MonoTextStyle
            )
        }
    }
}

@Composable
fun SettingsScreen(onBackClick: () -> Unit, onDebugClick: () -> Unit) {
    val context = LocalContext.current
    var selectedApi by rememberSaveable { 
        mutableStateOf(getStoredApiChoice(context) ?: "our") 
    }
    var personalApiKey by rememberSaveable { 
        mutableStateOf(getStoredApiKey(context) ?: "") 
    }
    var apiKeyStatus by remember { 
        mutableStateOf<ApiKeyStatus>(
            if (getStoredApiKey(context) != null) ApiKeyStatus.Valid 
            else ApiKeyStatus.Initial
        ) 
    }
    var saveIcsFiles by rememberSaveable {
        mutableStateOf(getIcsSavePreference(context))
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ButtonBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add the spacer at the top of the Column
        Spacer(modifier = Modifier.height(180.dp))

        Text(
            text = "API Key",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                color = TextColor  // Changed from Color(0xFF030303)
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectableGroup()
            ) {
                RadioButton(
                    selected = selectedApi == "our",
                    onClick = { 
                        selectedApi = "our"
                        saveApiChoice(context, "our")
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = PrimaryYellow,  // Changed from Color(0xFFE9DE20)
                        unselectedColor = TextColor     // Changed from Color(0xFF030303)
                    )
                )
                Text(
                    text = "Use our Gemini API key",
                    style = MonoTextStyle  // Changed from inline TextStyle
                )
            }
            
            // Second radio button
            Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.selectableGroup()
            ) {
            RadioButton(
            selected = selectedApi == "personal",
            onClick = { 
            selectedApi = "personal"
            saveApiChoice(context, "personal")
            },
            colors = RadioButtonDefaults.colors(
            selectedColor = PrimaryYellow,  // Changed from Color(0xFFE9DE20)
            unselectedColor = TextColor     // Changed from Color(0xFF030303)
            )
            )
            Text(
            text = "Use personal Gemini API key",
            style = MonoTextStyle,  // Changed from inline TextStyle
            )
            }
            
            if (selectedApi == "personal") {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Define custom text selection colors
                val customTextSelectionColors = TextSelectionColors(
                    handleColor = TextColor,
                    backgroundColor = PrimaryYellow.copy(alpha = 0.3f)
                )
                
                // Wrap the TextField with CompositionLocalProvider
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    OutlinedTextField(
                        value = personalApiKey,
                        onValueChange = { 
                            personalApiKey = it
                            if (apiKeyStatus is ApiKeyStatus.Valid) {
                                apiKeyStatus = ApiKeyStatus.Initial
                                clearStoredApiKey(context)
                            }
                        },
                        label = { 
                            Text(
                                "Enter your API key",
                                style = MonoTextStyle
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MonoTextStyle,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = when {
                                personalApiKey.isEmpty() -> TextColor
                                apiKeyStatus is ApiKeyStatus.Valid -> TextColor
                                else -> Color.Red
                            },
                            unfocusedBorderColor = when {
                                personalApiKey.isEmpty() -> TextColor
                                apiKeyStatus is ApiKeyStatus.Valid -> TextColor
                                else -> Color.Red
                            },
                            cursorColor = TextColor
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Validate button with same style as SETTINGS button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        // Shadow box behind the button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = 2.dp, y = 2.dp)
                                .background(Color.Black)
                        )
                        
                        // Actual button with border
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RectangleShape
                                )
                                .background(
                                    if (apiKeyStatus is ApiKeyStatus.Valid) 
                                        PrimaryGreen     // Changed from Color(0xFF4CD964)
                                    else 
                                        PrimaryYellow    // Changed from Color(0xFFE9DE20)
                                )
                                .clickable(
                                    enabled = personalApiKey.length == 39 && apiKeyStatus !is ApiKeyStatus.Validating
                                ) { 
                                    scope.launch {
                                        apiKeyStatus = ApiKeyStatus.Validating
                                        try {
                                            val isValid = validateApiKey(personalApiKey)
                                            if (isValid) {
                                                saveApiKey(context, personalApiKey)
                                                saveApiChoice(context, "personal")
                                                apiKeyStatus = ApiKeyStatus.Valid
                                            } else {
                                                apiKeyStatus = ApiKeyStatus.Invalid("Invalid API key")
                                            }
                                        } catch (e: Exception) {
                                            apiKeyStatus = ApiKeyStatus.Invalid(e.message ?: "Validation failed")
                                        }
                                    }
                                }
                        ) {
                            Text(
                                when (apiKeyStatus) {
                                    is ApiKeyStatus.Validating -> "VALIDATING..."
                                    is ApiKeyStatus.Valid -> "API KEY IS VALID"
                                    else -> "VALIDATE API KEY"
                                },
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    color = TextColor,  // Changed from Color(0xFF030303)
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    
                    // Get API key button with white background
                    Box(
                        modifier = Modifier
                            .width(158.dp)
                            .height(38.dp)
                    ) {
                        // Shadow box behind the button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = 2.dp, y = 2.dp)
                                .background(Color.Black)
                        )
                        
                        // Actual button with border
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RectangleShape
                                )
                                .background(Color.White)  // Always white background
                                .clickable { 
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/apikey"))
                                    context.startActivity(intent)
                                }
                        ) {
                            Text(
                                "GET API KEY",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    color = TextColor,  // Changed from Color(0xFF030303)
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (apiKeyStatus) {
                    is ApiKeyStatus.Invalid -> {
                        Text(
                            text = (apiKeyStatus as ApiKeyStatus.Invalid).message,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = Color.Red
                            )
                        )
                    }
                    else -> {}
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Checkbox(
                checked = saveIcsFiles,
                onCheckedChange = { checked ->
                    saveIcsFiles = checked
                    saveIcsSavePreference(context, checked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = TextColor,
                    uncheckedColor = TextColor,
                    checkmarkColor = ButtonBackground,
                    disabledCheckedColor = PrimaryYellow.copy(alpha = 0.5f),
                    disabledUncheckedColor = TextColor.copy(alpha = 0.5f),
                    disabledIndeterminateColor = PrimaryYellow.copy(alpha = 0.5f)
                )
            )
            Text(
                text = "Save .ics files to downloads",
                style = MonoTextStyle,
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Debug button with same style
        Box(
            modifier = Modifier
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
                        color = Color.Black,
                        shape = RectangleShape
                    )
                    .background(Color(0xFFE9DE20))
                    .clickable { onDebugClick() }
            ) {
                Text(
                    "DEBUG VIEW",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = TextColor,  // Changed from Color(0xFF030303)
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Back button with white background
        Box(
            modifier = Modifier
                .width(158.dp)
                .height(38.dp)
        ) {
            // Shadow box behind the button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(Color.Black)
            )
            
            // Actual button with border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RectangleShape
                    )
                    .background(Color.White)
                    .clickable { onBackClick() }
            ) {
                Text(
                    "BACK",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = TextColor,  // Changed from Color(0xFF030303)
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        // Remove the GitHub link section that was here
        // Spacer and Row with GitHub link have been removed
    }
}

@Composable
private fun ApiChoiceRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectableGroup()
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryYellow,
                unselectedColor = TextColor
            )
        )
        Text(
            text = text,
            style = MonoTextStyle,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
// Add this after the MainScreen composable and before the HomeScreen composable

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