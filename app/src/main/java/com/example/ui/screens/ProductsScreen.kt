package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Company
import com.example.data.model.Product
import com.example.ui.components.EmptyStateType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.IconAdd
import com.example.ui.components.IconDelete
import com.example.ui.components.IconEdit
import com.example.ui.components.IconSearch
import com.example.ui.components.IconStockIn
import com.example.ui.components.IconStockOut
import com.example.ui.theme.AmberAlert
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductsScreen(
    company: Company,
    products: List<Product>,
    categories: List<Category>,
    onProductClick: (Product) -> Unit,
    onAddProduct: (sku: String, name: String, desc: String, categoryId: String, stock: Long, threshold: Long, price: Double) -> Unit,
    onQuickStockIn: (Product) -> Unit,
    onQuickStockOut: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val brand = LocalBrandColor.current

    val currencyFormatter = remember(company.currency) {
        try {
            val format = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            format.currency = java.util.Currency.getInstance(company.currency.ifBlank { "EUR" })
            format
        } catch (_: Exception) {
            NumberFormat.getCurrencyInstance(Locale.FRANCE)
        }
    }

    val filteredProducts = remember(products, searchQuery, selectedFilter, selectedCategoryId) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.sku.contains(searchQuery, ignoreCase = true)
            val matchesStatus = when (selectedFilter) {
                "IN_STOCK" -> p.stock > p.threshold
                "LOW_STOCK" -> p.isLowStock
                "OUT_OF_STOCK" -> p.isOutOfStock
                else -> true
            }
            val matchesCategory = selectedCategoryId == null || p.categoryId == selectedCategoryId
            matchesQuery && matchesStatus && matchesCategory
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = brand,
                contentColor = Color(0xFF0A0E1A),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_product")
            ) {
                IconAdd(size = 24.dp, tint = Color(0xFF0A0E1A))
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher par référence, nom ou SKU…", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { IconSearch(size = 18.dp, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "Effacer",
                            style = MaterialTheme.typography.labelSmall.copy(color = brand),
                            modifier = Modifier
                                .clickable { searchQuery = "" }
                                .padding(horizontal = 8.dp)
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("products_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brand,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        label = "Tous (${products.size})",
                        isSelected = selectedFilter == "ALL" && selectedCategoryId == null,
                        onClick = {
                            selectedFilter = "ALL"
                            selectedCategoryId = null
                        }
                    )
                }
                item {
                    FilterChip(
                        label = "En stock",
                        isSelected = selectedFilter == "IN_STOCK",
                        onClick = { selectedFilter = "IN_STOCK" }
                    )
                }
                item {
                    FilterChip(
                        label = "Stock bas",
                        isSelected = selectedFilter == "LOW_STOCK",
                        onClick = { selectedFilter = "LOW_STOCK" }
                    )
                }
                item {
                    FilterChip(
                        label = "Rupture",
                        isSelected = selectedFilter == "OUT_OF_STOCK",
                        onClick = { selectedFilter = "OUT_OF_STOCK" }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        label = cat.name,
                        isSelected = selectedCategoryId == cat.id,
                        onClick = {
                            selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {
                if (products.isEmpty()) {
                    EmptyStateView(
                        type = EmptyStateType.PRODUCTS,
                        title = "Aucun produit en stock",
                        description = "Enregistrez votre première référence pour commencer à gérer vos entrées, sorties et alertes.",
                        actionLabel = "Créer mon premier produit",
                        onActionClick = { showCreateDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    EmptyStateView(
                        type = EmptyStateType.SEARCH,
                        title = "Aucun résultat",
                        description = "Aucun produit ne correspond à vos filtres actuels. Modifiez votre recherche ou réinitialisez les filtres.",
                        actionLabel = "Réinitialiser les filtres",
                        onActionClick = {
                            searchQuery = ""
                            selectedFilter = "ALL"
                            selectedCategoryId = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val category = categories.find { it.id == product.categoryId }
                        ProductCardItem(
                            product = product,
                            category = category,
                            currencyFormatter = currencyFormatter,
                            onClick = { onProductClick(product) },
                            onQuickIn = { onQuickStockIn(product) },
                            onQuickOut = { onQuickStockOut(product) },
                            onDelete = { onDeleteProduct(product) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProductDialog(
            categories = categories,
            defaultThreshold = company.defaultThreshold,
            onDismiss = { showCreateDialog = false },
            onConfirm = { sku, name, desc, catId, stock, threshold, price ->
                onAddProduct(sku, name, desc, catId, stock, threshold, price)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun FilterChip(
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
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

@Composable
fun ProductCardItem(
    product: Product,
    category: Category?,
    currencyFormatter: NumberFormat,
    onClick: () -> Unit,
    onQuickIn: () -> Unit,
    onQuickOut: () -> Unit,
    onDelete: () -> Unit
) {
    val brand = LocalBrandColor.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when {
        product.isOutOfStock -> RedDanger
        product.isLowStock -> AmberAlert
        else -> GreenSuccess
    }

    val statusLabel = when {
        product.isOutOfStock -> "Rupture"
        product.isLowStock -> "Stock bas"
        else -> "En stock"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // SKU tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.sku,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = brand,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (category != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${category.name}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )

            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quantité en stock",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                    Text(
                        text = "${product.stock} unité(s)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Prix unitaire",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                    Text(
                        text = currencyFormatter.format(product.unitPrice),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    )
                }

                // Quick Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GreenSuccess.copy(alpha = 0.15f))
                            .clickable { onQuickIn() }
                            .testTag("quick_in_btn_${product.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        IconStockIn(size = 18.dp, tint = GreenSuccess)
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(RedDanger.copy(alpha = 0.15f))
                            .clickable { onQuickOut() }
                            .testTag("quick_out_btn_${product.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        IconStockOut(size = 18.dp, tint = RedDanger)
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable { showDeleteConfirm = true },
                        contentAlignment = Alignment.Center
                    ) {
                        IconDelete(size = 16.dp, tint = TextMuted)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Archiver le produit ?", color = TextPrimary) },
            text = {
                Text(
                    "Ce produit ne sera plus affiché dans l'inventaire actif mais son historique de mouvements sera préservé.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text("Archiver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
fun CreateProductDialog(
    categories: List<Category>,
    defaultThreshold: Long,
    onDismiss: () -> Unit,
    onConfirm: (sku: String, name: String, desc: String, categoryId: String, stock: Long, threshold: Long, price: Double) -> Unit
) {
    var sku by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var stockText by remember { mutableStateOf("0") }
    var thresholdText by remember { mutableStateOf(defaultThreshold.toString()) }
    var priceText by remember { mutableStateOf("0.00") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val brand = LocalBrandColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nouveau Produit",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = sku,
                    onValueChange = {
                        sku = it.uppercase()
                        validationError = null
                    },
                    label = { Text("SKU / Référence *", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_sku_field"),
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

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        validationError = null
                    },
                    label = { Text("Nom de l'article *", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_name_field"),
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

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnel)", color = TextSecondary) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Stock initial", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_stock_field"),
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

                    OutlinedTextField(
                        value = thresholdText,
                        onValueChange = { thresholdText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Seuil alerte", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_threshold_field"),
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
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Prix unitaire", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_price_field"),
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
                    if (sku.trim().isBlank()) {
                        validationError = "Le code SKU est obligatoire."
                        return@Button
                    }
                    if (name.trim().isBlank()) {
                        validationError = "Le nom de l'article est obligatoire."
                        return@Button
                    }
                    val stock = stockText.toLongOrNull() ?: 0L
                    val threshold = thresholdText.toLongOrNull() ?: defaultThreshold
                    val price = priceText.replace(",", ".").toDoubleOrNull() ?: 0.0

                    onConfirm(sku, name, description, selectedCategoryId, stock, threshold, price)
                },
                colors = ButtonDefaults.buttonColors(containerColor = brand, contentColor = Color(0xFF0A0E1A)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_create_product_btn")
            ) {
                Text("Enregistrer", fontWeight = FontWeight.Bold)
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
