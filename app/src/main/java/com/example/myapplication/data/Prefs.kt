package com.example.myapplication.data

import android.content.Context

object Prefs {
    private const val NAME = "summer_start"

    fun summerStarted(context: Context): Boolean =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getBoolean("started", false)

    fun setSummerStarted(context: Context, value: Boolean) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("started", value)
            .apply()
    }
}
