package lumiknit.app.hwik.screen.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

object UserPrefs {
	val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
	val EXAMPLE_CODE = stringPreferencesKey("example_code")
	val EXAMPLE_SWITCH = booleanPreferencesKey("example_switch")
}