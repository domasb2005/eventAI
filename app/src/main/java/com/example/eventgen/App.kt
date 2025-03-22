package com.example.eventgen
import android.util.Log
import android.app.Application

class App : Application() {
    lateinit var viewModel: CalViewModel
        private set
    
    override fun onCreate() {
        super.onCreate()
        viewModel = CalViewModel()
        viewModel.setContext(applicationContext)
        Log.d("App", "ViewModel initialized with application context: ${viewModel.hashCode()}")
    }
    
    // Renamed from getViewModel to retrieveViewModel to avoid signature clash
    fun retrieveViewModel(): CalViewModel {
        Log.d("App", "retrieveViewModel called, returning instance: ${viewModel.hashCode()}")
        return viewModel
    }
}