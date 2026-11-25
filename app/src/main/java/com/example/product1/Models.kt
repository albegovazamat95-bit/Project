package com.example.product1

import java.util.UUID

data class Character(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Новый герой",
    var note: String = "",
    var items: List<Item> = emptyList()
)

data class Item(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var category: String = "Нет",
    var description: String = "",
    var equippedSlot: String = "",
    var quantity: Int = 1 // <-- ДОБАВИЛИ КОЛИЧЕСТВО (по умолчанию 1)
)

val allCategories = listOf(
    "Нет", "ГОЛОВА", "ШЕЯ", "ТЕЛО", "ЗАПЯСТЬЕ", "КИСТИ", "НОГИ",
    "ЛОБ", "ГЛАЗА", "ПЛЕЧИ", "КОЛЬЦО", "ГРУДЬ", "ПОЯС", "СУМКА"
)

val leftSlots = listOf("ГОЛОВА", "ШЕЯ", "ТЕЛО", "ЗАПЯСТЬЕ", "КИСТИ", "НОГИ")
val rightSlots = listOf("ЛОБ", "ГЛАЗА", "ПЛЕЧИ", "КОЛЬЦО 1", "КОЛЬЦО 2", "ГРУДЬ", "ПОЯС")
val beltSlots = listOf("СУМКА 1", "СУМКА 2", "СУМКА 3", "СУМКА 4")