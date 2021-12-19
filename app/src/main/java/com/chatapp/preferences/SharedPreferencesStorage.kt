package com.chatapp.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chatapp.util.Constants.IS_REMEMBERED
import com.chatapp.util.Constants.USER_NAME
import com.chatapp.util.Constants.USER_PREFERENCES
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface PreferenceStorage {
    var isRemembered: Boolean
    var userName: String
}

@Singleton
class SharedPreferencesStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val preferences: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
    }

    override var isRemembered by BooleanPreferences(preferences, IS_REMEMBERED, false)
    override var userName by BooleanPreferences.StringPreferences(
        preferences,
        USER_NAME,
        "JOhn Doe"
    )

}

class BooleanPreferences(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }

    class StringPreferences(
        private val preferences: Lazy<SharedPreferences>,
        private val name: String,
        private val defaultValue: String
    ) : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return preferences.value.getString(name, defaultValue) ?: defaultValue
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            preferences.value.edit { putString(name, value) }
        }
    }
}