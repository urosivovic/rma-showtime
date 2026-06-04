package rs.edu.raf.rma.showtime.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createPlatformTokenDataStore(): DataStore<Preferences> =
    createTokenDataStore(
        producePath = {
            val directory = File(
                System.getProperty("user.home"),
                ".showtime",
            )
            directory.mkdirs()
            File(directory, TokenDataStoreFileName).absolutePath
        },
    )
