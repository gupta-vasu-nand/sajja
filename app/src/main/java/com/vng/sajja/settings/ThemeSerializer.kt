package com.vng.sajja.settings

import com.google.gson.Gson

object ThemeSerializer {
    private val gson = Gson()

    fun export(settings: WallpaperSettings): String =
        gson.toJson(settings)

    fun import(json: String): WallpaperSettings =
        gson.fromJson(json, WallpaperSettings::class.java)
}