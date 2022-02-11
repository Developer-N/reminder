package ir.namoo.reminder

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application(){
    override fun onCreate() {
        super.onCreate()
        Log.e("NAMOO", "onCreate: -----------------------------" )
    }
}