package rs.edu.raf.rma.android

import android.app.Application
import android.util.Log
import rs.edu.raf.rma.showtime.core.datastore.setShowtimeDataStoreContext

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setShowtimeDataStoreContext(this)
        Log.d("Test", "App:onCreate()")
    }
}
