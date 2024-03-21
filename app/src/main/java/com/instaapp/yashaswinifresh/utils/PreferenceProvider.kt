package com.instaapp.yashaswinifresh.utils

import android.content.Context

private const val SPLASH_SCREEN_IMAGE_URL = "splash_screen_image_url"

class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val file = "com.example.instauserapp"

    private var sharedPreferences = appContext.getSharedPreferences(file, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun clear() {
        editor.clear()
        editor.commit()
    }

    fun setSplashScreenImageUrl(imageUrl: String) {
        editor.putString(SPLASH_SCREEN_IMAGE_URL, imageUrl)
        editor.apply()
    }

    fun getSplashScreenImageUrl(): String? {
        return sharedPreferences.getString(SPLASH_SCREEN_IMAGE_URL, "N/A")
    }

    fun setStringValue(stringData: String, KEY: String) {
        editor.putString(KEY, stringData)
        editor.apply()
    }

    fun getStringValue(KEY: String): String? {
        return sharedPreferences.getString(KEY, "0")
    }

    fun setIntValue(intData: Int, KEY: String) {
        editor.putInt(KEY, intData)
        editor.apply()
    }

    fun getIntValue(KEY: String): Int {
        return sharedPreferences.getInt(KEY, 0)
    }

    fun setBooleanValue(booleanData: Boolean, KEY: String) {
        editor.putBoolean(KEY, booleanData)
        editor.apply()
    }

    fun getBooleanValue(KEY: String): Boolean {
        return sharedPreferences.getBoolean(KEY, false)
    }


}