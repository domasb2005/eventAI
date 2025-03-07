package com.example.eventgen.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Debug : Screen("debug")
}