package com.durranitech.salahsync.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salahsync_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_USER_ROLE = stringPreferencesKey("pref_user_role")
        private val KEY_USER_ID = stringPreferencesKey("pref_user_id")
    }

    suspend fun saveUserRole(userRole: String){
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ROLE] = userRole
        }
    }

     fun readUserRole(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_USER_ROLE]
        }
    }
}
