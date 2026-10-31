package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Movement
import com.example.data.model.Product
import com.example.ui.components.EmptyStateType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.IconAdd
import com.example.ui.components.IconStockIn
import com.example.ui.components.IconStockOut
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MovementsScreen(
    movements: List<Movement>,
    products: List<Product>,
    preselectedProduct: Product? = null,
    preselectedType: String = "in",
    onRecordMovement: (productId: String, type: String, quantity: Long, reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, IN, OUT
    var showDialog by remember { mutableStateOf(false) }
    val brand = LocalBrandColor.current

    val filteredMovements = remember(movements, selectedFilter) {
        when (selectedFilter) {
            "IN" -> movements.filter { it.type == "in" }
            "OUT" -> movements.filter { it.type == "out" }
            else -> movements
        }
    }

    Scaffold(
        floatingActionButton = {
            if (products.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = brand,
                    contentColor = Color(0xFF0A0E1A),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_movement")
                ) {
                    IconAdd(size = 24.dp, tint = Color(0xFF0A0E1A))
                }
            }
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mouvements de Stock",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Journal d'audit temps réel de toutes les entrées et sorties",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MovementFilterChip(
                    label = "Tous les flux (${movements.size})",
                    isSelected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" }
                )
                MovementFilterChip(
                    label = "Entrées (+)",
                    isSelected = selectedFilter == "IN",
                    onClick = { selectedFilter = "IN" }
                )
                MovementFilterChip(
                    label = "Sorties (-)",
                    isSelected = selectedFilter == "OUT",
                    onClick = { selectedFilter = "OUT" }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredMovements.isEmpty()) {
                EmptyStateView(
                    type = EmptyStateType.MOVEMENTS,
                    title = "Aucun mouvement répertorié",
                    description = if (products.isEmpty()) {
                        "Créez d'abord un produit pour enregistrer des entrées et sorties de stock."
                    } else {
                        "Enregistrez des mouvements pour alimenter le journal de traçabilité."
                    },
                    actionLabel = if (products.isNotEmpty()) "Enregistrer un mouvement" else null,
                    onActionClick = if (products.isNotEmpty()) { { showDialog = true } } else null,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredMovements, key = { it.id }) { mov ->
                        MovementItemRow(movement = mov)
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        RecordMovementDialog(
            products = products,
            initialProduct = preselectedProduct,
            initialType = preselectedType,
            onDismiss = { showDialog = false },
            onConfirm = { prodId, type, qty, reason ->
                onRecordMovement(prodId, type, qty, reason)
                showDialog = false
            }
        )
    }
}

@Composable
fun MovementFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val brand = LocalBrandColor.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) brand.copy(alpha = 0.18f) else DarkSurfaceCard)
            .border(
                width = 1.dp,
                color = if (isSelected) brand else DarkBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) brand else TextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordMovementDialog(
    products: List<Product>,
    initialProduct: Product? = null,
    initialType: String = "in",
    onDismiss: () -> Unit,
    onConfirm: (productId: String, type: String, quantity: Long, reason: String) -> Unit
) {
    var selectedProduct by remember { mutableStateOf(initialProduct ?: products.firstOrNull()) }
    var type by remember { mutableStateOf(initialType) } // "in" or "out"
    var quantityText by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val brand = LocalBrandColor.current

    val currentStock = selectedProduct?.stock ?: 0L
    val requestedQty = quantityText.toLongOrNull() ?: 0L
    val isInsufficientStock = type == "out" && requestedQty > currentStock

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enregistrer un Mouvement",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Type selector (In / Out)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == "in") GreenSuccess.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(
                                width = if (type == "in") 1.5.dp else 1.dp,
                                color = if (type == "in") GreenSuccess else DarkBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                type = "in"
                                validationError = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconStockIn(size = 18.dp, tint = GreenSuccess)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Entrée (+)",
                                fontWeight = FontWeight.Bold,
                                color = if (type == "in") GreenSuccess else TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == "out") RedDanger.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(
                                width = if (type == "out") 1.5.dp else 1.dp,
                                color = if (type == "out") RedDanger else DarkBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                type = "out"
                                validationError = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconStockOut(size = 18.dp, tint = RedDanger)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sortie (-)",
                                fontWeight = FontWeight.Bold,
                                color = if (type == "out") RedDanger else TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Product Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProduct?.let { "${it.name} (${it.sku}) - Stock: ${it.stock}" } ?: "Sélectionner un produit",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Produit concerné *", color = TextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("movement_product_dropdown"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brand,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        products.forEach { prod ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(prod.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text("SKU: ${prod.sku} • Stock actuel: ${prod.stock}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedProduct = prod
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Quantity
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it.filter { ch -> ch.isDigit() }
                        validationError = null
                    },
                    label = { Text("Quantité *", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("movement_quantity_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isInsufficientStock) RedDanger else brand,
                        unfocusedBorderColor = if (isInsufficientStock) RedDanger else DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Warning if sortie > stock
                if (isInsufficientStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RedDanger.copy(alpha = 0.15f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Attention : Stock disponible insuffisant ($currentStock unité(s)). Sortie bloquée.",
                            style = MaterialTheme.typography.bodySmall.copy(color = RedDanger)
                        )
                    }
                }

                // Reason / Reference
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motif / N° Commande / BL", color = TextSecondary) },
                    placeholder = { Text("Ex: Vente comptoir, Réception arrivage…", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("movement_reason_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brand,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = RedDanger)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedProduct == null) {
                        validationError = "Veuillez sélectionner un produit."
                        return@Button
                    }
                    if (requestedQty <= 0) {
                        validationError = "La quantité doit être supérieure à 0."
                        return@Button
                    }
                    if (isInsufficientStock) {
                        validationError = "Stock insuffisant pour cette sortie."
                        return@Button
                    }
                    onConfirm(
                        selectedProduct!!.id,
                        type,
                        requestedQty,
                        reason.ifBlank { if (type == "in") "Entrée manuelle" else "Sortie manuelle" }
                    )
                },
                enabled = !isInsufficientStock && requestedQty > 0 && selectedProduct != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "in") GreenSuccess else RedDanger,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_movement_btn")
            ) {
                Text("Valider le mouvement", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceCard
    )
}
