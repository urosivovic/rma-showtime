package rs.edu.raf.rma.showtime.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

internal const val TokenDataStoreFileName = "showtime.preferences_pb"

fun createTokenDataStore(
    producePath: () -> String,
): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            producePath().toPath()
        },
    )

expect fun createPlatformTokenDataStore(): DataStore<Preferences>
