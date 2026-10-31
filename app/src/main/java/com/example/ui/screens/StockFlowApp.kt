package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.model.Company
import com.example.data.model.Product
import com.example.data.repository.StockFlowRepository
import com.example.ui.components.CompanyBrandHeader
import com.example.ui.components.IconAlerts
import com.example.ui.components.IconDashboard
import com.example.ui.components.IconMovements
import com.example.ui.components.IconProducts
import com.example.ui.components.IconSettings
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.StockFlowTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.parseHexColor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

enum class MainNavTab {
    DASHBOARD,
    PRODUCTS,
    MOVEMENTS,
    ALERTS,
    SETTINGS
}

@Composable
fun StockFlowApp(repository: StockFlowRepository) {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        StockFlowTheme {
            AuthScreen(
                onAuthSuccess = { currentUser = Firebase.auth.currentUser }
            )
        }
    } else {
        AuthenticatedAppContent(
            userId = currentUser!!.uid,
            repository = repository,
            onSignOutRequested = {
                Firebase.auth.signOut()
                currentUser = null
            }
        )
    }
}

@Composable
fun AuthenticatedAppContent(
    userId: String,
    repository: StockFlowRepository,
    onSignOutRequested: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val profileState by repository.observeUserProfileState(userId).collectAsState(initial = com.example.data.model.UserProfileState.Loading)

    var activeCompanyId by remember { mutableStateOf<String?>(null) }
    var timedOut by remember { mutableStateOf(false) }
    var forceOnboarding by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2500)
        timedOut = true
    }

    // Synchronize activeCompanyId from userProfile when loaded
    LaunchedEffect(profileState) {
        if (profileState is com.example.data.model.UserProfileState.Loaded) {
            val prof = (profileState as com.example.data.model.UserProfileState.Loaded).profile
            if (prof != null && prof.companyId.isNotBlank()) {
                activeCompanyId = prof.companyId
            }
        }
    }

    if (!activeCompanyId.isNullOrBlank()) {
        // Main Company Workspace
        val userProfile = (profileState as? com.example.data.model.UserProfileState.Loaded)?.profile
        val company by repository.observeCompany(activeCompanyId!!).collectAsState(initial = null)
        val products by repository.observeProducts(activeCompanyId!!).collectAsState(initial = emptyList())
        val movements by repository.observeMovements(activeCompanyId!!).collectAsState(initial = emptyList())
        val alerts by repository.observeAlerts(activeCompanyId!!).collectAsState(initial = emptyList())
        val categories by repository.observeCategories(activeCompanyId!!).collectAsState(initial = emptyList())

        val currentCompany = company ?: Company(id = activeCompanyId!!, name = "Mon Entreprise")
        val brandColor = parseHexColor(currentCompany.brandColor)

        StockFlowTheme(brandColor = brandColor) {
            CompanyWorkspaceContent(
                company = currentCompany,
                userProfile = userProfile,
                products = products,
                movements = movements,
                alerts = alerts,
                categories = categories,
                repository = repository,
                onSignOut = {
                    coroutineScope.launch {
                        try {
                            credentialManager.clearCredentialState(ClearCredentialStateRequest())
                        } catch (_: Exception) {}
                        onSignOutRequested()
                    }
                }
            )
        }
    } else if (forceOnboarding || profileState is com.example.data.model.UserProfileState.Loaded) {
        // User has no company yet or chose to proceed directly -> redirect to 3-step Onboarding Wizard
        StockFlowTheme {
            OnboardingScreen(
                repository = repository,
                onCompleted = { newCompanyId ->
                    activeCompanyId = newCompanyId
                }
            )
        }
    } else if (profileState is com.example.data.model.UserProfileState.Error) {
        val err = (profileState as com.example.data.model.UserProfileState.Error).message
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Initialisation de session",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = RedDanger,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { forceOnboarding = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5EEAD4),
                        contentColor = Color(0xFF0A0E1A)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Créer ou configurer mon entreprise", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onSignOutRequested() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se déconnecter")
                }
            }
        }
    } else {
        // Profile is Loading (< 2.5 seconds or timed out)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF5EEAD4))
                Spacer(modifier = Modifier.height(14.dp))
                Text("Chargement de votre session…", color = TextSecondary, fontSize = 13.sp)

                if (timedOut) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "La synchronisation prend du temps.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { forceOnboarding = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5EEAD4),
                            contentColor = Color(0xFF0A0E1A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Accéder à l'accueil / configuration", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onSignOutRequested() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Se déconnecter")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyWorkspaceContent(
    company: Company,
    userProfile: com.example.data.model.UserProfile?,
    products: List<Product>,
    movements: List<com.example.data.model.Movement>,
    alerts: List<com.example.data.model.StockAlert>,
    categories: List<com.example.data.model.Category>,
    repository: StockFlowRepository,
    onSignOut: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainNavTab.DASHBOARD) }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var quickMovementProduct by remember { mutableStateOf<Product?>(null) }
    var quickMovementType by remember { mutableStateOf("in") }
    var showQuickMovementDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val brand = LocalBrandColor.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    CompanyBrandHeader(companyName = company.name)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    if (alerts.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberAlert.copy(alpha = 0.2f))
                                .border(1.dp, AmberAlert.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { currentTab = MainNavTab.ALERTS }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${alerts.size} alerte(s)",
                                color = AmberAlert,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                currentTab = currentTab,
                alertCount = alerts.size,
                onTabSelected = {
                    currentTab = it
                    selectedProductForDetail = null
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedProductForDetail != null) {
                val detailMovements = movements.filter { it.productId == selectedProductForDetail!!.id }
                val updatedProduct = products.find { it.id == selectedProductForDetail!!.id } ?: selectedProductForDetail!!
                val productCategory = categories.find { it.id == updatedProduct.categoryId }

                ProductDetailScreen(
                    product = updatedProduct,
                    company = company,
                    category = productCategory,
                    movements = detailMovements,
                    onBack = { selectedProductForDetail = null },
                    onStockInClick = {
                        quickMovementProduct = updatedProduct
                        quickMovementType = "in"
                        showQuickMovementDialog = true
                    },
                    onStockOutClick = {
                        quickMovementProduct = updatedProduct
                        quickMovementType = "out"
                        showQuickMovementDialog = true
                    }
                )
            } else {
                when (currentTab) {
                    MainNavTab.DASHBOARD -> {
                        DashboardScreen(
                            company = company,
                            products = products,
                            movements = movements,
                            alerts = alerts,
                            onNavigateToProducts = { currentTab = MainNavTab.PRODUCTS },
                            onNavigateToMovements = { currentTab = MainNavTab.MOVEMENTS },
                            onNavigateToAlerts = { currentTab = MainNavTab.ALERTS },
                            onQuickStockIn = {
                                if (products.isNotEmpty()) {
                                    quickMovementProduct = products.first()
                                    quickMovementType = "in"
                                    showQuickMovementDialog = true
                                } else {
                                    currentTab = MainNavTab.PRODUCTS
                                }
                            },
                            onQuickStockOut = {
                                if (products.isNotEmpty()) {
                                    quickMovementProduct = products.first()
                                    quickMovementType = "out"
                                    showQuickMovementDialog = true
                                } else {
                                    currentTab = MainNavTab.PRODUCTS
                                }
                            },
                            onAddProduct = { currentTab = MainNavTab.PRODUCTS }
                        )
                    }

                    MainNavTab.PRODUCTS -> {
                        ProductsScreen(
                            company = company,
                            products = products,
                            categories = categories,
                            onProductClick = { selectedProductForDetail = it },
                            onAddProduct = { sku, name, desc, catId, stock, threshold, price ->
                                coroutineScope.launch {
                                    try {
                                        repository.addProduct(
                                            companyId = company.id,
                                            sku = sku,
                                            name = name,
                                            description = desc,
                                            categoryId = catId,
                                            initialStock = stock,
                                            threshold = threshold,
                                            unitPrice = price
                                        )
                                        snackbarHostState.showSnackbar("Produit $name créé avec succès.")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(e.localizedMessage ?: "Erreur lors de l'ajout.")
                                    }
                                }
                            },
                            onQuickStockIn = { prod ->
                                quickMovementProduct = prod
                                quickMovementType = "in"
                                showQuickMovementDialog = true
                            },
                            onQuickStockOut = { prod ->
                                quickMovementProduct = prod
                                quickMovementType = "out"
                                showQuickMovementDialog = true
                            },
                            onDeleteProduct = { prod ->
                                coroutineScope.launch {
                                    try {
                                        repository.deleteProduct(company.id, prod.id)
                                        snackbarHostState.showSnackbar("Produit ${prod.name} archivé.")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Erreur lors de l'archivage.")
                                    }
                                }
                            }
                        )
                    }

                    MainNavTab.MOVEMENTS -> {
                        MovementsScreen(
                            movements = movements,
                            products = products,
                            onRecordMovement = { prodId, type, qty, reason ->
                                coroutineScope.launch {
                                    try {
                                        repository.recordMovement(company.id, prodId, type, qty, reason)
                                        snackbarHostState.showSnackbar("Mouvement enregistré.")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(e.localizedMessage ?: "Erreur de mouvement.")
                                    }
                                }
                            }
                        )
                    }

                    MainNavTab.ALERTS -> {
                        AlertsScreen(
                            alerts = alerts,
                            products = products,
                            onRestockProduct = { prod ->
                                quickMovementProduct = prod
                                quickMovementType = "in"
                                showQuickMovementDialog = true
                            }
                        )
                    }

                    MainNavTab.SETTINGS -> {
                        SettingsScreen(
                            company = company,
                            userProfile = userProfile,
                            products = products,
                            onUpdateCompany = { updated ->
                                coroutineScope.launch {
                                    try {
                                        repository.updateCompany(updated)
                                        snackbarHostState.showSnackbar("Paramètres mis à jour.")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Erreur lors de la mise à jour.")
                                    }
                                }
                            },
                            onSignOut = onSignOut
                        )
                    }
                }
            }
        }
    }

    if (showQuickMovementDialog) {
        RecordMovementDialog(
            products = products,
            initialProduct = quickMovementProduct,
            initialType = quickMovementType,
            onDismiss = { showQuickMovementDialog = false },
            onConfirm = { prodId, type, qty, reason ->
                coroutineScope.launch {
                    try {
                        repository.recordMovement(company.id, prodId, type, qty, reason)
                        snackbarHostState.showSnackbar("Mouvement enregistré avec succès.")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(e.localizedMessage ?: "Erreur de mouvement.")
                    }
                }
                showQuickMovementDialog = false
            }
        )
    }
}

