package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BusinessMission
import com.example.data.model.CITY_MAP_PRESETS
import com.example.data.model.CartItem
import com.example.data.model.Comercio
import com.example.data.model.MapPointPreset
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductPresentation
import com.example.data.model.TransportBid
import com.example.data.model.TransportRequest
import com.example.data.model.UserMissionClaim
import com.example.data.model.UserMissionProgress
import com.example.data.model.UserProfile
import com.example.data.model.formatBs
import com.example.data.model.formatDualPrice
import com.example.data.model.formatUsd
import com.example.ui.components.BrightYellow
import com.example.ui.components.BusinessSectorFilterDialog
import com.example.ui.components.EditProfileModal
import com.example.ui.components.MyPointsAndPrizesModal
import com.example.ui.components.ResolvitosLogoBadge
import com.example.ui.components.SettingsRoleSwitcherCard
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCoral
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.DriverFareQuote
import com.example.ui.viewmodel.ResolvitosViewModel
import com.example.util.ResolvitosSoundEvent
import com.example.util.SoundAndHapticManager
import java.util.Locale

enum class ClientBottomTab(val label: String) {
    INICIO("Inicio"),
    ENCUENTRALO("Encuéntralo"),
    TAXI("Taxi"),
    PEDIDOS("Pedidos"),
    PERFIL("Perfil")
}

@Composable
fun ClientModeScreen(
    viewModel: ResolvitosViewModel,
    onOpenBcvCalculator: () -> Unit = {},
    onOpenEmergencyDirectory: () -> Unit = {},
    onRequestRoleSwitch: (String) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val liveBcv by viewModel.liveBcvRate.collectAsStateWithLifecycle()
    val comercios by viewModel.comercios.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val missions by viewModel.businessMissions.collectAsStateWithLifecycle()
    val missionClaims by viewModel.userMissionClaims.collectAsStateWithLifecycle()
    val missionProgress by viewModel.userMissionProgress.collectAsStateWithLifecycle()
    val transportRequests by viewModel.transportRequests.collectAsStateWithLifecycle()
    val transportBids by viewModel.transportBids.collectAsStateWithLifecycle()

    val effectiveBcv = liveBcv ?: settings.bcvRate
    val points = profile?.points ?: 266L

    var activeTab by remember { mutableStateOf(ClientBottomTab.INICIO) }
    var selectedStoreForDetail by remember { mutableStateOf<Comercio?>(null) }
    var selectedProductForModal by remember { mutableStateOf<Product?>(null) }
    var showSectorFilterModal by remember { mutableStateOf(false) }
    var selectedSectorFilter by remember { mutableStateOf("Todos") }
    var showPointsModal by remember { mutableStateOf(false) }
    var showRewardsScreen by remember { mutableStateOf(false) }
    var showEditProfileModal by remember { mutableStateOf(false) }
    var showCitySelectorDialog by remember { mutableStateOf(false) }
    var showMapRouteSubScreen by remember { mutableStateOf(false) }

    // Handle back navigation smoothly
    BackHandler(
        enabled = selectedStoreForDetail != null || showRewardsScreen || showMapRouteSubScreen || activeTab != ClientBottomTab.INICIO
    ) {
        when {
            selectedStoreForDetail != null -> selectedStoreForDetail = null
            showRewardsScreen -> showRewardsScreen = false
            showMapRouteSubScreen -> showMapRouteSubScreen = false
            else -> activeTab = ClientBottomTab.INICIO
        }
    }

    ScaffoldWithVideoBottomBar(
        activeTab = activeTab,
        cartCount = cartItems.sumOf { it.quantity },
        onSelectTab = { tab ->
            selectedStoreForDetail = null
            showRewardsScreen = false
            if (tab != ClientBottomTab.TAXI) {
                showMapRouteSubScreen = false
            }
            activeTab = tab
        }
    ) {
        when {
            selectedStoreForDetail != null -> {
                StoreDetailScreenVideo1(
                    comercio = selectedStoreForDetail!!,
                    products = products.filter { it.comercioId == selectedStoreForDetail!!.id },
                    bcvRate = effectiveBcv,
                    onBack = { selectedStoreForDetail = null },
                    onProductClicked = { prod -> selectedProductForModal = prod },
                    onQuickAddProduct = { prod ->
                        SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.PUSH_CHIME)
                        if (prod.hasPresentations) {
                            selectedProductForModal = prod
                        } else {
                            viewModel.addToCart(prod)
                        }
                    }
                )
            }
            showRewardsScreen -> {
                RetosYRecompensasScreenVideo1(
                    points = points,
                    missions = missions.filter { it.isActive },
                    progressList = missionProgress,
                    claims = missionClaims,
                    bcvRate = effectiveBcv,
                    onBack = { showRewardsScreen = false },
                    onAdvanceMission = { msn ->
                        SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.PUSH_CHIME)
                        viewModel.advanceMissionProgress(msn, pointsDelta = 25L, ordersDelta = 1L)
                    },
                    onClaimMission = { msn ->
                        SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                        viewModel.claimMissionPrize(msn)
                    }
                )
            }
            else -> {
                when (activeTab) {
                    ClientBottomTab.INICIO -> {
                        ClientHomeTabVideo1(
                            city = profile?.city ?: "Calabozo",
                            bcvRate = effectiveBcv,
                            points = points,
                            comercios = comercios,
                            products = products,
                            missions = missions.filter { it.isActive },
                            missionProgress = missionProgress,
                            missionClaims = missionClaims,
                            selectedSector = selectedSectorFilter,
                            onOpenBcvCalculator = onOpenBcvCalculator,
                            onOpenPointsModal = { showPointsModal = true },
                            onOpenRewardsCatalog = { showRewardsScreen = true },
                            onAdvanceMission = { msn ->
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.PUSH_CHIME)
                                viewModel.advanceMissionProgress(msn, pointsDelta = 25L, ordersDelta = 1L)
                            },
                            onClaimMission = { msn ->
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                                viewModel.claimMissionPrize(msn)
                            },
                            onOpenFilterModal = { showSectorFilterModal = true },
                            onOpenCitySelector = { showCitySelectorDialog = true },
                            onOpenMap = {
                                activeTab = ClientBottomTab.TAXI
                                showMapRouteSubScreen = true
                            },
                            onOpenTaxiBanner = {
                                activeTab = ClientBottomTab.TAXI
                            },
                            onOpenStoreDetail = { store ->
                                selectedStoreForDetail = store
                            },
                            onProductClicked = { prod ->
                                selectedProductForModal = prod
                            },
                            onOpenEmergencies = {
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.SOS_SIREN)
                                onOpenEmergencyDirectory()
                            }
                        )
                    }
                    ClientBottomTab.ENCUENTRALO -> {
                        ClientEncuentraloTabVideo1(
                            comercios = comercios,
                            selectedSector = selectedSectorFilter,
                            onOpenFilterModal = { showSectorFilterModal = true },
                            onOpenStoreDetail = { store -> selectedStoreForDetail = store }
                        )
                    }
                    ClientBottomTab.TAXI -> {
                        if (!showMapRouteSubScreen) {
                            ClientTaxiServiceHubScreen(
                                viewModel = viewModel,
                                bcvRate = effectiveBcv,
                                transportRequests = transportRequests,
                                onBack = { activeTab = ClientBottomTab.INICIO },
                                onSelectService = { category, vehicleType ->
                                    viewModel.setServiceCategory(category)
                                    viewModel.setVehicleType(vehicleType)
                                    showMapRouteSubScreen = true
                                }
                            )
                        } else {
                            ClientMobilityInteractiveFlowScreen(
                                viewModel = viewModel,
                                bcvRate = effectiveBcv,
                                transportRequests = transportRequests,
                                transportBids = transportBids,
                                onBackToHub = { showMapRouteSubScreen = false }
                            )
                        }
                    }
                    ClientBottomTab.PEDIDOS -> {
                        ClientPedidosTabVideo1(
                            cartItems = cartItems,
                            orders = orders,
                            bcvRate = effectiveBcv,
                            viewModel = viewModel
                        )
                    }
                    ClientBottomTab.PERFIL -> {
                        ClientPerfilTabVideo1(
                            profile = profile,
                            orders = orders,
                            transportRequests = transportRequests,
                            bcvRate = effectiveBcv,
                            onOpenEditProfile = { showEditProfileModal = true },
                            onOpenPointsModal = { showPointsModal = true },
                            onOpenPedidos = { activeTab = ClientBottomTab.PEDIDOS },
                            onOpenRewards = { showRewardsScreen = true },
                            onSelectRole = onRequestRoleSwitch,
                            onToggleBiometric = { enabled ->
                                viewModel.updateCityAndBiometric(profile?.city ?: "Calabozo", enabled)
                            },
                            onSignOut = onSignOut
                        )
                    }
                }
            }
        }
    }

    // --- MODALS FROM VIDEO 1 ---
    if (selectedProductForModal != null) {
        ProductDetailDialogVideo1(
            product = selectedProductForModal!!,
            bcvRate = effectiveBcv,
            onAddToCart = { prod, presLabel, priceUsd ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.PUSH_CHIME)
                viewModel.addToCart(prod, presLabel, priceUsd)
                selectedProductForModal = null
            },
            onDismiss = { selectedProductForModal = null }
        )
    }

    if (showSectorFilterModal) {
        BusinessSectorFilterDialog(
            selectedSector = selectedSectorFilter,
            onApplySector = { sector -> selectedSectorFilter = sector },
            onDismiss = { showSectorFilterModal = false }
        )
    }

    if (showPointsModal) {
        MyPointsAndPrizesModal(
            points = points,
            onOpenRewardsCatalog = { showRewardsScreen = true },
            onDismiss = { showPointsModal = false }
        )
    }

    if (showEditProfileModal) {
        EditProfileModal(
            profile = profile,
            onSave = { name, phone, cedType, cedNum, birth, gender ->
                viewModel.updateUserProfileDetails(name, phone, cedType, cedNum, birth, gender)
            },
            onDeleteAccount = {
                showEditProfileModal = false
                viewModel.deleteAccount(onSignOut)
            },
            onDismiss = { showEditProfileModal = false }
        )
    }

    if (showCitySelectorDialog) {
        val cities = listOf("Calabozo", "Caracas", "San Juan de los Morros", "Valencia", "Maracay", "Barquisimeto", "Maracaibo")
        AlertDialog(
            onDismissRequest = { showCitySelectorDialog = false },
            title = { Text("Selecciona tu Ciudad", fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cities.forEach { c ->
                        val isCurrent = (profile?.city ?: "Calabozo") == c
                        Surface(
                            color = if (isCurrent) BrightYellow else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCityAndBiometric(c, profile?.biometricLockEnabled ?: false)
                                    showCitySelectorDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📍 $c", fontWeight = FontWeight.Bold, color = ResolvitosSlate)
                                if (isCurrent) {
                                    Text("Activa", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ResolvitosSlate)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCitySelectorDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Bottom Navigation Bar matching Video 1:
 * Inicio | Encuéntralo | [Central Protruding Yellow Circle: Taxi] | Pedidos | Perfil
 */
@Composable
private fun ScaffoldWithVideoBottomBar(
    activeTab: ClientBottomTab,
    cartCount: Int,
    onSelectTab: (ClientBottomTab) -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }

        // Bottom Bar with Protruding Center Yellow Taxi Button
        Surface(
            color = Color.White,
            shadowElevation = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavStandardItem(
                    label = "Inicio",
                    icon = Icons.Default.Home,
                    selected = activeTab == ClientBottomTab.INICIO,
                    onClick = { onSelectTab(ClientBottomTab.INICIO) },
                    tag = "nav_tab_inicio"
                )
                BottomNavStandardItem(
                    label = "Encuéntralo",
                    icon = Icons.Default.Search,
                    selected = activeTab == ClientBottomTab.ENCUENTRALO,
                    onClick = { onSelectTab(ClientBottomTab.ENCUENTRALO) },
                    tag = "nav_tab_encuentralo"
                )

                // Central Protruding Yellow Taxi Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .clickable { onSelectTab(ClientBottomTab.TAXI) }
                        .testTag("nav_tab_taxi")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(BrightYellow)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Taxi",
                            tint = ResolvitosSlate,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Taxi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResolvitosSlate
                    )
                }

                Box {
                    BottomNavStandardItem(
                        label = "Pedidos",
                        icon = Icons.Default.ShoppingBag,
                        selected = activeTab == ClientBottomTab.PEDIDOS,
                        onClick = { onSelectTab(ClientBottomTab.PEDIDOS) },
                        tag = "nav_tab_pedidos"
                    )
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 2.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(StatusCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$cartCount",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                BottomNavStandardItem(
                    label = "Perfil",
                    icon = Icons.Default.Person,
                    selected = activeTab == ClientBottomTab.PERFIL,
                    onClick = { onSelectTab(ClientBottomTab.PERFIL) },
                    tag = "nav_tab_perfil"
                )
            }
        }
    }
}

@Composable
private fun BottomNavStandardItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ResolvitosSlate else Color(0xFF94A3B8),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (selected) ResolvitosSlate else Color(0xFF94A3B8)
        )
    }
}

