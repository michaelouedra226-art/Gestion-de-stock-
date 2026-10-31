package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.repository.StockFlowRepository
import com.example.ui.components.ColorPalettePicker
import com.example.ui.components.CompanyBrandHeader
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
import com.example.ui.theme.parseHexColor
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    repository: StockFlowRepository,
    onCompleted: (String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1, 2, 3
    var companyName by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("EUR") }
    var defaultThresholdText by remember { mutableStateOf("5") }
    var brandColorHex by remember { mutableStateOf("#5EEAD4") }
    var initialCategory by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Intercept back gesture so it steps back instead of exiting the entire app
    BackHandler(enabled = true) {
        if (step > 1) {
            step -= 1
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBrandColor = parseHexColor(brandColorHex)

    val currencies = listOf("EUR", "USD", "XOF", "GBP", "CAD", "CHF")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Fine animated progress bar (using direct float to ensure broad compatibility)
            val progress = (step / 3f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = currentBrandColor,
                trackColor = DarkSurfaceElevated
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step Indicator Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configuration initiale",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = currentBrandColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Étape $step sur 3",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "onboarding_steps"
            ) { targetStep ->
                when (targetStep) {
                    1 -> {
                        Column {
                            Text(
                                text = "Votre Entreprise",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Renseignez le nom de votre structure et vos préférences monétaires.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = "Nom de l'entreprise *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = companyName,
                                onValueChange = {
                                    companyName = it
                                    errorMessage = null
                                },
                                placeholder = { Text("Ex: Atelier Métal, Nexus Logistique…", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_company_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentBrandColor,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Devise principale",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currencies.forEach { cur ->
                                    val isCurSelected = currency == cur
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isCurSelected) DarkSurfaceElevated else DarkSurface)
                                            .border(
                                                width = if (isCurSelected) 1.5.dp else 1.dp,
                                                color = if (isCurSelected) currentBrandColor else DarkBorder,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { currency = cur }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cur,
                                            fontWeight = if (isCurSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurSelected) currentBrandColor else TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Seuil d'alerte de stock par défaut",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = defaultThresholdText,
                                onValueChange = { defaultThresholdText = it.filter { ch -> ch.isDigit() } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_threshold_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentBrandColor,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    2 -> {
                        Column {
                            Text(
                                text = "Identité & Branding",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choisissez la couleur de marque qui habillera l'ensemble de votre interface StockFlow.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Live preview of company branding
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurface)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                                    .padding(18.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Aperçu en direct :",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    CompanyBrandHeader(companyName = companyName.ifBlank { "Mon Entreprise" })
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            ColorPalettePicker(
                                selectedHex = brandColorHex,
                                onColorSelected = { brandColorHex = it }
                            )
                        }
                    }

                    3 -> {
                        Column {
                            Text(
                                text = "Première Catégorie",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Créez une première catégorie de produits pour organiser votre stock (étape optionnelle).",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = "Nom de catégorie (optionnel)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = initialCategory,
                                onValueChange = { initialCategory = it },
                                placeholder = { Text("Ex: Matières premières, Quincaillerie…", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_category_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentBrandColor,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall.copy(color = RedDanger)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step -= 1 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.testTag("onboarding_prev_button")
                    ) {
                        Text("Précédent")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (step == 1) {
                            if (companyName.trim().isBlank()) {
                                errorMessage = "Veuillez entrer le nom de votre entreprise."
                                return@Button
                            }
                            step = 2
                        } else if (step == 2) {
                            step = 3
                        } else {
                            // Finalize
                            isSubmitting = true
                            coroutineScope.launch {
                                try {
                                    val threshold = defaultThresholdText.toLongOrNull() ?: 5L
                                    val newCompanyId = repository.createCompanyAndAssign(
                                        companyName = companyName,
                                        brandColor = brandColorHex,
                                        currency = currency,
                                        defaultThreshold = threshold,
                                        initialCategory = initialCategory.ifBlank { null }
                                    )
                                    isSubmitting = false
                                    onCompleted(newCompanyId)
                                } catch (e: Exception) {
                                    isSubmitting = false
                                    errorMessage = e.localizedMessage ?: "Une erreur est survenue lors de la création."
                                }
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentBrandColor,
                        contentColor = Color(0xFF0A0E1A)
                    ),
                    modifier = Modifier.testTag("onboarding_next_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF0A0E1A),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (step == 3) "Terminer et accéder au stock" else "Continuer",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
