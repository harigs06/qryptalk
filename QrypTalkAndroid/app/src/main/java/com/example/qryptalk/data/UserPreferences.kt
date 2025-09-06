package com.example.qryptalk.data



import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.qryptalk.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension for datastore
val Context.userDataStore by preferencesDataStore(name = "user_prefs")

object UserPreferencesKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PROFILE = stringPreferencesKey("user_profile") // store URL or resource name
}

class UserPreferences(private val context: Context) {

    // Save user
    suspend fun saveUser(id: String, name: String, email: String, profilePic: String) {
        context.userDataStore.edit { prefs ->
            prefs[UserPreferencesKeys.USER_ID] = id
            prefs[UserPreferencesKeys.USER_NAME] = name
            prefs[UserPreferencesKeys.USER_EMAIL] = email
            prefs[UserPreferencesKeys.USER_PROFILE] = profilePic
        }
    }

    // Get user as Flow
    val userFlow: Flow<User?> = context.userDataStore.data.map { prefs ->
        val id = prefs[UserPreferencesKeys.USER_ID] ?: return@map null
        val name = prefs[UserPreferencesKeys.USER_NAME] ?: ""
        val email = prefs[UserPreferencesKeys.USER_EMAIL] ?: ""
        val profile = prefs[UserPreferencesKeys.USER_PROFILE] ?: ""
        User(id, name, email, 0) // fallback profile drawable
    }

    // Clear user
    suspend fun clearUser() {
        context.userDataStore.edit { it.clear() }
    }
}
