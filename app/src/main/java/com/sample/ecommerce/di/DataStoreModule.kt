package com.sample.ecommerce.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class DataStoreModule(private val context: Context) {
    fun providePreference(): DataStore<Preferences> {
        return context.createDataStore(
            name = "com.sample.ecommerce"
        )
    }
}
