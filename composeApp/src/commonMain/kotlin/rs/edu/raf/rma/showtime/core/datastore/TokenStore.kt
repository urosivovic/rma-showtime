package rs.edu.raf.rma.showtime.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenStore(
    private val dataStore: DataStore<Preferences>,
) {
    private val accessTokenKey = stringPreferencesKey("access_token")

    val accessToken: Flow<String?> =
        dataStore.data
            .map { preferences -> preferences[accessTokenKey] }
            .distinctUntilChanged()

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = token
        }
    }

    suspend fun clearAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }

    suspend fun currentAccessToken(): String? = accessToken.first()
}
