package com.example.product1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Menu

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
    currentSort: SortType,             // <-- Принимаем сортировку
    onSortChange: (SortType) -> Unit   // <-- Функция смены сортировки
) {
    var showSelectionDialog by remember { mutableStateOf(false) }
    var currentSlot by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) } // Состояние для нижней панели сортировки

    fun getEquippedItem(slotName: String): Item? {
        return items.find { it.equippedSlot == slotName }
    }

    fun openSelection(slot: String) {
        currentSlot = slot
        showSelectionDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedTab == 0) {
                        Text("ПЕРСОНАЖ: $characterName", fontWeight = FontWeight.Bold)
                    } else {
                        // Только текст, без иконок
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Сортировка",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { showSortSheet = true }
                                    .padding(8.dp)
                            )

                            Text(
                                text = "Фильтры",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { onOpenFilters() }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "К списку")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Персонаж") },
                    selected = selectedTab == 0,
                    onClick = { onTabChange(0) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    // ИСПОЛЬЗУЕМ MENU ВМЕСТО LIST
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Рюкзак") },
                    selected = selectedTab == 1,
                    onClick = { onTabChange(1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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

            // ВКЛАДКА 1: ПЕРСОНАЖ (СЛОТЫ)
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

            // ВКЛАДКА 2: РЮКЗАК (С ФИЛЬТРАЦИЕЙ И СОРТИРОВКОЙ)
            if (selectedTab == 1) {
                var filteredItems = items.filter { it.equippedSlot.isEmpty() }

                // Применяем поиск и фильтры
                if (currentFilter.searchQuery.isNotBlank()) {
                    filteredItems = filteredItems.filter { it.name.contains(currentFilter.searchQuery, ignoreCase = true) }
                }
                val minQty = currentFilter.minQuantity.toIntOrNull()
                val maxQty = currentFilter.maxQuantity.toIntOrNull()
                if (minQty != null) filteredItems = filteredItems.filter { it.quantity >= minQty }
                if (maxQty != null) filteredItems = filteredItems.filter { it.quantity <= maxQty }
                if (currentFilter.selectedCategories.isNotEmpty()) {
                    filteredItems = filteredItems.filter { it.category in currentFilter.selectedCategories }
                }

                // ПРИМЕНЯЕМ СОРТИРОВКУ
                filteredItems = when (currentSort) {
                    SortType.NAME_ASC -> filteredItems.sortedBy { it.name.lowercase() }
                    SortType.NAME_DESC -> filteredItems.sortedByDescending { it.name.lowercase() }
                    SortType.QUANTITY_ASC -> filteredItems.sortedBy { it.quantity }
                    SortType.QUANTITY_DESC -> filteredItems.sortedByDescending { it.quantity }
                }

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

    // НИЖНЯЯ ПАНЕЛЬ (MODAL BOTTOM SHEET) ДЛЯ СОРТИРОВКИ
    // НИЖНЯЯ ПАНЕЛЬ (MODAL BOTTOM SHEET) ДЛЯ СОРТИРОВКИ
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Сортировка",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, // <-- Золотистый заголовок
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 1. По названию (А-Я)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortChange(SortType.NAME_ASC)
                            showSortSheet = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == SortType.NAME_ASC,
                        onClick = {
                            onSortChange(SortType.NAME_ASC)
                            showSortSheet = false
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "По названию (А-Я)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary // <-- Золотистый текст
                    )
                }

                // СПЛОШНАЯ ЛИНИЯ РАЗДЕЛИТЕЛЬ
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // 2. По количеству (Возрастание)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortChange(SortType.QUANTITY_ASC)
                            showSortSheet = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == SortType.QUANTITY_ASC,
                        onClick = {
                            onSortChange(SortType.QUANTITY_ASC)
                            showSortSheet = false
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "По количеству (Возрастание)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary // <-- Золотистый текст
                    )
                }

                // 3. По количеству (Убывание)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortChange(SortType.QUANTITY_DESC)
                            showSortSheet = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == SortType.QUANTITY_DESC,
                        onClick = {
                            onSortChange(SortType.QUANTITY_DESC)
                            showSortSheet = false
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "По количеству (Убывание)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary // <-- Золотистый текст
                    )
                }
            }
        }
    }

    // ДИАЛОГ ВЫБОРА ПРЕДМЕТА (без изменений)
    if (showSelectionDialog) {
        // ... (код диалога остается прежним) ...
        // Вставьте сюда блок if (showSelectionDialog) из предыдущего кода MainScreen.kt
    }
}

// Компонент слота
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