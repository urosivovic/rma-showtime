package rs.edu.raf.rma.showtime.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private lateinit var applicationContext: Context

fun setShowtimeDataStoreContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createPlatformTokenDataStore(): DataStore<Preferences> =
    createTokenDataStore(
        producePath = {
            check(::applicationContext.isInitialized) {
                "Showtime DataStore context is not initialized."
            }
            applicationContext.filesDir
                .resolve(TokenDataStoreFileName)
                .absolutePath
        },
    )
