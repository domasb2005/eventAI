package com.example.eventgen.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

// App-wide color definitions
object AppColors {
    // Light theme colors
    private val LightPrimaryYellow = Color(0xFFE9DE20)
    private val LightPrimaryGreen = Color(0xFF4CD964)
    private val LightTextColor = Color(0xFF030303)
    private val LightShadowColor = Color.Black
    private val LightButtonBackground = Color.White
    private val LightContentBackground = Color.White
    private val LightDebugBackground = LightPrimaryYellow

    
    // Dark theme colors
    private val DarkPrimaryYellow = Color(0xFF8E1616)
    private val DarkPrimaryGreen = Color(0xFF2E8B57)
    private val DarkTextColor = Color.White
    private val DarkShadowColor = Color.Black
    private val DarkButtonBackground = Color(0xFF1D1616)
    private val DarkContentBackground = Color(0xFF1D1616)
    private val DarkDebugBackground = Color(0xFF1D1616)
    
    // Theme state
    private var isDarkTheme by mutableStateOf(false)
    
    // Current theme colors
    val PrimaryYellow get() = if (isDarkTheme) DarkPrimaryYellow else LightPrimaryYellow
    val PrimaryGreen get() = if (isDarkTheme) DarkPrimaryGreen else LightPrimaryGreen
    val TextColor get() = if (isDarkTheme) DarkTextColor else LightTextColor
    val ShadowColor get() = if (isDarkTheme) DarkShadowColor else LightShadowColor
    val ButtonBackground get() = if (isDarkTheme) DarkButtonBackground else LightButtonBackground
    val ContentBackground get() = if (isDarkTheme) DarkContentBackground else LightContentBackground
    val DebugBackground get() = if (isDarkTheme) DarkDebugBackground else LightDebugBackground
    
    // Common text style
    val MonoTextStyle get() = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        color = TextColor
    )
    
    // Function to toggle theme
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
    
    // Function to check current theme
    fun isDark(): Boolean = isDarkTheme
}