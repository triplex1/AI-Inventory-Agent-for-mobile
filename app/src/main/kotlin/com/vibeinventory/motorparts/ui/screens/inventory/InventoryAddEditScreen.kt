package com.vibeinventory.motorparts.ui.screens.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

/**
 * Basic add/edit inventory item screen.
 *
 * - When [itemId] is null, creates a new item
 * - When [itemId] is non-null and matches an existing item, edits that item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryAddEditScreen(
    itemId: String? = null,
    onNavigateBack: () -> Unit = {},
    viewModel: InventoryViewModel = viewModel()
    ) {
    val items by viewModel.inventoryItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Find existing item when editing
    val existingItem = remember(items, itemId) {
        items.firstOrNull { it.id == itemId }
    }

    var name by remember(existingItem) { mutableStateOf(existingItem?.name ?: "") }
    var partNumber by remember(existingItem) { mutableStateOf(existingItem?.partNumber ?: "") }
    var quantityText by remember(existingItem) { mutableStateOf((existingItem?.quantity ?: 0).toString()) }
    var location by remember(existingItem) { mutableStateOf(existingItem?.location ?: "") }
    var priceText by remember(existingItem) { mutableStateOf((existingItem?.price ?: 0.0).toString()) }
    var category by remember(existingItem) { mutableStateOf(existingItem?.category ?: "") }

    LaunchedEffect(error) {
        if (error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(error ?: "Something went wrong")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingItem != null) "Edit Item" else "Add Item",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                singleLine = true
            )

            OutlinedTextField(
                value = partNumber,
                onValueChange = { partNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Part Number") },
                singleLine = true
            )

            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Quantity") },
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location") },
                singleLine = true
            )

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Price") },
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category") },
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val quantity = quantityText.toIntOrNull() ?: 0
                        val price = priceText.toDoubleOrNull() ?: 0.0

                        if (name.isBlank() || partNumber.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Name and Part Number are required")
                            }
                            return@Button
                        }

                        val baseItem = existingItem ?: InventoryItem()
                        val updated = baseItem.copy(
                            name = name.trim(),
                            partNumber = partNumber.trim(),
                            quantity = quantity,
                            location = location.trim(),
                            price = price,
                            category = category.trim()
                        )

                        if (existingItem == null) {
                            viewModel.addInventoryItem(updated)
                        } else {
                            viewModel.updateInventoryItem(updated)
                        }

                        onNavigateBack()
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (existingItem != null) "Save Changes" else "Add Item")
                }

                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

