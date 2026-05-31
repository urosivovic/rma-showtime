package rs.edu.raf.rma.android

import android.app.Application
import android.util.Log

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Test", "App:onCreate()")
    }
}
