package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.Product
import com.example.data.model.UserProfile
import com.example.ui.components.ColorPalettePicker
import com.example.ui.components.CompanyBrandHeader
import com.example.ui.components.IconExport
import com.example.ui.components.IconLogout
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    company: Company,
    userProfile: UserProfile?,
    products: List<Product>,
    onUpdateCompany: (Company) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val brand = LocalBrandColor.current

    var name by remember(company.name) { mutableStateOf(company.name) }
    var currency by remember(company.currency) { mutableStateOf(company.currency) }
    var thresholdText by remember(company.defaultThreshold) { mutableStateOf(company.defaultThreshold.toString()) }
    var selectedBrandColor by remember(company.brandColor) { mutableStateOf(company.brandColor) }
    var isSaving by remember { mutableStateOf(false) }

    val currencies = listOf("EUR", "USD", "XOF", "GBP", "CAD", "CHF")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column {
                Text(
                    text = "Paramètres",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Gestion de l'entreprise, personnalisation et exportation",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            // Section 1: Entreprise & Branding
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Entreprise & Marque",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    CompanyBrandHeader(companyName = name)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom de l'entreprise", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_company_name_input"),
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

                    Text(
                        text = "Devise des stocks",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currencies.forEach { cur ->
                            val isSel = currency == cur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) DarkSurfaceElevated else DarkSurface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) brand else DarkBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { currency = cur }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cur,
                                    color = if (isSel) brand else TextSecondary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = thresholdText,
                        onValueChange = { thresholdText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Seuil d'alerte par défaut (unités)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_threshold_input"),
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

                    ColorPalettePicker(
                        selectedHex = selectedBrandColor,
                        onColorSelected = { selectedBrandColor = it }
                    )

                    Button(
                        onClick = {
                            isSaving = true
                            val updated = company.copy(
                                name = name.trim().ifBlank { company.name },
                                currency = currency,
                                defaultThreshold = thresholdText.toLongOrNull() ?: company.defaultThreshold,
                                brandColor = selectedBrandColor
                            )
                            onUpdateCompany(updated)
                            isSaving = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Paramètres enregistrés avec succès.")
                            }
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = brand, contentColor = Color(0xFF0A0E1A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_settings_btn")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF0A0E1A))
                        } else {
                            Text("Enregistrer les modifications", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section 2: Profil & Droits
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Profil Utilisateur",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Connecté via Google Authenticator",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email : ${userProfile?.email?.ifBlank { "Compte Google" } ?: "Compte Google"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Rôle : ${userProfile?.role ?: "owner"} (Gestionnaire)",
                        style = MaterialTheme.typography.bodySmall.copy(color = brand)
                    )
                }
            }

            // Section 3: Données & Sauvegarde (Export CSV)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Données & Sauvegarde",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Exportez l'ensemble de votre inventaire de produits et valeurs en format CSV standard.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    OutlinedButton(
                        onClick = {
                            exportProductsToCsv(context, company, products)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Export CSV préparé.")
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = brand),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(brand)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("export_csv_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconExport(size = 18.dp, tint = brand)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exporter le stock au format CSV", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Section 4: Déconnexion
            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger.copy(alpha = 0.15f), contentColor = RedDanger),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("settings_sign_out_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconLogout(size = 18.dp, tint = RedDanger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Se déconnecter", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun exportProductsToCsv(context: Context, company: Company, products: List<Product>) {
    val header = "SKU,Nom,Stock,Seuil,PrixUnitaire,ValeurTotale\n"
    val rows = products.joinToString("\n") { p ->
        "\"${p.sku}\",\"${p.name.replace("\"", "\"\"")}\",${p.stock},${p.threshold},${p.unitPrice},${p.totalValue}"
    }
    val csvContent = header + rows

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, csvContent)
        putExtra(Intent.EXTRA_TITLE, "Inventaire_${company.name.replace(" ", "_")}.csv")
        type = "text/csv"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Exporter l'inventaire StockFlow")
    context.startActivity(shareIntent)
}