// ============================================================================
// TAB 1: INICIO (Exact layout from Video 1: 00:05 - 00:15 & 00:25 - 00:33)
// ============================================================================
@Composable
private fun ClientHomeTabVideo1(
    city: String,
    bcvRate: Double,
    points: Long,
    comercios: List<Comercio>,
    products: List<Product>,
    missions: List<BusinessMission>,
    missionProgress: List<UserMissionProgress>,
    missionClaims: List<UserMissionClaim>,
    selectedSector: String,
    onOpenBcvCalculator: () -> Unit,
    onOpenPointsModal: () -> Unit,
    onOpenRewardsCatalog: () -> Unit,
    onAdvanceMission: (BusinessMission) -> Unit,
    onClaimMission: (BusinessMission) -> Unit,
    onOpenFilterModal: () -> Unit,
    onOpenCitySelector: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenTaxiBanner: () -> Unit,
    onOpenStoreDetail: (Comercio) -> Unit,
    onProductClicked: (Product) -> Unit,
    onOpenEmergencies: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // View mode: 0 = Grid 2x2, 1 = Large Card, 2 = List (just like Video 1)
    var storeViewMode by remember { mutableIntStateOf(2) }

    val filteredComercios = comercios.filter { c ->
        val matchesSector = selectedSector == "Todos" || c.category.equals(selectedSector, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || c.name.contains(searchQuery, ignoreCase = true)
        matchesSector && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. Yellow Gradient Top Header matching Video 1
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrightYellow,
                                BrightYellow,
                                Color(0xFFFFF59D),
                                Color(0xFFF8FAFC)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Top Row: Logo | TASA BCV | PUNTOS | Heart | Bell
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ResolvitosLogoBadge(
                            boxSize = 38.dp,
                            showTaglineRight = true,
                            darkText = true
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // TASA BCV
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .clickable { onOpenBcvCalculator() }
                                    .testTag("bcv_rate_pill")
                            ) {
                                Text(
                                    text = "TASA BCV",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResolvitosSlate.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", bcvRate)} Bs",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ResolvitosSlate
                                )
                            }

                            // PUNTOS
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.clickable { onOpenPointsModal() }
                            ) {
                                Text(
                                    text = "PUNTOS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResolvitosSlate.copy(alpha = 0.7f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ResolvitosSlate,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${points}.72",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ResolvitosSlate
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favoritos",
                                tint = ResolvitosSlate,
                                modifier = Modifier.size(22.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notificaciones",
                                tint = ResolvitosSlate,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // White Pill Search Bar: "Buscar en Resolvitos" + Filter Button
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(50),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Buscar en Resolvitos",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp
                                        )
                                    }
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtros",
                                tint = Color(0xFF64748B),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onOpenFilterModal() }
                            )
                        }
                    }

                    // Location Row: 📍 Calabozo | [🗺️ Ver mapa]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenCitySelector() }
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ResolvitosSlate,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = city,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = ResolvitosSlate
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.clickable { onOpenMap() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = null,
                                    tint = ResolvitosSlate,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Ver mapa",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ResolvitosSlate
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Hero Promotional Banner ("PIDE TU MOTOTAXI EN RESOLVITOS")
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenTaxiBanner() }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = BrightYellow,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "PIDE TU MOTOTAXI O TAXI",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "EN RESOLVITOS AL INSTANTE.",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Tarifa justa calculada en el mapa • Moto Taxi y Taxi Económico",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(BrightYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = ResolvitosSlate,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2B. Gamified Missions Progress Card Section on Main Client Home Screen
        if (missions.isNotEmpty()) {
            item {
                ClientGamifiedMissionsProgressSection(
                    points = points,
                    bcvRate = bcvRate,
                    missions = missions,
                    progressList = missionProgress,
                    claims = missionClaims,
                    onOpenRewardsCatalog = onOpenRewardsCatalog,
                    onAdvanceMission = onAdvanceMission,
                    onClaimMission = onClaimMission
                )
            }
        }

        // 3. "Tiendas en tu zona" (Square Store Icons like 911 Grill & Sound and Art Publicidad)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tiendas en tu zona",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = ResolvitosSlate
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ver todas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(filteredComercios, key = { "mini_${it.id}" }) { store ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(88.dp)
                                .clickable { onOpenStoreDetail(store) }
                        ) {
                            StoreLogoSquareArtwork(store = store, sizeDp = 82)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = store.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 4. "Destacados" / "VISTO RECIENTEMENTE" (2-column product cards matching Video 1 00:11 - 00:12)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Destacados",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Ver todos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }

                Text(
                    text = "VISTO RECIENTEMENTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF64748B)
                )

                val chunkedProducts = products.chunked(2)
                chunkedProducts.forEach { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { prod ->
                            ProductCardVideo1(
                                product = prod,
                                bcvRate = bcvRate,
                                onClick = { onProductClicked(prod) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 5. "Comercios en tu zona" with 3-mode View Switcher (Grid 2x2 | Large Card | List)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comercios en tu zona",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = ResolvitosSlate
                    )
                    ViewModeSwitcherRow(
                        currentMode = storeViewMode,
                        onSelectMode = { storeViewMode = it }
                    )
                }

                when (storeViewMode) {
                    0 -> {
                        // 2-Column Grid
                        filteredComercios.chunked(2).forEach { rowStores ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowStores.forEach { store ->
                                    StoreGridCardVideo1(
                                        store = store,
                                        onClick = { onOpenStoreDetail(store) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowStores.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    1 -> {
                        // Large Featured Cards (Video 1, 00:24 & 00:30)
                        filteredComercios.forEach { store ->
                            StoreLargeBannerCardVideo1(
                                store = store,
                                onClick = { onOpenStoreDetail(store) }
                            )
                        }
                    }
                    else -> {
                        // Horizontal List Cards (Video 1, 00:13 & 00:18)
                        filteredComercios.forEach { store ->
                            StoreListRowCardVideo1(
                                store = store,
                                onClick = { onOpenStoreDetail(store) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Red 24-Hour Emergency Assistance Card (Video 1, 00:13-00:14)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onOpenEmergencies() }
                    .testTag("sos_emergency_button")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFE11D48), Color(0xFFBE123C))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "ASISTENCIA 24 HORAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "Emergencias",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Ambulancias, Bomberos...",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ABRIR >",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFBE123C)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. "¡Una app 100% Venezolana!" Card + Legal Disclaimer Footer (Video 1, 00:14-00:15)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "¡Una app 100%\nVenezolana!",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Regístrate gratis, muévete y pide lo que quieras!",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ResolvitosLogoBadge(boxSize = 128.dp, showTaglineRight = false)
                    }
                    Text(
                        text = "CONOCE MÁS →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResolvitosSlate
                    )
                }
            }

            Text(
                text = "Grupo Resolvitos VE C.A. no está autorizado por la Superintendencia de las Instituciones del Sector Bancario (SUDEBAN) para intermediar o fungir como pasarela de pagos entre clientes y comercios. Resolvitos es un portal que conecta a clientes con comercios y conductores afiliados. Los clientes depositarán los pagos directamente en las cuentas bancarias de los comercios o conductores.",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ViewModeSwitcherRow(
    currentMode: Int,
    onSelectMode: (Int) -> Unit
) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val icons = listOf(
                0 to Icons.Default.GridView,
                1 to Icons.Default.ViewAgenda,
                2 to Icons.Default.ViewList
            )
            icons.forEach { (mode, icon) ->
                val active = currentMode == mode
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) Color.White else Color.Transparent)
                        .clickable { onSelectMode(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (active) ResolvitosSlate else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreLogoSquareArtwork(
    store: Comercio,
    sizeDp: Int
) {
    val is911 = store.name.contains("911", ignoreCase = true)
    val bgBrush = if (is911) {
        Brush.linearGradient(listOf(Color(0xFF7F1D1D), Color(0xFFDC2626), Color(0xFFF97316)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7)))
    }
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            Text(
                text = if (is911) "911\nGRILL" else store.name.take(10).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (sizeDp > 60) 13.sp else 10.sp,
                lineHeight = if (sizeDp > 60) 14.sp else 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductCardVideo1(
    product: Product,
    bcvRate: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOsole = product.name.contains("OSOLE", ignoreCase = true)
    val isMavesa = product.name.contains("Mavesa", ignoreCase = true)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Product Artwork Box with Top-Right Yellow Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            isOsole -> Color(0xFFFFF1F2)
                            isMavesa -> Color(0xFFFFFBEB)
                            else -> Color(0xFF1E293B)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Surface(
                        color = when {
                            isOsole -> Color(0xFFDC2626)
                            isMavesa -> Color(0xFF1D4ED8)
                            else -> BrightYellow
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when {
                                isOsole -> "OSOLE"
                                isMavesa -> "mavesa"
                                else -> "POLLO 5$"
                            },
                            color = if (isOsole || isMavesa) Color.White else ResolvitosSlate,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOsole || isMavesa) ResolvitosSlate else Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }

                // Top-Right Yellow Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(BrightYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌙", fontSize = 11.sp)
                }
            }

            Text(
                text = product.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = ResolvitosSlate,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "5.0",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3B82F6)
                )
            }

            // Dual Price Box ($1.20 | 1063.47 Bs)
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatUsd(product.priceUsd),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = ResolvitosSlate
                    )
                    Text("|", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(
                        text = formatBs(product.priceUsd, bcvRate),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = "Envío gratis",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StatusEmerald
            )
        }
    }
}

@Composable
private fun StoreListRowCardVideo1(
    store: Comercio,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                StoreLogoSquareArtwork(store = store, sizeDp = 52)
                Column {
                    Text(
                        text = store.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = ResolvitosSlate,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${store.category} • ",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "📍 ${store.distanceLabel.ifBlank { "457 m" }}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "5",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
private fun StoreGridCardVideo1(
    store: Comercio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ResolvitosSlate),
                contentAlignment = Alignment.Center
            ) {
                StoreLogoSquareArtwork(store = store, sizeDp = 70)
            }
            Text(
                text = store.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = ResolvitosSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${store.category} • ${store.distanceLabel}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = "🕒 30-45 min",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun StoreLargeBannerCardVideo1(
    store: Comercio,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF7C2D12))
                        )
                    )
                    .padding(12.dp)
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("★ 5 ", fontWeight = FontWeight.Black, fontSize = 12.sp, color = ResolvitosSlate)
                        Text("(0+)", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrightYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌙", fontSize = 16.sp)
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = store.name.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Aliado Oficial Resolvitos • ${store.city}",
                        fontSize = 12.sp,
                        color = BrightYellow
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StoreLogoSquareArtwork(store = store, sizeDp = 44)
                Column {
                    Text(
                        text = store.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "${store.category.uppercase()} • 🕒 30-45 MIN • 📍 ${store.distanceLabel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// ============================================================================
// STORE DETAIL SCREEN (Video 1, 01:13 - 01:50: 911 Grill & Sound and Art Publicidad)
// ============================================================================
@Composable
private fun StoreDetailScreenVideo1(
    comercio: Comercio,
    products: List<Product>,
    bcvRate: Double,
    onBack: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onQuickAddProduct: (Product) -> Unit
) {
    var activeStoreTab by remember { mutableIntStateOf(0) } // 0 = Menú, 1 = Reseñas
    val is911 = comercio.name.contains("911", ignoreCase = true)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Cover Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        if (is911) {
                            Brush.verticalGradient(listOf(Color(0xFF450A0A), Color(0xFF991B1B), Color(0xFF0F172A)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF090D16)))
                        }
                    )
                    .padding(16.dp)
            ) {
                // Top Action Buttons (←, Share, ♥)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Surface(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorito", tint = StatusCoral, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Bottom Store Avatar + Name + "Siguiendo"
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(3.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            StoreLogoSquareArtwork(store = comercio, sizeDp = 68)
                        }
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(50),
                            shadowElevation = 2.dp,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Text(
                                text = "★ 5",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = comercio.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${comercio.followersCount} SEGUIDORES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Siguiendo",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Store Info & Delivery Benefit Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "★ 5 (0+ reseñas)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = ResolvitosSlate
                    )
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "● ABIERTO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "🕒 ${comercio.etaMinutes} min",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Benefit Banner matching Video 1 (01:14 & 01:36)
                if (is911) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrightYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ResolvitosSlate)
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "RED DE ENTREGAS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309)
                                    )
                                    Surface(color = ResolvitosSlate, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "EN RESOLVITOS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BrightYellow,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Tu pedido llegará a ti en Resolvitos gracias a la red de deliverys locales, elige el delivery de tu preferencia y hazle seguimiento hasta la puerta de casa en tiempo real 😉",
                                    fontSize = 12.sp,
                                    color = ResolvitosSlate
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StatusEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White)
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "BENEFICIO EXCLUSIVO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF047857)
                                    )
                                    Surface(color = StatusEmerald, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "100% GRATIS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Envío gratis por compras a partir de $20.00",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = "Aprovecha y llena tu carrito para disfrutar de este beneficio",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "${comercio.category} • ${comercio.city}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                // Action Chips Row (Siguiendo | WhatsApp | Ver en Mapa | Llamar | Compartir)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreActionChip("🔔 Siguiendo (1)", Color(0xFFF1F5F9), ResolvitosSlate)
                    StoreActionChip("💬 WhatsApp", Color(0xFFECFDF5), Color(0xFF059669))
                    StoreActionChip("🗺️ Ver en Mapa", Color(0xFFEFF6FF), Color(0xFF2563EB))
                    StoreActionChip("📞 Llamar", Color(0xFFFFFBEB), Color(0xFFD97706))
                    StoreActionChip("🔗 Compartir", Color(0xFFF8FAFC), ResolvitosSlate)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = ResolvitosSlate, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = comercio.address,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ResolvitosSlate
                    )
                }

                // Tabs: Menú | Reseñas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.clickable { activeStoreTab = 0 },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏪 Menú",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (activeStoreTab == 0) ResolvitosSlate else Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (activeStoreTab == 0) {
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(3.dp)
                                    .background(BrightYellow)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.clickable { activeStoreTab = 1 },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "☆ Reseñas",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (activeStoreTab == 1) ResolvitosSlate else Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (activeStoreTab == 1) {
                            Box(
                                modifier = Modifier
                                    .width(78.dp)
                                    .height(3.dp)
                                    .background(BrightYellow)
                            )
                        }
                    }
                }

                if (activeStoreTab == 0) {
                    Text(
                        text = "CATÁLOGO (${products.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF94A3B8)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = BrightYellow,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Todos",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = comercio.category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Store Products Grid with Yellow "+" Button
                    products.chunked(2).forEach { rowProds ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProds.forEach { prod ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onProductClicked(prod) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        ProductCardVideo1(
                                            product = prod,
                                            bcvRate = bcvRate,
                                            onClick = { onProductClicked(prod) }
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (is911) "Cotizar" else "Disponible",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (is911) Color(0xFFD97706) else StatusEmerald
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(BrightYellow)
                                                    .clickable { onQuickAddProduct(prod) }
                                                    .testTag("add_to_cart_${prod.id}"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Añadir",
                                                    tint = ResolvitosSlate,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (rowProds.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // Reseñas Tab (Video 1, 01:48 - 01:49: "Listo", "Chao", "Excelente", "Buen")
                    val sampleReviews = listOf(
                        "Listo" to 4,
                        "Chao" to 5,
                        "Excelente" to 5,
                        "Buen" to 4
                    )
                    sampleReviews.forEach { (comment, stars) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                    }
                                    Row {
                                        repeat(5) { idx ->
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (idx < stars) Color(0xFFF59E0B) else Color(0xFFE2E8F0),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = comment,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreActionChip(
    label: String,
    bgColor: Color,
    textColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/**
 * Product Detail Modal matching Video 1 (01:20, 01:40, 01:43):
 * Supports presentations (250GR $1.00, 500GR $4.00, 1KG $5.00) with Bs conversion and "+ Añadir al Carrito"
 */
@Composable
private fun ProductDetailDialogVideo1(
    product: Product,
    bcvRate: Double,
    onAddToCart: (Product, String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val presentations = remember(product) {
        if (product.hasPresentations) {
            listOf(
                ProductPresentation("250GR", 1.00),
                ProductPresentation("500GR", 4.00),
                ProductPresentation("1KG", 5.00)
            )
        } else {
            emptyList()
        }
    }
    var selectedPresentation by remember { mutableStateOf<ProductPresentation?>(presentations.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Image Box with ♥ and ✕
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    ProductCardVideo1(
                        product = product,
                        bcvRate = bcvRate,
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Surface(
                        color = Color.White,
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(36.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ResolvitosSlate)
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = product.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = ResolvitosSlate,
                        modifier = Modifier.weight(1f)
                    )
                    val activePrice = selectedPresentation?.priceUsd ?: product.priceUsd
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatUsd(activePrice),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = " | ${formatBs(activePrice, bcvRate)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )

                if (presentations.isNotEmpty()) {
                    Text(
                        text = "PRESENTACIONES *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF64748B)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presentations.forEach { pres ->
                            val isSelected = selectedPresentation?.label == pres.label
                            Surface(
                                color = if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) BrightYellow else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPresentation = pres }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = pres.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = formatUsd(pres.priceUsd),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = formatBs(pres.priceUsd, bcvRate),
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val label = selectedPresentation?.label ?: ""
                        val price = selectedPresentation?.priceUsd ?: product.priceUsd
                        onAddToCart(product, label, price)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightYellow,
                        contentColor = ResolvitosSlate
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "+  Añadir al Carrito",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// TAB 2: ENCUÉNTRALO (Video 1, 00:16 - 00:24)
// ============================================================================
@Composable
private fun ClientEncuentraloTabVideo1(
    comercios: List<Comercio>,
    selectedSector: String,
    onOpenFilterModal: () -> Unit,
    onOpenStoreDetail: (Comercio) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var viewMode by remember { mutableIntStateOf(2) }

    val filtered = comercios.filter { c ->
        val matchesSector = selectedSector == "Todos" || c.category.equals(selectedSector, ignoreCase = true)
        val matchesQuery = query.isBlank() || c.name.contains(query, ignoreCase = true) || c.category.contains(query, ignoreCase = true)
        matchesSector && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Yellow Header: "¿Qué buscamos? 🧐"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrightYellow)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "¿Qué buscamos? 🧐",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = ResolvitosSlate
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text("Restaurant, plato o cat", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { onOpenFilterModal() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = ResolvitosSlate)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Explorar locales",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Surface(
                            color = Color(0xFFFFF9C4),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${filtered.size}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    ViewModeSwitcherRow(
                        currentMode = viewMode,
                        onSelectMode = { viewMode = it }
                    )
                }
            }

            when (viewMode) {
                0 -> {
                    items(filtered.chunked(2)) { rowStores ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowStores.forEach { store ->
                                StoreGridCardVideo1(
                                    store = store,
                                    onClick = { onOpenStoreDetail(store) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowStores.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                1 -> {
                    items(filtered, key = { it.id }) { store ->
                        StoreLargeBannerCardVideo1(
                            store = store,
                            onClick = { onOpenStoreDetail(store) }
                        )
                    }
                }
                else -> {
                    items(filtered, key = { it.id }) { store ->
                        StoreListRowCardVideo1(
                            store = store,
                            onClick = { onOpenStoreDetail(store) }
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 3: TAXI — MOVILIDAD HUB (Video 1, 00:34 - 00:37) + INTERACTIVE MAP
// ============================================================================
@Composable
private fun ClientTaxiServiceHubVideo1(
    onBack: () -> Unit,
    onSelectService: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2732))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrightYellow)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Resolvitos Movilidad",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "ELIGE TU SERVICIO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "☀️ 30°C   🌙 22°C",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué necesitas hoy?",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = "Selecciona una opción para comenzar tu solicitud personalizada",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }

        // 2x2 Grid of 4 Bright Yellow Cards matching Video 1 (00:34 - 00:37)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            YellowMobilityServiceCard(
                badge = "PASAJEROS",
                title = "Taxi / Mototaxi",
                subtitle = "Mototaxi y Taxi Económico con tarifa por km",
                chips = listOf("Moto", "Taxi Económico"),
                icon = Icons.Default.DirectionsCar,
                onClick = { onSelectService("transport", "moto") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_pasajeros")
            )
            YellowMobilityServiceCard(
                badge = "EXPRESS",
                title = "Envío de Paquete",
                subtitle = "Encomiendas, documentos, llaves o paquetes",
                chips = listOf("Directo", "Sin escalas"),
                icon = Icons.Default.Inventory2,
                onClick = { onSelectService("encomienda", "moto") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_express")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            YellowMobilityServiceCard(
                badge = "DILIGENCIAS",
                title = "PERSONAL SHOPPER",
                subtitle = "Compras, pagos y diligencias a tu medida",
                chips = listOf("Compras", "Trámites"),
                icon = Icons.Default.ShoppingBag,
                onClick = { onSelectService("mandado", "moto") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_shopper")
            )
            YellowMobilityServiceCard(
                badge = "⚡ 1 TOQUE",
                title = "Transporte Rápido",
                subtitle = "Pide en segundos con radar masivo sin esperas",
                chips = listOf("GPS Inmediato", "Radar Resolvitos"),
                icon = Icons.Default.Bolt,
                onClick = { onSelectService("transport", "taxi_economico") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_rapido")
            )
        }

        // Electric Blue Banner: "CARRO TAXI EXPRESS • ¡Pide a dónde y arrancamos de una!"
        Surface(
            color = Color(0xFF1D4ED8),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectService("transport", "taxi_economico") }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = BrightYellow,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "CARRO TAXI ECONÓMICO EXPRESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = BrightYellow
                        )
                        Text(
                            text = "¡Pide a dónde y arrancamos de una!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }

        // Road & Vehicles Strip + "Tarifas transparentes"
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("🚚", fontSize = 26.sp)
                Text("🚙", fontSize = 26.sp)
                Text("🚕", fontSize = 28.sp)
                Text("🛵", fontSize = 26.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Tarifas transparentes calculadas en el mapa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}

@Composable
private fun YellowMobilityServiceCard(
    badge: String,
    title: String,
    subtitle: String,
    chips: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrightYellow),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ResolvitosSlate),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = BrightYellow, modifier = Modifier.size(22.dp))
                }
                Surface(
                    color = ResolvitosSlate,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = BrightYellow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = ResolvitosSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = ResolvitosSlate.copy(alpha = 0.8f),
                minLines = 2,
                maxLines = 2
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                chips.forEach { chip ->
                    Surface(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = chip,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Map & Per-Driver Fare Calculator Screen (Moto Taxi & Taxi Económico)
 */
@Composable
private fun ClientMobilityInteractiveMapScreen(
    viewModel: ResolvitosViewModel,
    bcvRate: Double,
    transportRequests: List<TransportRequest>,
    transportBids: List<TransportBid>,
    onBackToHub: () -> Unit
) {
    val context = LocalContext.current
    val selectedOrigin by viewModel.selectedOrigin.collectAsStateWithLifecycle()
    val selectedDestination by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val selectedVehicleType by viewModel.selectedVehicleType.collectAsStateWithLifecycle()
    val selectedServiceCategory by viewModel.selectedServiceCategory.collectAsStateWithLifecycle()
    val drivers by viewModel.drivers.collectAsStateWithLifecycle()

    val distanceKm = remember(selectedOrigin, selectedDestination) {
        viewModel.currentRouteDistanceKm()
    }
    val driverQuotes = remember(selectedOrigin, selectedDestination, selectedVehicleType, drivers) {
        viewModel.computeDriverQuotesForCurrentRoute()
    }

    var selectedDriverQuote by remember(driverQuotes) {
        mutableStateOf(driverQuotes.firstOrNull())
    }
    var packageNotes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { onBackToHub() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }
                    Column {
                        Text(
                            text = "Mapa de Destino y Tarifas",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Toca el mapa o elige tu destino para calcular el monto exacto",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Exclusive Vehicle Selector: Moto Taxi (Viajes + Delivery) or Taxi Económico
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isMoto = selectedVehicleType == "moto"
                Surface(
                    color = if (isMoto) BrightYellow else Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setVehicleType("moto") }
                        .testTag("vehicle_type_moto")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = if (isMoto) ResolvitosSlate else BrightYellow
                        )
                        Column {
                            Text(
                                text = "Moto Taxi",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = if (isMoto) ResolvitosSlate else Color.White
                            )
                            Text(
                                text = "Pasajeros y Delivery",
                                fontSize = 10.sp,
                                color = if (isMoto) ResolvitosSlate.copy(alpha = 0.8f) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                val isTaxi = selectedVehicleType == "taxi_economico"
                Surface(
                    color = if (isTaxi) BrightYellow else Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setVehicleType("taxi_economico") }
                        .testTag("vehicle_type_taxi_economico")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (isTaxi) ResolvitosSlate else BrightYellow
                        )
                        Column {
                            Text(
                                text = "Taxi Económico",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = if (isTaxi) ResolvitosSlate else Color.White
                            )
                            Text(
                                text = "Confort al mejor precio",
                                fontSize = 10.sp,
                                color = if (isTaxi) ResolvitosSlate.copy(alpha = 0.8f) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Interactive Map Canvas
        item {
            InteractiveDestinationMapCard(
                origin = selectedOrigin,
                destination = selectedDestination,
                distanceKm = distanceKm,
                onTapMapDestination = { normX, normY ->
                    viewModel.setDestinationFromMapTap(normX, normY)
                },
                onSelectOriginPreset = { viewModel.setOriginPoint(it) },
                onSelectDestinationPreset = { viewModel.setDestinationPoint(it) }
            )
        }

        // Live Driver Fare Calculation Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Tarifa Calculada según cada Conductor (${String.format(Locale.US, "%.2f", distanceKm)} km)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Selecciona tu piloto para ver su tarifa base + km excedente configurada:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    driverQuotes.forEach { quote ->
                        val isSelected = selectedDriverQuote?.driver?.id == quote.driver.id
                        Surface(
                            color = if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) BrightYellow else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDriverQuote = quote }
                                .testTag("driver_quote_${quote.driver.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = quote.driver.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "${quote.driver.vehicleModel} (${quote.driver.vehiclePlate}) • ★ ${quote.driver.rating}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "Base $${String.format(Locale.US, "%.2f", quote.driver.baseFareUsd)} (cubre ${quote.driver.baseIncludedKm.toInt()} km) + $${String.format(Locale.US, "%.2f", quote.driver.ratePerKmUsd)}/km extra",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatUsd(quote.calculatedFareUsd),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = StatusEmerald
                                    )
                                    Text(
                                        text = formatBs(quote.calculatedFareUsd, bcvRate),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ResolvitosSlate
                                    )
                                }
                            }
                        }
                    }

                    if (selectedServiceCategory != "transport") {
                        OutlinedTextField(
                            value = packageNotes,
                            onValueChange = { packageNotes = it },
                            label = { Text("Detalle del paquete o diligencia") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    val activeQuote = selectedDriverQuote ?: driverQuotes.firstOrNull()
                    val finalFareUsd = activeQuote?.calculatedFareUsd ?: 1.50

                    Button(
                        onClick = {
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.ALERT_LOOP)
                            viewModel.requestRideOrMandado(
                                selectedQuote = activeQuote,
                                customOfferedUsd = finalFareUsd,
                                packageDescription = packageNotes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("request_ride_button")
                    ) {
                        Text(
                            text = "Confirmar Destino • ${formatDualPrice(finalFareUsd, bcvRate)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Active Transport Requests & Live Bids
        items(transportRequests, key = { it.id }) { req ->
            val reqBids = transportBids.filter { it.requestId == req.id && it.status == "pending" }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (req.vehicleType == "moto") "🛵 Moto Taxi / Delivery" else "🚕 Taxi Económico",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = ResolvitosSlate
                        )
                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = req.status.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("📍 Origen: ${req.originAddress}", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("🏁 Destino: ${req.destinationAddress}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)
                    Text(
                        text = "Distancia: ${String.format(Locale.US, "%.2f", req.distanceKm)} km • Monto: ${formatDualPrice(req.priceUsd, bcvRate)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = StatusEmerald
                    )

                    reqBids.forEach { bid ->
                        Surface(
                            color = Color(0xFFFFF8CC),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bid.driverName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("${bid.vehicleModel} • ${formatDualPrice(bid.offeredPriceUsd, bcvRate)}", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.acceptDriverBid(req, bid) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald, contentColor = Color.White)
                                ) {
                                    Text("Aceptar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.openChat(req.id, "Chat Viaje • ${req.destinationAddress.take(20)}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir Chat / Llamada VoIP con Piloto", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveDestinationMapCard(
    origin: MapPointPreset,
    destination: MapPointPreset,
    distanceKm: Double,
    onTapMapDestination: (Float, Float) -> Unit,
    onSelectOriginPreset: (MapPointPreset) -> Unit,
    onSelectDestinationPreset: (MapPointPreset) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🗺️ Mapa GPS en Vivo",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = ResolvitosSlate
                )
                Surface(color = BrightYellow, shape = RoundedCornerShape(50)) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", distanceKm)} km",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = ResolvitosSlate,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val normX = (offset.x / size.width.toFloat()).coerceIn(0.05f, 0.95f)
                            val normY = (offset.y / size.height.toFloat()).coerceIn(0.05f, 0.95f)
                            onTapMapDestination(normX, normY)
                        }
                    }
                    .testTag("interactive_destination_map")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    for (i in 1..5) {
                        val y = h * (i / 6f)
                        drawLine(Color(0xFF1E293B), Offset(0f, y), Offset(w, y), 2f)
                    }
                    for (j in 1..5) {
                        val x = w * (j / 6f)
                        drawLine(Color(0xFF1E293B), Offset(x, 0f), Offset(x, h), 2f)
                    }

                    fun toCanvasOffset(lat: Double, lng: Double): Offset {
                        val x = (((lng - (-67.4480)) / 0.0480).toFloat().coerceIn(0.08f, 0.92f)) * w
                        val y = ((1.0 - ((lat - 8.9050) / 0.0450)).toFloat().coerceIn(0.10f, 0.90f)) * h
                        return Offset(x, y)
                    }

                    val origPt = toCanvasOffset(origin.lat, origin.lng)
                    val destPt = toCanvasOffset(destination.lat, destination.lng)

                    drawLine(
                        color = BrightYellow,
                        start = origPt,
                        end = destPt,
                        strokeWidth = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                    )
                    drawCircle(StatusEmerald, radius = 12f, center = origPt)
                    drawCircle(StatusCoral, radius = 14f, center = destPt)
                }

                Surface(
                    color = ResolvitosSlate.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🟢 Origen: ${origin.name}\n🔴 Destino: ${destination.name}",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text("1. Punto de Recogida:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CITY_MAP_PRESETS, key = { "orig_${it.name}" }) { preset ->
                    val selected = origin.name == preset.name
                    Surface(
                        color = if (selected) StatusEmerald else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable { onSelectOriginPreset(preset) }
                    ) {
                        Text(
                            text = preset.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else ResolvitosSlate,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Text("2. Selecciona tu Destino:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CITY_MAP_PRESETS, key = { "dest_${it.name}" }) { preset ->
                    val selected = destination.name == preset.name
                    Surface(
                        color = if (selected) StatusCoral else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable { onSelectDestinationPreset(preset) }
                    ) {
                        Text(
                            text = preset.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else ResolvitosSlate,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 4: PEDIDOS (Video 1, 00:38 - 00:41: NUEVO PEDIDO | MIS PEDIDOS)
// ============================================================================
@Composable
private fun ClientPedidosTabVideo1(
    cartItems: List<CartItem>,
    orders: List<Order>,
    bcvRate: Double,
    viewModel: ResolvitosViewModel
) {
    val context = LocalContext.current
    // 0 = NUEVO PEDIDO, 1 = MIS PEDIDOS
    var selectedSubTab by remember { mutableIntStateOf(if (cartItems.isNotEmpty()) 0 else 0) }
    var deliveryType by remember { mutableStateOf("delivery") }
    var paymentMethod by remember { mutableStateOf("Pago Móvil") }
    var paymentRef by remember { mutableStateOf("994421") }
    var deliveryAddress by remember { mutableStateOf("Ubicación GPS confirmada - Calabozo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Segmented Pill Bar: [🛍️ NUEVO PEDIDO] | [🕒 MIS PEDIDOS]
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp)
            ) {
                val tabs = listOf(
                    0 to "🛍️  NUEVO PEDIDO",
                    1 to "🕒  MIS PEDIDOS"
                )
                tabs.forEach { (idx, title) ->
                    val active = selectedSubTab == idx
                    Surface(
                        color = if (active) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(50),
                        shadowElevation = if (active) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSubTab = idx }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (active) ResolvitosSlate else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        if (selectedSubTab == 0) {
            // NUEVO PEDIDO (Cart or Empty Cart matching Video 1, 00:38)
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(78.dp)
                        )
                        Text(
                            text = "Carrito vacío",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            } else {
                val subtotalUsd = cartItems.sumOf { it.unitPriceUsd * it.quantity }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { "${it.product.id}_${it.presentationLabel}" }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val pres = if (item.presentationLabel.isNotBlank()) " (${item.presentationLabel})" else ""
                                    Text(
                                        text = "${item.product.name}$pres",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = formatDualPrice(item.unitPriceUsd * item.quantity, bcvRate),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusEmerald
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.removeFromCart(item.product.id, item.presentationLabel) }) {
                                        Icon(Icons.Default.Remove, contentDescription = "Menos")
                                    }
                                    Text("${item.quantity}", fontWeight = FontWeight.ExtraBold)
                                    IconButton(onClick = { viewModel.addToCart(item.product, item.presentationLabel, item.unitPriceUsd) }) {
                                        Icon(Icons.Default.Add, contentDescription = "Más")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Confirmar Pedido Bimonetario", fontWeight = FontWeight.ExtraBold, color = ResolvitosSlate)
                                OutlinedTextField(
                                    value = deliveryAddress,
                                    onValueChange = { deliveryAddress = it },
                                    label = { Text("Dirección GPS de Entrega") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = paymentRef,
                                    onValueChange = { paymentRef = it },
                                    label = { Text("Referencia Pago Móvil / Efectivo") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "Total a pagar: ${formatDualPrice(subtotalUsd, bcvRate)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = ResolvitosSlate
                                )
                                Button(
                                    onClick = {
                                        SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_CASH)
                                        viewModel.submitCartOrder(
                                            deliveryType = deliveryType,
                                            deliveryAddress = deliveryAddress,
                                            paymentMethod = paymentMethod,
                                            paymentReference = paymentRef,
                                            onOrderCreated = { selectedSubTab = 1 }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrightYellow,
                                        contentColor = ResolvitosSlate
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("checkout_submit_button")
                                ) {
                                    Text("Enviar Pedido Ahora", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // MIS PEDIDOS (Video 1, 00:40 - 00:41)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🕒  PEDIDOS RECIENTES /\nHISTORIAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "${orders.size} Recientes",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                items(orders, key = { it.id }) { ord ->
                    OrderHistoryCardVideo1(
                        order = ord,
                        bcvRate = bcvRate,
                        onOpenChat = {
                            viewModel.openChat(ord.id, "Pedido • ${ord.comercioName}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCardVideo1(
    order: Order,
    bcvRate: Double,
    onOpenChat: () -> Unit
) {
    val isCancelled = order.status == "cancelled"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.comercioName.take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = order.comercioName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = ResolvitosSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "#${order.id.takeLast(6).uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Text(
                            text = "15/10/2026 • 05:17 P. M.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = if (isCancelled) Color(0xFFFFF1F2) else Color(0xFFECFDF5),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            1.dp,
                            if (isCancelled) Color(0xFFFECDD3) else Color(0xFFA7F3D0)
                        )
                    ) {
                        Text(
                            text = if (isCancelled) "● CANCELADO POR TI" else "✓ ENTREGADO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCancelled) StatusCoral else StatusEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatUsd(order.totalUsd),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = formatBs(order.totalUsd, bcvRate),
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NearMe, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.deliveryAddress,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Text(
                text = order.itemsSummary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )

            if (isCancelled && order.cancelReason.isNotBlank()) {
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ⓘ MOTIVO: ${order.cancelReason}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusCoral,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            } else if (order.driverName.isNotBlank()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BrightYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = order.driverName.take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = ResolvitosSlate
                            )
                        }
                        Column {
                            Text(
                                text = "REPARTIDOR ASIGNADO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = order.driverName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = ResolvitosSlate
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCancelled) "✕ Pedido Cerrado" else "★ Calificación registrada",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Ver Detalle del Pedido >",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ResolvitosSlate,
                    modifier = Modifier.clickable { onOpenChat() }
                )
            }
        }
    }
}

// ============================================================================
// TAB 5: PERFIL (Video 1, 00:42 - 01:03 + SUICHE DE APLICACIÓN EN AJUSTES)
// ============================================================================
@Composable
private fun ClientPerfilTabVideo1(
    profile: UserProfile?,
    orders: List<Order>,
    transportRequests: List<TransportRequest>,
    bcvRate: Double,
    onOpenEditProfile: () -> Unit,
    onOpenPointsModal: () -> Unit,
    onOpenPedidos: () -> Unit,
    onOpenRewards: () -> Unit,
    onSelectRole: (String) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    var historyFilter by remember { mutableIntStateOf(0) } // 0 = Todos, 1 = Viajes, 2 = Mandados
    var notificationsEnabled by remember { mutableStateOf(true) }
    var onlineLocationEnabled by remember { mutableStateOf(true) }
    var expandedFaq by remember { mutableIntStateOf(-1) }

    val points = profile?.points ?: 266L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // 1. Dark Profile Header Card (Video 1, 00:42)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ResolvitosSlate)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MI CUENTA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF94A3B8)
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onOpenEditProfile() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Settings, contentDescription = "Editar Perfil", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, BrightYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ResolvitosLogoBadge(boxSize = 52.dp, showTaglineRight = false)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = profile?.displayName?.ifBlank { "Louis Naranjo" } ?: "Louis Naranjo",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = profile?.email?.ifBlank { "louisnaranjo1@gmail.com" } ?: "louisnaranjo1@gmail.com",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Surface(
                                color = BrightYellow,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.clickable { onOpenPointsModal() }
                            ) {
                                Text(
                                    text = "🏅 $points pts >",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Three Quick Cards: Mis Puntos | Pedidos | Regalos
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileQuickCard(
                    title = "Mis Puntos",
                    icon = Icons.Default.EmojiEvents,
                    onClick = onOpenPointsModal,
                    modifier = Modifier.weight(1f)
                )
                ProfileQuickCard(
                    title = "Pedidos",
                    icon = Icons.Default.ShoppingBag,
                    onClick = onOpenPedidos,
                    modifier = Modifier.weight(1f)
                )
                ProfileQuickCard(
                    title = "Regalos",
                    icon = Icons.Default.CardGiftcard,
                    onClick = onOpenRewards,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Dark Banner: Misiones y Premios + "Canjear" Button
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResolvitosSlate),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onOpenRewards() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrightYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = StatusCoral)
                        }
                        Column {
                            Text(
                                text = "Misiones y Premios...",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Acumula puntos con cada viaje o compra...",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Button(
                        onClick = onOpenRewards,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Canjear", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 4. "Historial de Actividad" (Video 1, 00:44 & 00:55)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial de Actividad",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = ResolvitosSlate
                    )
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "57 total",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResolvitosSlate,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf(
                        0 to "Todos  57",
                        1 to "Viajes  16",
                        2 to "Mandados  0"
                    )
                    filters.forEach { (idx, label) ->
                        val active = historyFilter == idx
                        Surface(
                            color = if (active) ResolvitosSlate else Color.White,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, if (active) ResolvitosSlate else Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable { historyFilter = idx }
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (active) BrightYellow else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Sample Activity Rows matching Video 1 (00:44)
                ActivityItemRowVideo1(
                    title = "8.91819, -67.43109",
                    subtitle = "#2988E • 23/10/2026 03:...",
                    amountUsd = 1.50,
                    bcvRate = bcvRate,
                    statusLabel = "COMPLETADO",
                    isCompleted = true
                )
                ActivityItemRowVideo1(
                    title = "Parque Rómulo G...",
                    subtitle = "#64AA3 • 21/10/2026 02:...",
                    amountUsd = 1.50,
                    bcvRate = bcvRate,
                    statusLabel = "COMPLETADO",
                    isCompleted = true
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ver todas (57)  ⌄",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = ResolvitosSlate
                        )
                    }
                }
            }
        }

        // 5. "Ajustes" (Configuraciones) — INCLUDES THE ROLE SWITCHER ("SUICHE")
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ajustes",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ResolvitosSlate
                )

                // SUICHE DE APLICACIÓN (CAMBIAR A CONDUCTOR O ADMINISTRADOR DE NEGOCIOS)
                SettingsRoleSwitcherCard(
                    activeRole = profile?.activeRole ?: "client",
                    onSelectRole = onSelectRole
                )

                // Notificaciones
                SettingsToggleRowVideo1(
                    title = "Notificaciones",
                    subtitle = "Recibe promos y actualizaciones",
                    icon = Icons.Default.NotificationsNone,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                // Bloqueo Biométrico
                SettingsToggleRowVideo1(
                    title = "Bloqueo Biométrico",
                    subtitle = "Protege tu cuenta con huella o rostro",
                    icon = Icons.Default.Fingerprint,
                    checked = profile?.biometricLockEnabled ?: false,
                    onCheckedChange = onToggleBiometric
                )

                // Ubicación en Línea
                SettingsToggleRowVideo1(
                    title = "Ubicación en Línea",
                    subtitle = "Permite usar todas las funciones de la app",
                    icon = Icons.Default.NearMe,
                    checked = onlineLocationEnabled,
                    onCheckedChange = { onlineLocationEnabled = it }
                )

                // Reportar una Falla
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF1F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = StatusCoral)
                            }
                            Column {
                                Text("Reportar una Falla", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ResolvitosSlate)
                                Text("¿Problemas con la app? Ayúdanos a mejorar", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCBD5E1))
                    }
                }
            }
        }

        // 6. PREGUNTAS FRECUENTES (FAQ) + Cerrar Sesión + Footer (Video 1, 00:46 - 00:47)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PREGUNTAS FRECUENTES (FAQ)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF64748B)
                )

                val faqs = listOf(
                    "¿Qué es Resolvitos?" to "Resolvitos Taxi Deliverys es la súper aplicación venezolana que integra Marketplace bimonetario (USD/Bs BCV), Moto Taxi, Taxi Económico, Delivery y Misiones con premios.",
                    "¿Qué productos o servicios puedo encontrar en Resolvitos?" to "Restaurantes, supermercados, farmacias, repuestos, viajes en Moto Taxi y Taxi Económico, envíos express y directorio de emergencias 24/7.",
                    "¿Cómo puedo ser aliado a Resolvitos?" to "Usa el Suiche en Ajustes para activar tu perfil de Conductor o Administrador de Negocios al instante con tu misma cuenta."
                )

                faqs.forEachIndexed { index, (question, answer) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaq = if (expandedFaq == index) -1 else index }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = question,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                            if (expandedFaq == index) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = answer, fontSize = 12.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }

                // Red Cerrar Sesión Card
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSignOut() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = StatusCoral)
                            }
                            Text(
                                text = "Cerrar Sesión",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = StatusCoral
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = StatusCoral)
                    }
                }

                // Footer with Resolvitos - Encuentra lo que Quieras
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Términos y Privacidad", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text("•", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                        Text("Eliminar mi cuenta", fontSize = 11.sp, color = StatusCoral)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    ResolvitosLogoBadge(boxSize = 40.dp, showTaglineRight = true, darkText = true)
                    Text(
                        text = "HECHO CON ♥ EN VENEZUELA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileQuickCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFBEB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color(0xFFD97706))
            }
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = ResolvitosSlate
            )
        }
    }
}

@Composable
private fun ActivityItemRowVideo1(
    title: String,
    subtitle: String,
    amountUsd: Double,
    bcvRate: Double,
    statusLabel: String,
    isCompleted: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFBEB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛵", fontSize = 18.sp)
                }
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = ResolvitosSlate,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatUsd(amountUsd),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = formatBs(amountUsd, bcvRate),
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
                Surface(
                    color = if (isCompleted) Color(0xFFD1FAE5) else Color(0xFFFFE4E6),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCompleted) Color(0xFF059669) else StatusCoral,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRowVideo1(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = ResolvitosSlate)
                }
                Column {
                    Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ResolvitosSlate)
                    Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ResolvitosSlate,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

// ============================================================================
// GAMIFIED MISSIONS PROGRESS CARD SECTION (CLIENT MAIN HOME SCREEN)
// ============================================================================
@Composable
private fun ClientGamifiedMissionsProgressSection(
    points: Long,
    bcvRate: Double,
    missions: List<BusinessMission>,
    progressList: List<UserMissionProgress>,
    claims: List<UserMissionClaim>,
    onOpenRewardsCatalog: () -> Unit,
    onAdvanceMission: (BusinessMission) -> Unit,
    onClaimMission: (BusinessMission) -> Unit
) {
    val completedCount = missions.count { msn ->
        val prog = progressList.firstOrNull { it.missionId == msn.id }
        val claimed = claims.any { it.missionId == msn.id }
        claimed || prog?.isCompleted == true || points >= msn.targetPoints
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ResolvitosSlate),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_gamified_missions_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Header: Badge + Counter + "Ver todos"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrightYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Retos Activos",
                            tint = ResolvitosSlate,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "RETOS Y MISIONES ACTIVAS",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = BrightYellow
                            )
                            Surface(
                                color = StatusEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, StatusEmerald)
                            ) {
                                Text(
                                    text = "$completedCount/${missions.size} listos",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF6EE7B7),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Completa retos por negocio • Gana puntos y desbloquea premios",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { onOpenRewardsCatalog() }
                        .testTag("open_all_missions_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Catálogo",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = BrightYellow
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = BrightYellow,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Horizontal Scroll of Active Mission Progress Cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(missions, key = { "home_msn_${it.id}" }) { msn ->
                    val progress = progressList.firstOrNull { it.missionId == msn.id }
                    val existingClaim = claims.firstOrNull { it.missionId == msn.id }

                    val curPts = progress?.currentPoints ?: minOf(points, msn.targetPoints)
                    val targetPts = (progress?.targetPoints ?: msn.targetPoints).coerceAtLeast(1L)
                    val curOrders = progress?.currentOrders ?: 0L
                    val targetOrders = (progress?.targetOrders ?: msn.targetOrders).coerceAtLeast(1L)

                    val ptsRatio = (curPts.toFloat() / targetPts.toFloat()).coerceIn(0f, 1f)
                    val ordRatio = (curOrders.toFloat() / targetOrders.toFloat()).coerceIn(0f, 1f)
                    val overallRatio = maxOf(ptsRatio, ordRatio)
                    val overallPercent = (overallRatio * 100).toInt()

                    val isUnlocked = progress?.isCompleted == true || curPts >= targetPts || curOrders >= targetOrders
                    val isClaimed = existingClaim != null || progress?.rewardClaimed == true
                    val couponCode = existingClaim?.couponCode?.ifBlank { progress?.couponCode ?: "" }
                        ?: progress?.couponCode.orEmpty()

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = when {
                                isClaimed -> StatusEmerald
                                isUnlocked -> BrightYellow
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                        modifier = Modifier
                            .width(292.dp)
                            .testTag("mission_progress_card_${msn.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Store Tag + Bonus Points Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ResolvitosSlate,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${msn.badgeEmoji} ${msn.comercioName}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrightYellow,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, StatusEmerald.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "+${msn.pointsReward} PTS BONO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = StatusEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Mission Title & Description
                            Text(
                                text = msn.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = ResolvitosSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = msn.description,
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Progress Metrics Row (Points & Orders)
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⭐ $curPts / $targetPts pts",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = "🛍️ $curOrders / $targetOrders pedidos",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = "$overallPercent%",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) StatusEmerald else ResolvitosSlate
                                        )
                                    }

                                    // High-Contrast Progress Track
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(9.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFFE2E8F0))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(overallRatio.coerceAtLeast(0.06f))
                                                .height(9.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(
                                                    if (isUnlocked) StatusEmerald else ResolvitosSlate
                                                )
                                        )
                                    }
                                }
                            }

                            // Unlockable Reward Banner (Dual Currency USD + Bs. BCV)
                            Surface(
                                color = ResolvitosSlate,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎁 ${msn.rewardTitle}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = BrightYellow,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatDualPrice(msn.rewardValueUsd, bcvRate),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // Action Button: Claimed Coupon vs Claim Reward vs Advance Challenge
                            when {
                                isClaimed -> {
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, StatusEmerald),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "✅ Premio Reclamado • Cupón: ${couponCode.ifBlank { "ACTIVO" }}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = StatusEmerald,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 9.dp, horizontal = 8.dp)
                                        )
                                    }
                                }
                                isUnlocked -> {
                                    Button(
                                        onClick = { onClaimMission(msn) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BrightYellow,
                                            contentColor = ResolvitosSlate
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("claim_mission_btn_${msn.id}")
                                    ) {
                                        Text(
                                            text = "🎁 Reclamar Premio Desbloqueado",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = ResolvitosSlate
                                        )
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = { onAdvanceMission(msn) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ResolvitosSlate,
                                            contentColor = BrightYellow
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("advance_mission_btn_${msn.id}")
                                    ) {
                                        Text(
                                            text = "⚡ Registrar Pedido / +25 Pts al Reto",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = BrightYellow
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// RETOS Y RECOMPENSAS SCREEN (Video 1, 01:01 - Business Missions & Prizes)
// ============================================================================
@Composable
private fun RetosYRecompensasScreenVideo1(
    points: Long,
    missions: List<BusinessMission>,
    progressList: List<UserMissionProgress>,
    claims: List<UserMissionClaim>,
    bcvRate: Double,
    onBack: () -> Unit,
    onAdvanceMission: (BusinessMission) -> Unit,
    onClaimMission: (BusinessMission) -> Unit
) {
    var filterTab by remember { mutableIntStateOf(0) } // 0 = Todos, 1 = Premios Globales, 2 = Comercios Aliados

    val filteredMissions = missions.filter { m ->
        when (filterTab) {
            1 -> m.comercioId == "global"
            2 -> m.comercioId != "global"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Dark Indigo/Slate Gradient Header matching Video 1 (01:01)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onBack() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Retos y Recompensas",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFF59E0B), CircleShape)
                            .background(Color(0xFF312E81)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = BrightYellow,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "MIS PUNTOS ACUMULADOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$points",
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp,
                            color = BrightYellow
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text(
                        text = "Ganas puntos automáticamente por cada compra completada en comercios y cada viaje en Movilidad. ¡Canje directo garantizado!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Catálogo de Canje Directo",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Reclama premios tangibles y cupones de cada negocio al cumplir sus misiones",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val tabs = listOf(
                        0 to "Todos",
                        1 to "🌐 Premios Globales",
                        2 to "🏪 Comercios Aliados"
                    )
                    tabs.forEach { (idx, title) ->
                        val active = filterTab == idx
                        Surface(
                            color = if (active) BrightYellow else Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, if (active) BrightYellow else Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable { filterTab = idx }
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        items(filteredMissions, key = { it.id }) { msn ->
            val progress = progressList.firstOrNull { it.missionId == msn.id }
            val alreadyClaimed = claims.any { it.missionId == msn.id } || progress?.rewardClaimed == true
            val curPts = progress?.currentPoints ?: minOf(points, msn.targetPoints)
            val targetPts = (progress?.targetPoints ?: msn.targetPoints).coerceAtLeast(1L)
            val curOrders = progress?.currentOrders ?: 0L
            val targetOrders = (progress?.targetOrders ?: msn.targetOrders).coerceAtLeast(1L)
            val unlocked = progress?.isCompleted == true || curPts >= targetPts || curOrders >= targetOrders
            val overallRatio = maxOf(
                (curPts.toFloat() / targetPts.toFloat()).coerceIn(0f, 1f),
                (curOrders.toFloat() / targetOrders.toFloat()).coerceIn(0f, 1f)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ResolvitosSlate,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${msn.badgeEmoji} ${msn.comercioName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Meta: $targetPts pts o $targetOrders pedidos • +${msn.pointsReward} pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = StatusEmerald
                        )
                    }

                    Text(
                        text = msn.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = msn.description,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    // Live Progress Bar from user_mission_progress
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso: $curPts/$targetPts pts • $curOrders/$targetOrders pedidos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = "${(overallRatio * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (unlocked) StatusEmerald else ResolvitosSlate
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(overallRatio.coerceAtLeast(0.06f))
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (unlocked) StatusEmerald else ResolvitosSlate)
                            )
                        }
                    }

                    Text(
                        text = "🎁 Premio: ${msn.rewardTitle} (${formatDualPrice(msn.rewardValueUsd, bcvRate)})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = ResolvitosSlate
                    )

                    if (alreadyClaimed) {
                        val coupon = claims.firstOrNull { it.missionId == msn.id }?.couponCode
                            ?: progress?.couponCode.orEmpty()
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Premio Desbloqueado • Cupón: ${coupon.ifBlank { "ACTIVO" }}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = StatusEmerald,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else if (unlocked) {
                        Button(
                            onClick = { onClaimMission(msn) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrightYellow,
                                contentColor = ResolvitosSlate
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎁 Reclamar Premio Desbloqueado",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onAdvanceMission(msn) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ResolvitosSlate,
                                    contentColor = BrightYellow
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "⚡ Avanzar Reto (+25 pts)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = BrightYellow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
