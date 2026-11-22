package com.example.product1

import java.util.UUID

data class Item(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var category: String = "Нет",
    var description: String = "",
    var equippedSlot: String = ""
)

val allCategories = listOf(
    "Нет",
    "ГОЛОВА", "ШЕЯ", "ТЕЛО", "ЗАПЯСТЬЕ", "КИСТИ", "НОГИ",
    "ЛОБ", "ГЛАЗА", "ПЛЕЧИ", "КОЛЬЦО 1", "КОЛЬЦО 2", "ГРУДЬ", "ПОЯС",
    "СУМКА" // <--- Теперь в выборе только одна категория для сумки
)

val leftSlots = listOf("ГОЛОВА", "ШЕЯ", "ТЕЛО", "ЗАПЯСТЬЕ", "КИСТИ", "НОГИ")
val rightSlots = listOf("ЛОБ", "ГЛАЗА", "ПЛЕЧИ", "КОЛЬЦО 1", "КОЛЬЦО 2", "ГРУДЬ", "ПОЯС")

// <--- Возвращаем 4 ячейки для пояса
val beltSlots = listOf("СУМКА 1", "СУМКА 2", "СУМКА 3", "СУМКА 4")