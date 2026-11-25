package com.example.product1

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: String,
    items: List<Item>,
    onItemsChange: (List<Item>) -> Unit,
    onSaveComplete: () -> Unit,
    onBackClick: () -> Unit
) {
    val currentItem = items.find { it.id == itemId }

    if (currentItem == null) {
        LaunchedEffect(Unit) { onBackClick() }
        return
    }

    var itemName by remember { mutableStateOf(currentItem.name) }
    var itemDesc by remember { mutableStateOf(currentItem.description) }
    var selectedCategory by remember { mutableStateOf(currentItem.category) }
    var quantityText by remember { mutableStateOf(currentItem.quantity.toString()) } // Храним текст для поля ввода
    var dropdownExpanded by remember { mutableStateOf(false) }

    fun processExit() {
        if (itemName.isBlank()) {
            onItemsChange(items.filter { it.id != itemId })
        } else {
            // Преобразуем текст в число. Если пусто или ошибка — ставим 1.
            val qty = quantityText.toIntOrNull() ?: 1

            val updatedItem = currentItem.copy(
                name = itemName,
                description = itemDesc,
                category = selectedCategory,
                quantity = if (qty > 0) qty else 1 // Защита от нуля или отрицательных чисел
            )
            onItemsChange(items.map { if (it.id == itemId) updatedItem else it })
            onSaveComplete()
        }
    }

    BackHandler { processExit() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemName.isBlank()) "НОВЫЙ ПРЕДМЕТ" else "ПРЕДМЕТ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { processExit() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Ряд для Названия и Количества
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Название предмета") },
                    singleLine = true,
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newText ->
                        // Разрешаем вводить только цифры
                        if (newText.isEmpty() || newText.all { it.isDigit() }) {
                            quantityText = newText
                        }
                    },
                    label = { Text("Кол-во") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { processExit() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("СОХРАНИТЬ") }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    onItemsChange(items.filter { it.id != itemId })
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("УДАЛИТЬ ПРЕДМЕТ") }
        }
    }
}