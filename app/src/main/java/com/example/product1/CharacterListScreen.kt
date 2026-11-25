package com.example.product1

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    characters: List<Character>,
    onCharacterClick: (String) -> Unit,
    onAddCharacter: (String, String) -> Unit,
    onDeleteCharacter: (String) -> Unit,
    onEditCharacter: (String, String, String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newNote by remember { mutableStateOf("") }

    var showActionsDialog by remember { mutableStateOf(false) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("МОИ ПЕРСОНАЖИ", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { newName = ""; newNote = ""; showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { innerPadding ->
        if (characters.isEmpty()) {
            // Красивый экран, если персонажей нет
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Здесь пока пусто",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Создайте своего первого персонажа, чтобы начать собирать для него экипировку.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Создать героя", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(characters, key = { it.id }) { character ->
                    var menuExpanded by remember { mutableStateOf(false) }

                    // Объемная карточка с закруглениями
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(character) {
                                detectTapGestures(
                                    onTap = { onCharacterClick(character.id) },
                                    onLongPress = { selectedCharacter = character; showActionsDialog = true }
                                )
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // КРУГИ УБРАНЫ, ТЕКСТ СРАЗУ
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = character.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (character.note.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = character.note,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Кнопка меню
                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Меню",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Редактировать") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            editName = character.name
                                            editNote = character.note
                                            selectedCharacter = character
                                            showEditDialog = true
                                        }
                                    )
                                    Divider()
                                    DropdownMenuItem(
                                        text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            menuExpanded = false
                                            onDeleteCharacter(character.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ДИАЛОГ СОЗДАНИЯ ПЕРСОНАЖА
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Новый персонаж", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Имя персонажа") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newNote,
                        onValueChange = { newNote = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAddCharacter(newName, newNote)
                            showCreateDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Отмена") }
            }
        )
    }

    // ДИАЛОГ ДЕЙСТВИЙ
    if (showActionsDialog && selectedCharacter != null) {
        AlertDialog(
            onDismissRequest = { showActionsDialog = false },
            title = { Text(selectedCharacter!!.name, fontWeight = FontWeight.Bold) },
            text = { Text("Выберите действие") },
            confirmButton = {
                TextButton(onClick = {
                    editName = selectedCharacter!!.name
                    editNote = selectedCharacter!!.note
                    showActionsDialog = false
                    showEditDialog = true
                }) { Text("Редактировать") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        onDeleteCharacter(selectedCharacter!!.id)
                        showActionsDialog = false
                    }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { showActionsDialog = false }) {
                        Text("Отмена")
                    }
                }
            }
        )
    }

    // ДИАЛОГ РЕДАКТИРОВАНИЯ
    if (showEditDialog && selectedCharacter != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать персонажа", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Имя") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            onEditCharacter(selectedCharacter!!.id, editName, editNote)
                            showEditDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Отмена") }
            }
        )
    }
}