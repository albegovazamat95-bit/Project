package com.example.product1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.reflect.TypeToken

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val storage = remember { DataStorage(context) }

    val itemListType = object : TypeToken<List<Item>>() {}.type

    // Теперь у нас один единый список всех предметов
    val items = remember {
        val saved = storage.loadData<List<Item>>("items", itemListType)
        if (saved != null) mutableStateListOf(*saved.toTypedArray())
        else mutableStateListOf() // Изначально рюкзак пуст
    }

    val saveAllData = {
        storage.saveData("items", items)
    }

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                navController = navController,
                items = items,
                onSaveNeeded = { saveAllData() }
            )
        }

        composable("edit_item/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""

            EditItemScreen(
                itemId = id,
                items = items,
                onSaveComplete = {
                    saveAllData()
                    navController.popBackStack()
                },
                onBackClick = {
                    saveAllData()
                    navController.popBackStack()
                }
            )
        }
    }
}