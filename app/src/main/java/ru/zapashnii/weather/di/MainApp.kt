package ru.zapashnii.weather.di

import android.app.Application

class MainApp : Application() {

    companion object {
        lateinit var instance: MainApp

    }
        lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.create()
    }
}