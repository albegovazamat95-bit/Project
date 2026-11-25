package com.example.product1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Класс для хранения состояния фильтров
data class FilterState(
    val searchQuery: String = "",
    val minQuantity: String = "",
    val maxQuantity: String = "",
    val selectedCategories: Set<String> = emptySet()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    currentFilter: FilterState,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit,
    onBackClick: () -> Unit
) {
    // Локальные состояния для экрана фильтров
    var searchQuery by remember { mutableStateOf(currentFilter.searchQuery) }
    var minQty by remember { mutableStateOf(currentFilter.minQuantity) }
    var maxQty by remember { mutableStateOf(currentFilter.maxQuantity) }
    val selectedCategories = remember { mutableStateListOf<String>().apply { addAll(currentFilter.selectedCategories) } }

    // Список категорий, которые можно фильтровать (исключаем "Нет")
    val categoriesToFilter = allCategories.filter { it != "Нет" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильтры", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Сброс всех полей
                        searchQuery = ""
                        minQty = ""
                        maxQty = ""
                        selectedCategories.clear()
                        onReset() // Сообщаем наверх, чтобы сбросили фильтр в списке
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Сбросить")
                }
                Button(
                    onClick = {
                        onApply(FilterState(searchQuery, minQty, maxQty, selectedCategories.toSet()))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Применить", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Поиск по названию
            Text("Название предмета", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Найти предмет...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Количество
            Text("Количество", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minQty,
                    onValueChange = { if (it.all { char -> char.isDigit() }) minQty = it },
                    placeholder = { Text("От") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Text("-", color = MaterialTheme.colorScheme.onBackground)
                OutlinedTextField(
                    value = maxQty,
                    onValueChange = { if (it.all { char -> char.isDigit() }) maxQty = it },
                    placeholder = { Text("До") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Категории
            Text("Категории", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))

            // Выводим категории в две колонки
            categoriesToFilter.chunked(2).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowCategories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (selectedCategories.contains(category)) selectedCategories.remove(category)
                                    else selectedCategories.add(category)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedCategories.contains(category),
                                onCheckedChange = null, // Обрабатываем клик на всю строку
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(category, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    // Если в ряду нечетное количество элементов, добавляем пустое место
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}