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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.StockAlert
import com.example.ui.components.EmptyStateType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.IconAlerts
import com.example.ui.components.IconStockIn
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AlertsScreen(
    alerts: List<StockAlert>,
    products: List<Product>,
    onRestockProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val brand = LocalBrandColor.current

    Scaffold(
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
                text = "Alertes de Réapprovisionnement",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Surveillance active des stocks sous le seuil critique",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (alerts.isEmpty()) {
                EmptyStateView(
                    type = EmptyStateType.ALERTS,
                    title = "Aucune alerte de stock",
                    description = "Tous vos articles sont au-dessus de leur seuil minimal de sécurité. Le flux d'inventaire est équilibré.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        val matchingProduct = products.find { it.id == alert.productId }
                        val isOut = alert.currentStock <= 0L
                        val accent = if (isOut) RedDanger else AmberAlert

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("alert_card_${alert.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(accent.copy(alpha = 0.35f)))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconAlerts(size = 20.dp, tint = accent)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = alert.productName.ifBlank { "Article" },
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isOut) "RUPTURE TOTALE" else "Stock actuel : ${alert.currentStock} (Seuil : ${alert.threshold})",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = accent,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                if (matchingProduct != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(GreenSuccess.copy(alpha = 0.18f))
                                            .border(1.dp, GreenSuccess.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .clickable { onRestockProduct(matchingProduct) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                            .testTag("restock_btn_${alert.id}")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconStockIn(size = 14.dp, tint = GreenSuccess)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Entrée",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GreenSuccess,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
