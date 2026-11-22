package com.example.product1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(
    navController: NavController,
    items: SnapshotStateList<Item>,
    onSaveNeeded: () -> Unit
) {
    var showSelectionDialog by remember { mutableStateOf(false) }
    var currentSlot by remember { mutableStateOf("") }

    fun getEquippedItem(slotName: String): Item? {
        return items.find { it.equippedSlot == slotName }
    }

    fun openSelection(slot: String) {
        currentSlot = slot
        showSelectionDialog = true
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("СНАРЯЖЕНИЕ", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                leftSlots.forEach { slotName ->
                    SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                }
            }

            Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.hero),
                    contentDescription = "Персонаж",
                    modifier = Modifier.fillMaxWidth().aspectRatio(0.6f)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rightSlots.forEach { slotName ->
                    SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                }
            }
        }

        // Блок пояса
        if (getEquippedItem("ПОЯС") != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("ДОПОЛНИТЕЛЬНЫЕ СЛОТЫ (ПОЯС)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                beltSlots.forEach { slotName ->
                    Box(modifier = Modifier.weight(1f)) {
                        SlotDisplay(slotName, getEquippedItem(slotName)) { openSelection(slotName) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Рюкзак
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("РЮКЗАК / ИНВЕНТАРЬ", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                val backpackOnlyItems = items.filter { it.equippedSlot.isEmpty() }

                if (backpackOnlyItems.isEmpty()) {
                    Text("Рюкзак пуст", style = MaterialTheme.typography.bodySmall)
                } else {
                    backpackOnlyItems.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = { navController.navigate("edit_item/${item.id}") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(item.name.ifEmpty { "Без названия" }, fontWeight = FontWeight.Bold)
                                Text("Категория: ${item.category}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val newItem = Item()
                    items.add(newItem)
                    navController.navigate("edit_item/${newItem.id}")
                }) {
                    Text("Добавить в рюкзак")
                }
            }
        }
    }

    // ДИАЛОГ ВЫБОРА ПРЕДМЕТА (Обновлен)
    if (showSelectionDialog) {
        val availableItems = items.filter {
            (it.category == currentSlot ||
                    (currentSlot.startsWith("КОЛЬЦО") && it.category.startsWith("КОЛЬЦО")) ||
                    (currentSlot.startsWith("СУМКА") && it.category == "СУМКА"))
                    && it.equippedSlot.isEmpty()
        }

        val currentEquipped = getEquippedItem(currentSlot)

        AlertDialog(
            onDismissRequest = { showSelectionDialog = false },
            title = { Text("Слот: $currentSlot") },
            text = {
                // Добавляем verticalScroll, чтобы длинные описания не обрезались
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    // БЛОК ТЕКУЩЕГО ПРЕДМЕТА
                    if (currentEquipped != null) {
                        Text("Сейчас надето: ${currentEquipped.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // ВЫВОДИМ ОПИСАНИЕ ТЕКУЩЕГО ПРЕДМЕТА
                        if (currentEquipped.description.isNotEmpty()) {
                            Text(
                                text = currentEquipped.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val index = items.indexOf(currentEquipped)
                                if (index != -1) items[index] = currentEquipped.copy(equippedSlot = "")
                                onSaveNeeded()
                                showSelectionDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Снять предмет") }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }

                    // БЛОК СПИСКА ДОСТУПНЫХ ПРЕДМЕТОВ
                    if (availableItems.isEmpty()) {
                        Text("Нет подходящих предметов в рюкзаке.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Выберите, что надеть:", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        availableItems.forEach { item ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                onClick = {
                                    currentEquipped?.let { old ->
                                        val oldIdx = items.indexOf(old)
                                        if (oldIdx != -1) items[oldIdx] = old.copy(equippedSlot = "")
                                    }
                                    val newIdx = items.indexOf(item)
                                    if (newIdx != -1) items[newIdx] = item.copy(equippedSlot = currentSlot)

                                    onSaveNeeded()
                                    showSelectionDialog = false
                                }
                            ) {
                                // Изменили Row на Column, чтобы влезло описание
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(item.name, fontWeight = FontWeight.Bold)
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
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(slotName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
            if (equippedItem != null) {
                Text(equippedItem.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}