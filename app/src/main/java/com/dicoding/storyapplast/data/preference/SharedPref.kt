package com.dicoding.storyapplast.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name ="token_session")

class SharedPref private constructor(private val  dataStore: DataStore<Preferences>){

    companion object {
        @Volatile
        private var instance : SharedPref? = null
        fun getInnstance(dataStore: DataStore<Preferences>) : SharedPref {
            return instance ?: synchronized(this) {
                val getInstance = SharedPref(dataStore)
                instance = getInstance
                getInstance
            }
        }

        private val GET_TOKEN = stringPreferencesKey("token")
        private val GET_USERID = stringPreferencesKey("userId")
        private val GET_NAME = stringPreferencesKey("name")
        private val GET_ISLOGIN = booleanPreferencesKey("isLogin")
    }

    suspend fun setToken(shared: SharedPrefModel) {
        dataStore.edit { pref ->
            pref[GET_TOKEN] = shared.token
            pref[GET_USERID] = shared.userId
            pref[GET_NAME] = shared.name
            pref[GET_ISLOGIN] = true
        }
    }

    fun getToken(): Flow<SharedPrefModel> {
        return  dataStore.data.map { pref ->
            SharedPrefModel(
                pref[GET_TOKEN] ?: "",
                pref[GET_USERID] ?: "",
                pref[GET_NAME] ?: "",
                pref[GET_ISLOGIN] ?: false
            )
        }
    }

    suspend fun clearToken() {
        dataStore.edit { it.clear() }
    }
}