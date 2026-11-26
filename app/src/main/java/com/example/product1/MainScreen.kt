package com.example.product1

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.* // Здесь есть иконка поиска и крестик
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    characterName: String,
    charId: String,
    items: List<Item>,
    onItemsChange: (List<Item>) -> Unit,
    onBackClick: () -> Unit,
    navController: NavController,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    currentFilter: FilterState,
    onOpenFilters: () -> Unit,
    currentSortOption: String,
    isAscending: Boolean,
    onSortChange: (String, Boolean) -> Unit
) {
    var showSelectionDialog by remember { mutableStateOf(false) }
    var currentSlot by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }

    // СОСТОЯНИЕ ДЛЯ ПОИСКА ПО НАЗВАНИЮ
    var searchQuery by remember { mutableStateOf("") }

    fun getEquippedItem(slotName: String): Item? {
        return items.find { it.equippedSlot == slotName }
    }

    fun openSelection(slot: String) {
        currentSlot = slot
        showSelectionDialog = true
    }

    fun handleBack() {
        if (selectedTab == 1) {
            onTabChange(0)
        } else {
            onBackClick()
        }
    }

    BackHandler {
        handleBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "ПЕРСОНАЖ: $characterName" else "РЮКЗАК",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (selectedTab == 1) {
                        Row(
                            modifier = Modifier.padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { showSortSheet = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Сортировка", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Сортировка", style = MaterialTheme.typography.labelMedium)
                            }

                            Row(
                                modifier = Modifier
                                    .clickable { onOpenFilters() }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "Фильтры", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Фильтры", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Персонаж") },
                    label = { Text("Персонаж") },
                    selected = selectedTab == 0,
                    onClick = { onTabChange(0) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Рюкзак") },
                    label = { Text("Рюкзак") },
                    selected = selectedTab == 1,
                    onClick = { onTabChange(1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(onClick = {
                    val newItem = Item()
                    onItemsChange(items + newItem)
                    navController.navigate("edit_item/$charId/${newItem.id}")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // ВКЛАДКА 1: ПЕРСОНАЖ
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            leftSlots.forEach { slotName ->
                                SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                            }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rightSlots.forEach { slotName ->
                                SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                            }
                        }
                    }

                    if (getEquippedItem("ПОЯС") != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("СУМКИ НА ПОЯСЕ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            beltSlots.forEach { slotName ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // ВКЛАДКА 2: РЮКЗАК
            if (selectedTab == 1) {
                var filteredItems = items.filter { it.equippedSlot.isEmpty() }

                // 1. ПРИМЕНЯЕМ ПОИСК ПО НАЗВАНИЮ
                if (searchQuery.isNotBlank()) {
                    filteredItems = filteredItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                // 2. ПРИМЕНЯЕМ ФИЛЬТР КАТЕГОРИЙ
                if (currentFilter.selectedCategories.isNotEmpty()) {
                    filteredItems = filteredItems.filter { it.category in currentFilter.selectedCategories }
                }

                // 3. ПРИМЕНЯЕМ СОРТИРОВКУ
                filteredItems = when (currentSortOption) {
                    "Название" -> if (isAscending) filteredItems.sortedBy { it.name.lowercase() } else filteredItems.sortedByDescending { it.name.lowercase() }
                    "Количество" -> if (isAscending) filteredItems.sortedBy { it.quantity } else filteredItems.sortedByDescending { it.quantity }
                    "Категория" -> if (isAscending) filteredItems.sortedBy { it.category } else filteredItems.sortedByDescending { it.category }
                    else -> filteredItems
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // СТРОКА ПОИСКА
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Поиск по названию...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Очистить")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    // СПИСОК ПРЕДМЕТОВ
                    if (filteredItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (items.isEmpty()) "Рюкзак пуст" else "Ничего не найдено",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { navController.navigate("edit_item/$charId/${item.id}") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Слот: ${item.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            if (item.description.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                            }
                                        }

                                        if (item.quantity > 1) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "x${item.quantity}",
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ВЫЕЗЖАЮЩАЯ ПАНЕЛЬ СОРТИРОВКИ
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Сортировка", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showSortSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }

                val sortOptions = listOf("Название", "Количество", "Категория")
                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentSortOption == option),
                            onClick = { onSortChange(option, isAscending) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isAscending,
                        onClick = { onSortChange(currentSortOption, true) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("По возрастанию (А-Я / 1-9)", style = MaterialTheme.typography.bodyLarge)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isAscending,
                        onClick = { onSortChange(currentSortOption, false) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("По убыванию (Я-А / 9-1)", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    // ДИАЛОГ ВЫБОРА ПРЕДМЕТА (С ЛОГИКОЙ РАЗДЕЛЕНИЯ СТАКОВ)
    if (showSelectionDialog) {
        val availableItems = items.filter {
            (it.category == currentSlot ||
                    (currentSlot.startsWith("КОЛЬЦО") && it.category == "КОЛЬЦО") ||
                    (currentSlot.startsWith("СУМКА") && it.category == "СУМКА"))
                    && it.equippedSlot.isEmpty()
        }
        val currentEquipped = getEquippedItem(currentSlot)

        AlertDialog(
            onDismissRequest = { showSelectionDialog = false },
            title = { Text("Слот: $currentSlot") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (currentEquipped != null) {
                        Text("Надето: ${currentEquipped.name} (x${currentEquipped.quantity})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        if (currentEquipped.description.isNotEmpty()) {
                            Text(currentEquipped.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val updatedList = items.toMutableList()
                                updatedList.remove(currentEquipped)

                                val existingStackIndex = updatedList.indexOfFirst {
                                    it.name == currentEquipped.name &&
                                            it.category == currentEquipped.category &&
                                            it.equippedSlot == ""
                                }

                                if (existingStackIndex != -1) {
                                    val stack = updatedList[existingStackIndex]
                                    updatedList[existingStackIndex] = stack.copy(quantity = stack.quantity + currentEquipped.quantity)
                                } else {
                                    updatedList.add(currentEquipped.copy(equippedSlot = ""))
                                }

                                onItemsChange(updatedList)
                                showSelectionDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Снять") }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }

                    if (availableItems.isEmpty()) {
                        Text("Нет подходящих предметов.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Надеть:", style = MaterialTheme.typography.bodySmall)
                        availableItems.forEach { item ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                onClick = {
                                    val updatedList = items.toMutableList()

                                    if (currentEquipped != null) {
                                        updatedList.remove(currentEquipped)
                                        val existingStackIndex = updatedList.indexOfFirst {
                                            it.name == currentEquipped.name &&
                                                    it.category == currentEquipped.category &&
                                                    it.equippedSlot == ""
                                        }
                                        if (existingStackIndex != -1) {
                                            val stack = updatedList[existingStackIndex]
                                            updatedList[existingStackIndex] = stack.copy(quantity = stack.quantity + currentEquipped.quantity)
                                        } else {
                                            updatedList.add(currentEquipped.copy(equippedSlot = ""))
                                        }
                                    }

                                    val selected = updatedList.find { it.id == item.id }
                                    if (selected != null) {
                                        updatedList.remove(selected)

                                        updatedList.add(selected.copy(
                                            id = UUID.randomUUID().toString(),
                                            quantity = 1,
                                            equippedSlot = currentSlot
                                        ))

                                        if (selected.quantity > 1) {
                                            updatedList.add(selected.copy(quantity = selected.quantity - 1))
                                        }
                                    }

                                    onItemsChange(updatedList)
                                    showSelectionDialog = false
                                }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (item.quantity > 1) "${item.name} (x${item.quantity})" else item.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (item.description.isNotEmpty()) {
                                        Text(item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSelectionDialog = false }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
fun SlotDisplay(slotName: String, equippedItem: Item?, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (equippedItem != null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(slotName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            if (equippedItem != null) {
                val displayName = if (equippedItem.quantity > 1) "${equippedItem.name} (x${equippedItem.quantity})" else equippedItem.name
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            } else {
                Text("—", color = Color.LightGray)
            }
        }
    }
}