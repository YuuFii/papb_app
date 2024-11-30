package com.example.papb_app.pages

import android.net.Uri
import android.widget.Space
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.papb_app.AuthState
import com.example.papb_app.AuthViewModel
import com.example.papb_app.ToDoItem
import com.example.papb_app.ToDoViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    toDoViewModel: ToDoViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<ToDoItem?>(null) }
    val toDoList by toDoViewModel.toDoList.collectAsState()

    LaunchedEffect(Unit) {
        toDoViewModel.loadToDoItems()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    authViewModel.signout()
                }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Add Todo")
                    Text(text = "Sign Out")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Homepage",
                fontSize = 32.sp,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(toDoList) { item ->
                    ToDoItemCard(
                        item = item,
                        onEdit = {editItem = it},
                        onDelete = {toDoViewModel.deleteToDoItem(it.id)}
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Todo")
        }
    }

    // Add Todo Dialog
    if (showDialog || editItem != null) {
        AddOrEditToDoDialog(
            item = editItem,
            onDismiss = {
                showDialog = false
                editItem = null
            },
            onAddOrEditTodo = { title, description ->
                if (editItem != null) {
                    // Update existing item
                    toDoViewModel.updateToDoItem(editItem!!.copy(title = title, description = description))
                } else {
                    // Add new item
                    toDoViewModel.addToDoItem(title, description)
                }
                showDialog = false
                editItem = null
            }
        )
    }
}

@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onAddTodo: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add New Todo",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && description.isNotEmpty()) {
                                onAddTodo(title, description)
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun ToDoItemCard(item: ToDoItem, onEdit: (ToDoItem) -> Unit, onDelete: (ToDoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (item.isDone) "Done" else "Pending",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onEdit(item) }) {
                    Text(text = "Edit")
                }

                TextButton(onClick = { onDelete(item) }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddOrEditToDoDialog(
    item: ToDoItem? = null,
    onDismiss: () -> Unit,
    onAddOrEditTodo: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(item?.title ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (item == null) "Add New Todo" else "Edit Todo",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = title,
                    onValueChange = {title = it},
                    label = { Text(text = "Title")},
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = {description = it},
                    label = { Text(text = "Description")},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))



                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotEmpty() && description.isNotEmpty()) {
                                onAddOrEditTodo(title, description)
                            }
                        }
                    ) {
                        Text(text = if (item == null) "Add" else "Save")
                    }
                }
            }
        }
    }
}