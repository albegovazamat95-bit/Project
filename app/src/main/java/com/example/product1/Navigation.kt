package com.example.product1

import androidx.compose.runtime.*
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

    val characterListType = object : TypeToken<List<Character>>() {}.type

    val characters = remember {
        mutableStateListOf<Character>().apply {
            val saved = storage.loadData<List<Character>>("characters", characterListType)
            if (saved != null) addAll(saved)
        }
    }

    val saveAllData = {
        storage.saveData("characters", characters.toList())
    }

    var currentTab by remember { mutableStateOf(0) }
    var currentFilter by remember { mutableStateOf(FilterState()) }

    // НОВЫЕ ПЕРЕМЕННЫЕ ДЛЯ СОРТИРОВКИ
    var currentSortOption by remember { mutableStateOf("Название") }
    var isAscending by remember { mutableStateOf(true) }

    NavHost(navController = navController, startDestination = "characters_screen") {

        composable("characters_screen") {
            CharacterListScreen(
                characters = characters,
                onCharacterClick = { id -> navController.navigate("main_screen/$id") },
                onAddCharacter = { name, note ->
                    characters.add(Character(name = name, note = note))
                    saveAllData()
                },
                onDeleteCharacter = { id ->
                    characters.removeAll { it.id == id }
                    saveAllData()
                },
                onEditCharacter = { id, newName, newNote ->
                    val index = characters.indexOfFirst { it.id == id }
                    if (index != -1) {
                        characters[index] = characters[index].copy(name = newName, note = newNote)
                        saveAllData()
                    }
                }
            )
        }

        composable("main_screen/{charId}") { backStackEntry ->
            val charId = backStackEntry.arguments?.getString("charId") ?: ""
            val characterIndex = characters.indexOfFirst { it.id == charId }

            if (characterIndex != -1) {
                val character = characters[characterIndex]
                MainScreen(
                    characterName = character.name,
                    charId = charId,
                    items = character.items,
                    onItemsChange = { newItems ->
                        characters[characterIndex] = character.copy(items = newItems)
                        saveAllData()
                    },
                    onBackClick = { navController.popBackStack() },
                    navController = navController,
                    selectedTab = currentTab,
                    onTabChange = { currentTab = it },
                    currentFilter = currentFilter,
                    onOpenFilters = { navController.navigate("filter_screen") },
                    // ПЕРЕДАЕМ СОРТИРОВКУ
                    currentSortOption = currentSortOption,
                    isAscending = isAscending,
                    onSortChange = { option, ascending ->
                        currentSortOption = option
                        isAscending = ascending
                    }
                )
            }
        }

        composable("edit_item/{charId}/{itemId}") { backStackEntry ->
            val charId = backStackEntry.arguments?.getString("charId") ?: ""
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val characterIndex = characters.indexOfFirst { it.id == charId }

            if (characterIndex != -1) {
                val character = characters[characterIndex]
                EditItemScreen(
                    itemId = itemId,
                    items = character.items,
                    onItemsChange = { newItems ->
                        characters[characterIndex] = character.copy(items = newItems)
                        saveAllData()
                    },
                    onSaveComplete = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable("filter_screen") {
            FilterScreen(
                currentFilter = currentFilter,
                onApply = { newFilter ->
                    currentFilter = newFilter
                    navController.popBackStack()
                },
                onReset = {
                    currentFilter = FilterState()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}