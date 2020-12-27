package com.ak.chatter.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.clear
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.ak.chatter.MyApplication
import com.ak.chatter.util.Constants.DATASTORE_NAME
import kotlinx.coroutines.flow.first

object MyDataStore {

    private var dataStore: DataStore<Preferences> = MyApplication.instance.createDataStore(name = DATASTORE_NAME)

    suspend fun save(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}