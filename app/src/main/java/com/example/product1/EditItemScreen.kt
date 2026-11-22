package com.example.product1

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: String,
    items: SnapshotStateList<Item>,
    onSaveComplete: () -> Unit,
    onBackClick: () -> Unit
) {
    // Ищем наш предмет в списке
    val itemIndex = items.indexOfFirst { it.id == itemId }

    // Если предмет не найден (например, был удален), сразу выходим
    if (itemIndex == -1) {
        LaunchedEffect(Unit) { onBackClick() }
        return
    }

    val currentItem = items[itemIndex]

    var itemName by remember { mutableStateOf(currentItem.name) }
    var itemDesc by remember { mutableStateOf(currentItem.description) }
    var selectedCategory by remember { mutableStateOf(currentItem.category) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    fun saveItem() {
        items[itemIndex] = currentItem.copy(
            name = itemName,
            description = itemDesc,
            category = selectedCategory
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemName.isEmpty()) "Новый предмет" else itemName) },
                navigationIcon = {
                    IconButton(onClick = {
                        saveItem()
                        onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Название предмета") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ВЫПАДАЮЩИЙ СПИСОК КАТЕГОРИЙ
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория (куда надевается)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    allCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                dropdownExpanded = false
                                // Если сменили категорию, то предмет автоматически снимается (если был надет)
                                if (currentItem.equippedSlot.isNotEmpty() && currentItem.equippedSlot != category) {
                                    items[itemIndex] = currentItem.copy(equippedSlot = "")
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = itemDesc,
                onValueChange = { itemDesc = it },
                label = { Text("Описание / Эффект") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    saveItem()
                    onSaveComplete()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("СОХРАНИТЬ") }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    items.removeAt(itemIndex)
                    onSaveComplete()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("УДАЛИТЬ ПРЕДМЕТ") }
        }
    }
}