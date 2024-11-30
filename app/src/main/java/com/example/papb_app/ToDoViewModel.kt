package com.example.papb_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ToDoViewModel() : ViewModel() {
    private val repository = ToDoRepository()

    private val _toDoList = MutableStateFlow<List<ToDoItem>>(emptyList())
    val toDoList: StateFlow<List<ToDoItem>> = _toDoList

    fun loadToDoItems() {
        viewModelScope.launch {
            val items = repository.getToDoItems()    // Operasi asynchronous
            _toDoList.value = items                  // Mengubah state UI setelah data didapat
        }
    }

    fun addToDoItem(title: String, description: String) {
        val newItem = ToDoItem(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            isDone = false
        )
        viewModelScope.launch {
            repository.addToDoItem(newItem) // Operasi asynchronous menyimpan ke Firestore
            loadToDoItems() // Memuat ulang data setelah menambahkan item baru
        }
    }

    fun updateToDoItem(item: ToDoItem) {
        viewModelScope.launch {
            repository.updateToDoItem(item)
            loadToDoItems()
        }
    }

    fun deleteToDoItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteToDoItem(itemId)
            loadToDoItems()
        }
    }
}