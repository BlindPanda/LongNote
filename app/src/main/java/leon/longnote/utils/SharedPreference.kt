package leon.longnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

object SharedPreference {
    val SHARED_PREFS_REMINDTIME = "remindtime"
    val SHARED_PREFS_REMIND = "remind"


    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_REMINDTIME,
                Context.MODE_PRIVATE)
    }

    @SuppressLint("NewApi")
    fun setSharedPreference(context: Context, key: String, value: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(key, value).apply()
    }

    @SuppressLint("NewApi")
    fun setSharedPreference(context: Context, key: String, value: Long) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putLong(key, value).apply()
    }

    @SuppressLint("NewApi")
    fun setSharedPreference(context: Context, key: String, value: Boolean) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @SuppressLint("NewApi")
    fun setSharedPreference(context: Context, key: String, value: Int) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getSharedPreference(context: Context, key: String, defaultValue: String): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(key, defaultValue)
    }

    fun getSharedPreference(context: Context, key: String, defaultValue: Long): Long {
        val prefs = getSharedPreferences(context)
        return prefs.getLong(key, defaultValue)
    }

    fun getSharedPreference(context: Context, key: String, defaultValue: Int): Int {
        val prefs = getSharedPreferences(context)
        return prefs.getInt(key, defaultValue)
    }

    fun getSharedPreference(context: Context, key: String, defaultValue: Boolean): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(key, defaultValue)
    }
}
