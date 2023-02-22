package com.example.products.localstorage
import android.content.Context
import android.content.SharedPreferences
class Prefs(context : Context){
    companion object {
        private const val PREFS_FILENAME = "products_prefs"
    }
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
}