package com.example.product1

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataStorage(context: Context) {
    // Файл, где будут храниться данные
    private val prefs = context.getSharedPreferences("pathfinder_save", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Функция сохранения любого объекта в JSON
    fun saveData(key: String, data: Any) {
        val json = gson.toJson(data)
        prefs.edit().putString(key, json).apply()
    }

    // Функция загрузки объекта из JSON
    fun <T> loadData(key: String, type: Type): T? {
        val json = prefs.getString(key, null) ?: return null
        return gson.fromJson(json, type)
    }
}