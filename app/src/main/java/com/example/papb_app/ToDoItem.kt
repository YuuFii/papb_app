package com.example.papb_app

data class ToDoItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false,
    val imageUri: String? = null
)