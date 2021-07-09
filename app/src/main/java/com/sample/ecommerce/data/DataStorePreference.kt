package com.sample.ecommerce.data

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.clear
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreference(private val preferences: DataStore<Preferences>) {

    suspend fun saveBoolean(key: String, save: Boolean) {
        preferences.edit {
            it[preferencesKey<Boolean>(key)] = save
        }
    }

    suspend fun saveString(key: String, save: String) {
        preferences.edit {
            it[preferencesKey<String>(key)] = save
        }
    }

    suspend fun saveDouble(key: String, save: Double) {
        preferences.edit {
            it[preferencesKey<Double>(key)] = save
        }
    }

    fun fetchBoolean(key: String) = preferences.data.map {
        it[preferencesKey<Boolean>(key)] ?: false
    }

    fun fetchString(key: String) = preferences.data.map {
        it[preferencesKey<String>(key)] ?: ""
    }

    fun fetchDouble(key: String) = preferences.data.map {
        it[preferencesKey<Double>(key)] ?: 0.0
    }

}