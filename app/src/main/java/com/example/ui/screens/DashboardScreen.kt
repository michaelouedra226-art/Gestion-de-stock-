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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.Movement
import com.example.data.model.Product
import com.example.data.model.StockAlert
import com.example.ui.components.EmptyStateType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.IconAdd
import com.example.ui.components.IconAlerts
import com.example.ui.components.IconMovements
import com.example.ui.components.IconProducts
import com.example.ui.components.IconStockIn
import com.example.ui.components.IconStockOut
import com.example.ui.components.IconTrending
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    company: Company,
    products: List<Product>,
    movements: List<Movement>,
    alerts: List<StockAlert>,
    onNavigateToProducts: () -> Unit,
    onNavigateToMovements: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onQuickStockIn: () -> Unit,
    onQuickStockOut: () -> Unit,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brand = LocalBrandColor.current
    val totalReferences = products.size
    val totalStockValue = products.sumOf { it.totalValue }
    val lowStockCount = alerts.size
    val totalMovementsCount = movements.size

    val currencyFormatter = remember(company.currency) {
        try {
            val format = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            format.currency = java.util.Currency.getInstance(company.currency.ifBlank { "EUR" })
            format
        } catch (_: Exception) {
            NumberFormat.getCurrencyInstance(Locale.FRANCE)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tableau de Bord",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "Vue d'ensemble de vos flux de stock",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // 4 KPI Cards Bento Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Références",
                        value = totalReferences.toString(),
                        subtitle = "Produits au catalogue",
                        icon = { IconProducts(size = 20.dp, tint = brand) },
                        accent = brand,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_card_references")
                    )
                    KpiCard(
                        title = "Valeur Stock",
                        value = currencyFormatter.format(totalStockValue),
                        subtitle = "Valorisation totale",
                        icon = { IconTrending(size = 20.dp, tint = brand) },
                        accent = brand,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_card_stock_value")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Mouvements",
                        value = totalMovementsCount.toString(),
                        subtitle = "Flux enregistrés",
                        icon = { IconMovements(size = 20.dp, tint = brand) },
                        accent = brand,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_card_movements")
                    )
                    KpiCard(
                        title = "Alertes Stock",
                        value = lowStockCount.toString(),
                        subtitle = if (lowStockCount > 0) "À réapprovisionner" else "Tous stocks OK",
                        icon = { IconAlerts(size = 20.dp, tint = if (lowStockCount > 0) AmberAlert else GreenSuccess) },
                        accent = if (lowStockCount > 0) AmberAlert else GreenSuccess,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_card_alerts")
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "Actions Rapides",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    label = "Nouveau Produit",
                    icon = { IconAdd(size = 18.dp, tint = brand) },
                    onClick = onAddProduct,
                    modifier = Modifier.weight(1f).testTag("quick_action_add_product")
                )
                QuickActionButton(
                    label = "Entrée (+)",
                    icon = { IconStockIn(size = 18.dp, tint = GreenSuccess) },
                    onClick = onQuickStockIn,
                    modifier = Modifier.weight(1f).testTag("quick_action_stock_in")
                )
                QuickActionButton(
                    label = "Sortie (-)",
                    icon = { IconStockOut(size = 18.dp, tint = RedDanger) },
                    onClick = onQuickStockOut,
                    modifier = Modifier.weight(1f).testTag("quick_action_stock_out")
                )
            }
        }

        // Active Stock Alerts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alertes de Stock Actives",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                if (alerts.isNotEmpty()) {
                    Text(
                        text = "Voir tout (${alerts.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = brand,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToAlerts() }
                    )
                }
            }
        }

        if (alerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                ) {
                    EmptyStateView(
                        type = EmptyStateType.ALERTS,
                        title = "Aucune alerte active",
                        description = "Tous vos niveaux de stock sont au-dessus de leurs seuils critiques."
                    )
                }
            }
        } else {
            items(alerts.take(3)) { alert ->
                AlertItemCard(
                    alert = alert,
                    onRestockClick = onQuickStockIn
                )
            }
        }

        // Recent Movements Timeline Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Derniers Mouvements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                if (movements.isNotEmpty()) {
                    Text(
                        text = "Historique complet",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = brand,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToMovements() }
                    )
                }
            }
        }

        if (movements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                ) {
                    EmptyStateView(
                        type = EmptyStateType.MOVEMENTS,
                        title = "Aucun mouvement enregistré",
                        description = "Les entrées et sorties de stock enregistrées apparaîtront ici avec leur horodatage.",
                        actionLabel = if (products.isNotEmpty()) "Enregistrer un mouvement" else null,
                        onActionClick = if (products.isNotEmpty()) onQuickStockIn else null
                    )
                }
            }
        } else {
            items(movements.take(5)) { movement ->
                MovementItemRow(movement = movement)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceCard)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.35f), DarkBorder)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                icon()
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AlertItemCard(
    alert: StockAlert,
    onRestockClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, AmberAlert.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AmberAlert.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconAlerts(size = 18.dp, tint = AmberAlert)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = alert.productName.ifBlank { "Produit" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Stock : ${alert.currentStock} (Seuil : ${alert.threshold})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AmberAlert,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberAlert.copy(alpha = 0.2f))
                    .clickable { onRestockClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Réapprovisionner",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AmberAlert,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun MovementItemRow(movement: Movement) {
    val isEntry = movement.type == "in"
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.FRANCE) }
    val formattedDate = remember(movement.createdAt, movement.date) {
        val t = movement.createdAt ?: movement.date
        if (t != null) dateFormatter.format(t.toDate()) else "—"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEntry) GreenSuccess.copy(alpha = 0.15f) else RedDanger.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEntry) {
                    IconStockIn(size = 16.dp, tint = GreenSuccess)
                } else {
                    IconStockOut(size = 16.dp, tint = RedDanger)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = movement.productName.ifBlank { "Article" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "${movement.reason.ifBlank { if (isEntry) "Entrée de stock" else "Sortie de stock" }} • $formattedDate",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Text(
            text = "${if (isEntry) "+" else "-"}${movement.quantity}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isEntry) GreenSuccess else RedDanger,
                fontSize = 15.sp
            )
        )
    }
}
