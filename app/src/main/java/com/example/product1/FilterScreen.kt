package com.example.product1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Класс для хранения состояния фильтров (ТЕПЕРЬ ТОЛЬКО КАТЕГОРИИ)
data class FilterState(
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
    // Локальное состояние выбранных категорий
    val selectedCategories = remember { mutableStateListOf<String>().apply { addAll(currentFilter.selectedCategories) } }

    // Список категорий (исключаем "Нет")
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
                    .navigationBarsPadding() // <--- ВОТ ЭТА СТРОКА УБИРАЕТ НАЛОЖЕНИЕ НА ПАНЕЛЬ ТЕЛЕФОНА
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedCategories.clear()
                        onReset() // Сбрасываем фильтр в списке
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Сбросить")
                }
                Button(
                    onClick = {
                        onApply(FilterState(selectedCategories.toSet()))
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Категории (сразу идут после заголовка)
            Text("Категории", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))

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
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(category, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}