@Composable
fun CustomBottomNavigationBar(
    currentTab: MainNavTab,
    alertCount: Int,
    onTabSelected: (MainNavTab) -> Unit
) {
    val brand = LocalBrandColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(width = 1.dp, color = DarkBorder)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Dashboard",
            icon = { IconDashboard(size = 20.dp, tint = if (currentTab == MainNavTab.DASHBOARD) brand else TextSecondary) },
            isSelected = currentTab == MainNavTab.DASHBOARD,
            onClick = { onTabSelected(MainNavTab.DASHBOARD) },
            testTag = "nav_tab_dashboard"
        )
        BottomNavItem(
            label = "Produits",
            icon = { IconProducts(size = 20.dp, tint = if (currentTab == MainNavTab.PRODUCTS) brand else TextSecondary) },
            isSelected = currentTab == MainNavTab.PRODUCTS,
            onClick = { onTabSelected(MainNavTab.PRODUCTS) },
            testTag = "nav_tab_products"
        )
        BottomNavItem(
            label = "Mouvements",
            icon = { IconMovements(size = 20.dp, tint = if (currentTab == MainNavTab.MOVEMENTS) brand else TextSecondary) },
            isSelected = currentTab == MainNavTab.MOVEMENTS,
            onClick = { onTabSelected(MainNavTab.MOVEMENTS) },
            testTag = "nav_tab_movements"
        )
        BottomNavItem(
            label = "Alertes",
            icon = { IconAlerts(size = 20.dp, tint = if (currentTab == MainNavTab.ALERTS) brand else TextSecondary) },
            badgeCount = alertCount,
            isSelected = currentTab == MainNavTab.ALERTS,
            onClick = { onTabSelected(MainNavTab.ALERTS) },
            testTag = "nav_tab_alerts"
        )
        BottomNavItem(
            label = "Paramètres",
            icon = { IconSettings(size = 20.dp, tint = if (currentTab == MainNavTab.SETTINGS) brand else TextSecondary) },
            isSelected = currentTab == MainNavTab.SETTINGS,
            onClick = { onTabSelected(MainNavTab.SETTINGS) },
            testTag = "nav_tab_settings"
        )
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    val brand = LocalBrandColor.current

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            icon()
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AmberAlert)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) brand else TextSecondary,
                fontSize = 10.sp
            )
        )
    }
}
