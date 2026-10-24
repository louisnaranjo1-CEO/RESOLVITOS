package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BusinessMission
import com.example.data.model.Comercio
import com.example.data.model.DriverProfile
import com.example.data.model.MapPointPreset
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.TransportRequest
import com.example.data.model.calculateDriverFareUsd
import com.example.data.model.formatBs
import com.example.data.model.formatDualPrice
import com.example.data.model.formatUsd
import com.example.ui.components.BrightYellow
import com.example.ui.components.MapProviderType
import com.example.ui.components.ModularInteractiveMapView
import com.example.ui.components.ResolvitosLogoBadge
import com.example.ui.components.SettingsRoleSwitcherCard
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCoral
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ResolvitosViewModel
import com.example.util.ResolvitosSoundEvent
import com.example.util.SoundAndHapticManager
import java.util.Locale

// ============================================================================
// VIDEO 2: INTERFAZ DEL CONDUCTOR ("Resolvitos Centro de comandas")
// ============================================================================
enum class DriverBottomTab(val label: String) {
    RADAR("Radar"),
    GANANCIAS("Ganancias"),
    LOGROS("Logros"),
    PERFIL("Perfil")
}

@Composable
fun DriverModeScreen(
    viewModel: ResolvitosViewModel,
    onRequestRoleSwitch: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val liveBcv by viewModel.liveBcvRate.collectAsStateWithLifecycle()
    val drivers by viewModel.drivers.collectAsStateWithLifecycle()
    val requests by viewModel.transportRequests.collectAsStateWithLifecycle()

    val bcvRate = liveBcv ?: settings.bcvRate
    val myDriver = drivers.firstOrNull { it.driverId == viewModel.currentUserId }
        ?: drivers.firstOrNull()
        ?: DriverProfile(
            id = viewModel.currentUserId,
            driverId = viewModel.currentUserId,
            name = profile?.displayName ?: "Simón Bolívar",
            vehicleType = "moto",
            baseFareUsd = 1.50,
            baseIncludedKm = 6.0,
            ratePerKmUsd = 0.13
        )

    var activeTab by remember { mutableStateOf(DriverBottomTab.RADAR) }
    var isOnline by remember(myDriver.isOnline) { mutableStateOf(myDriver.isOnline) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Video 2 Top Bar: [Resolvitos Badge] Centro de comandas / Llega en un 2x3 a tu destino. | [● ACTIVO]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1520))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResolvitosLogoBadge(boxSize = 40.dp, showTaglineRight = false)
                Column {
                    Text(
                        text = "Centro de\ncomandas",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Llega en un 2x3 a tu destino.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                color = if (isOnline) Color(0xFF10B981) else Color(0xFF475569),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .clickable {
                        isOnline = !isOnline
                        viewModel.saveDriverConfiguration(
                            name = myDriver.name,
                            vehicleType = myDriver.vehicleType,
                            vehicleModel = myDriver.vehicleModel,
                            vehiclePlate = myDriver.vehiclePlate,
                            isOnline = isOnline,
                            baseFareUsd = myDriver.baseFareUsd,
                            ratePerKmUsd = myDriver.ratePerKmUsd,
                            minimumFareUsd = myDriver.minimumFareUsd,
                            deliveryFlatFeeUsd = myDriver.deliveryFlatFeeUsd,
                            hasThermalBag = myDriver.hasThermalBag,
                            pagoMovilInfo = myDriver.pagoMovilInfo,
                            baseIncludedKm = myDriver.baseIncludedKm
                        )
                    }
                    .testTag("driver_online_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Text(
                        text = if (isOnline) "ACTIVO" else "DESCONECTADO",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Main Content
        val mapProvider by viewModel.mapProvider.collectAsStateWithLifecycle()
        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                DriverBottomTab.RADAR -> {
                    DriverRadarTabVideo2(
                        driver = myDriver,
                        isOnline = isOnline,
                        requests = requests,
                        bcvRate = bcvRate,
                        mapProvider = mapProvider,
                        onSwitchMapProvider = { next -> viewModel.setMapProvider(next) },
                        onSendOffer = { req, priceUsd ->
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.CYCLE_DRIVER)
                            viewModel.submitDriverBid(req, priceUsd, 4L)
                        },
                        onAdvanceStatus = { req, next ->
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.CYCLE_TRANSIT)
                            viewModel.advanceTransportStatus(req, next)
                        },
                        onConfirmPaymentReceived = { reqId ->
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                            viewModel.confirmRidePaymentReceived(reqId)
                        },
                        onOpenChat = { req ->
                            viewModel.openChat(req.id, req.clientName)
                        }
                    )
                }
                DriverBottomTab.GANANCIAS -> {
                    DriverGananciasYMisTarifasTabVideo2(
                        driver = myDriver,
                        bcvRate = bcvRate,
                        onSaveTarifas = { vType, baseFare, includedKm, extraKmRate ->
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                            viewModel.saveDriverConfiguration(
                                name = myDriver.name,
                                vehicleType = vType,
                                vehicleModel = myDriver.vehicleModel,
                                vehiclePlate = myDriver.vehiclePlate,
                                isOnline = isOnline,
                                baseFareUsd = baseFare,
                                ratePerKmUsd = extraKmRate,
                                minimumFareUsd = baseFare,
                                deliveryFlatFeeUsd = myDriver.deliveryFlatFeeUsd,
                                hasThermalBag = myDriver.hasThermalBag,
                                pagoMovilInfo = myDriver.pagoMovilInfo,
                                baseIncludedKm = includedKm
                            )
                        }
                    )
                }
                DriverBottomTab.LOGROS -> {
                    DriverLogrosTabVideo2(
                        onBackToRadar = { activeTab = DriverBottomTab.RADAR }
                    )
                }
                DriverBottomTab.PERFIL -> {
                    DriverPerfilAndRegistrationTabVideo2(
                        driver = myDriver,
                        activeRole = profile?.activeRole ?: "driver",
                        onSaveDriverProfile = { name, vType, model, plate ->
                            viewModel.saveDriverConfiguration(
                                name = name,
                                vehicleType = vType,
                                vehicleModel = model,
                                vehiclePlate = plate,
                                isOnline = isOnline,
                                baseFareUsd = myDriver.baseFareUsd,
                                ratePerKmUsd = myDriver.ratePerKmUsd,
                                minimumFareUsd = myDriver.minimumFareUsd,
                                deliveryFlatFeeUsd = myDriver.deliveryFlatFeeUsd,
                                hasThermalBag = myDriver.hasThermalBag,
                                pagoMovilInfo = myDriver.pagoMovilInfo,
                                baseIncludedKm = myDriver.baseIncludedKm
                            )
                        },
                        onSelectRole = onRequestRoleSwitch
                    )
                }
            }
        }

        // Video 2 Bottom Navigation Bar: Radar | Ganancias | Logros | Perfil
        Surface(
            color = if (activeTab == DriverBottomTab.LOGROS) Color(0xFF0B1520) else Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Triple(DriverBottomTab.RADAR, "Radar", Icons.Default.Explore),
                    Triple(DriverBottomTab.GANANCIAS, "Ganancias", Icons.Default.AttachMoney),
                    Triple(DriverBottomTab.LOGROS, "Logros", Icons.Default.EmojiEvents),
                    Triple(DriverBottomTab.PERFIL, "Perfil", Icons.Default.Person)
                )
                tabs.forEach { (tab, label, icon) ->
                    val selected = activeTab == tab
                    val activeColor = if (activeTab == DriverBottomTab.LOGROS) BrightYellow else ResolvitosSlate
                    val inactiveColor = Color(0xFF94A3B8)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { activeTab = tab }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("driver_tab_${tab.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) activeColor else inactiveColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selected) activeColor else inactiveColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverRadarTabVideo2(
    driver: DriverProfile,
    isOnline: Boolean,
    requests: List<TransportRequest>,
    bcvRate: Double,
    mapProvider: MapProviderType,
    onSwitchMapProvider: (MapProviderType) -> Unit,
    onSendOffer: (TransportRequest, Double) -> Unit,
    onAdvanceStatus: (TransportRequest, String) -> Unit,
    onConfirmPaymentReceived: (String) -> Unit,
    onOpenChat: (TransportRequest) -> Unit
) {
    var radarFilter by remember { mutableIntStateOf(0) } // 0 = TODOS, 1 = TAXIS, 2 = DELIVERIES, 3 = MANDADOS
    var selectedIncomingOfferModal by remember { mutableStateOf<TransportRequest?>(null) }
    var showActiveServiceFullScreen by remember { mutableStateOf(true) }

    val activeServiceRequest = requests.firstOrNull {
        it.status in listOf("accepted", "arrived", "in_transit")
    }
    val pendingConciliationRequests = requests.filter {
        it.status == "completed" && it.paymentStatus != "confirmed"
    }

    val filteredRequests = requests.filter { req ->
        req.status != "cancelled" && when (radarFilter) {
            1 -> req.serviceCategory == "transport" || req.serviceCategory == "fast_1touch"
            2 -> req.serviceCategory == "delivery"
            3 -> req.serviceCategory == "mandado" || req.serviceCategory == "encomienda"
            else -> true
        }
    }

    // If there is an active ride and driver is viewing active service screen (Videos 4 & 8)
    if (activeServiceRequest != null && showActiveServiceFullScreen) {
        DriverActiveServiceMapScreen(
            request = activeServiceRequest,
            bcvRate = bcvRate,
            mapProvider = mapProvider,
            onSwitchMapProvider = onSwitchMapProvider,
            onMinimizeToRadar = { showActiveServiceFullScreen = false },
            onAdvanceStatus = { nextStatus ->
                onAdvanceStatus(activeServiceRequest, nextStatus)
            },
            onOpenChat = { onOpenChat(activeServiceRequest) }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Top Yellow Bar if there is an active ride minimized ("● SERVICIO EN CURSO ACTIVO [VER SERVICIO]")
        if (activeServiceRequest != null) {
            item {
                Surface(
                    color = BrightYellow,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showActiveServiceFullScreen = true }
                        .testTag("driver_resume_active_service_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "● SERVICIO EN CURSO ACTIVO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = "${activeServiceRequest.clientName} → ${activeServiceRequest.destinationAddress.take(28)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ResolvitosSlate
                            )
                        }
                        Surface(color = ResolvitosSlate, shape = RoundedCornerShape(50)) {
                            Text(
                                text = "VER SERVICIO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 1. Dark Radar Status Card (Video 2 & Video 8: "RADAR ACTIVO • ESCANEO 5 KM — Buscando Clientes ●")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071A24)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(StatusEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) StatusEmerald else Color.Gray)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isOnline) "En línea y disponible" else "Fuera de línea",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Surface(
                                    color = Color(0xFF78350F),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (driver.vehicleType == "moto") "MOTO" else "TAXI ECON.",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrightYellow,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "RADAR ACTIVO • ESCANEO 5 KM • Buscando Clientes ●",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusEmerald
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF0F2937),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, StatusEmerald.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🕒 Capacidad: ${if (activeServiceRequest != null) "1/2 (En servicio)" else "0/2 (Libre)"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFF0F2937),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, BrightYellow.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                val next = if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) {
                                    MapProviderType.MAPBOX_PREVIEW
                                } else {
                                    MapProviderType.GOOGLE_MAPS_PREVIEW
                                }
                                onSwitchMapProvider(next)
                            }
                        ) {
                            Text(
                                text = "🗺️ ${if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) "Google Maps" else "Mapbox"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 CONSEJO PARA PILOTOS: Revisa siempre el capture y los últimos dígitos de referencia antes de conciliar el pago.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // 2. Filter Pills Row: TODOS | TAXIS | DELIVERIES | MANDADOS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pills = listOf(
                    0 to "TODOS  ${requests.count { it.status != "cancelled" }}",
                    1 to "TAXIS  ${requests.count { it.serviceCategory == "transport" || it.serviceCategory == "fast_1touch" }}",
                    2 to "DELIVERIES  ${requests.count { it.serviceCategory == "delivery" }}",
                    3 to "MANDADOS  ${requests.count { it.serviceCategory == "mandado" || it.serviceCategory == "encomienda" }}"
                )
                pills.forEach { (idx, label) ->
                    val active = radarFilter == idx
                    Surface(
                        color = if (active) ResolvitosSlate else Color.White,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, if (active) ResolvitosSlate else Color(0xFFE2E8F0)),
                        modifier = Modifier.clickable { radarFilter = idx }
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (active) BrightYellow else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 3. Active Ride/Delivery Requests on Radar
        items(filteredRequests, key = { it.id }) { req ->
            val myAutoFare = calculateDriverFareUsd(driver, req.distanceKm)
            val appFeeUsd = 0.25
            val netEarningsUsd = maxOf(0.50, req.priceUsd - appFeeUsd)

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(
                    width = if (req.status == "searching") 2.dp else 1.dp,
                    color = if (req.status == "searching") ResolvitosSlate else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = ResolvitosSlate,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = when (req.serviceCategory) {
                                        "encomienda" -> "📦 ENCOMIENDA"
                                        "mandado" -> "🛍️ MANDADO"
                                        "fast_1touch" -> "⚡ 1 TOQUE"
                                        else -> if (req.vehicleType == "moto") "🛵 TAXI MOTO" else "🚕 TAXI ECON."
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "${req.clientName} • ${String.format(Locale.US, "%.1f", req.distanceKm)} km",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = ResolvitosSlate
                            )
                        }

                        Surface(
                            color = ResolvitosSlate,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = req.status.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("🟢 PUNTO A: ${req.originAddress}", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("🟡 DESTINO B: ${req.destinationAddress}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)

                    if (req.packageDescription.isNotBlank()) {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "📋 Detalle: ${req.packageDescription}",
                                fontSize = 11.sp,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Earnings Breakdown Strip matching Video 4 ("GANANCIA BRUTA / Tarifa Resolvitos -$0.25 / Neto Libre")
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GANANCIA BRUTA", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
                                Text(
                                    text = formatDualPrice(req.priceUsd, bcvRate),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("NETO LIBRE (-$0.25 App)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = StatusEmerald)
                                Text(
                                    text = formatDualPrice(netEarningsUsd, bcvRate),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = StatusEmerald
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (req.status == "searching") {
                            Button(
                                onClick = { selectedIncomingOfferModal = req },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusEmerald,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("driver_open_offer_modal_${req.id}")
                            ) {
                                Text("✨ ACEPTAR VIAJE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onSendOffer(req, myAutoFare) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrightYellow,
                                    contentColor = ResolvitosSlate
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ofertar ${formatUsd(myAutoFare)}", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        } else if (req.status in listOf("accepted", "arrived", "in_transit")) {
                            Button(
                                onClick = { showActiveServiceFullScreen = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ResolvitosSlate,
                                    contentColor = BrightYellow
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🗺️ Abrir Mapa de Servicio en Curso", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = { onOpenChat(req) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 4. Post-Trip Payment Conciliation Card ("PAGOS POR CONCILIAR — Revisa el capture antes de confirmar", Videos 4 & 8)
        item {
            if (pendingConciliationRequests.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1520)),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.5.dp, BrightYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PAGOS POR CONCILIAR (${pendingConciliationRequests.size})",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = BrightYellow
                                )
                                Text(
                                    text = "Revisa el capture antes de confirmar",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        pendingConciliationRequests.forEach { pendingReq ->
                            val isReported = pendingReq.paymentStatus == "reported"
                            Surface(
                                color = Color(0xFF162032),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isReported) StatusEmerald else Color(0xFFF59E0B)
                                ),
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
                                        Column {
                                            Text(
                                                text = pendingReq.clientName,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = formatDualPrice(pendingReq.priceUsd, bcvRate),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = BrightYellow
                                            )
                                        }
                                        Surface(
                                            color = if (isReported) StatusEmerald else Color(0xFF78350F),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = if (isReported) "COMPROBANTE ENVIADO" else "PENDIENTE POR TRANSFERIR",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isReported) Color.White else BrightYellow,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "REFERENCIA: #${pendingReq.paymentReference.ifBlank { "123456 (Pago Móvil Banesco)" }}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCBD5E1)
                                    )

                                    if (pendingReq.paymentCaptureUri.isNotBlank() || isReported) {
                                        Surface(
                                            color = Color(0xFF0F172A),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.Image, contentDescription = null, tint = BrightYellow, modifier = Modifier.size(18.dp))
                                                Text(
                                                    text = "Capture Bancario Adjunto Verificado (${pendingReq.paymentCaptureUri.takeLast(24).ifBlank { "comprobante_bcv.png" }})",
                                                    fontSize = 11.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { onConfirmPaymentReceived(pendingReq.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = StatusEmerald,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("driver_confirm_payment_button")
                                    ) {
                                        Text(
                                            text = "✓ CONFIRMAR Y CONCILIAR PAGO RECIBIDO",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Default "Al día" card when all trips are conciliated
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1520)),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(StatusEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusEmerald,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = "¡Al día! No tienes pagos\npendientes por conciliar",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Todas tus carreras anteriores han sido verificadas y cobradas satisfactoriamente. Estás listo para seguir rodando.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 5. Cream Card: "★★★★★ FEEDBACK RECIENTE • ¡Buen trabajo, Piloto!" (Video 2, 00:02)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("★★★★★", color = Color(0xFFF59E0B), fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(
                            text = "FEEDBACK RECIENTE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                    Text(
                        text = "¡Buen trabajo, Piloto!",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "\"Excelente servicio, llegó rápido\"",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        }

        // 6. Dark Card: "GARANTÍA RESOLVITOS • Protección 48h" (Video 2, 00:03)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071A24)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StatusEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = StatusEmerald)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "GARANTÍA RESOLVITOS",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = StatusEmerald
                            )
                            Surface(color = StatusEmerald, shape = RoundedCornerShape(50)) {
                                Text(
                                    text = "Protección 48h",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tus ganancias están respaldadas. Cuentas con un plazo de protección de 48 horas para cualquier reclamo o comprobante dudoso.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }

    // Incoming Ride Offer Modal matching Video 4 (00:36 - 00:46: "🚕 TAXI MOTO • 🕒 19s • ✨ ACEPTAR VIAJE")
    if (selectedIncomingOfferModal != null) {
        val offerReq = selectedIncomingOfferModal!!
        val appFee = 0.25
        val netUsd = maxOf(0.50, offerReq.priceUsd - appFee)

        Dialog(onDismissRequest = { selectedIncomingOfferModal = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1520)),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(2.dp, BrightYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = BrightYellow, shape = RoundedCornerShape(50)) {
                            Text(
                                text = if (offerReq.vehicleType == "moto") "🚕 TAXI MOTO" else "🚕 TAXI ECONÓMICO",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(50)) {
                            Text(
                                text = "🕒 19s",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Gross / BCV / Net Earnings Card
                    Surface(
                        color = Color(0xFF162032),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("GANANCIA BRUTA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8))
                                Text("TASA BCV: ${String.format(Locale.US, "%.2f", bcvRate)} Bs/$", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrightYellow)
                            }
                            Text(
                                text = "${formatUsd(offerReq.priceUsd)}  (${formatBs(offerReq.priceUsd, bcvRate)})",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tarifa Resolvitos (App)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text("-$0.25", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusCoral)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Neto Libre para ti", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StatusEmerald)
                                Text(
                                    text = "${formatUsd(netUsd)} (${formatBs(netUsd, bcvRate)})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StatusEmerald
                                )
                            }
                        }
                    }

                    // Route A -> B
                    Surface(
                        color = Color(0xFF162032),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🟢 A PUNTO DE RECOGIDA", fontSize = 9.sp, fontWeight = FontWeight.Black, color = StatusEmerald)
                            Text(offerReq.originAddress, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("🟡 B DESTINO (${String.format(Locale.US, "%.1f", offerReq.distanceKm)} km)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = BrightYellow)
                            Text(offerReq.destinationAddress, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Button(
                        onClick = {
                            selectedIncomingOfferModal = null
                            showActiveServiceFullScreen = true
                            onAdvanceStatus(offerReq, "accepted")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusEmerald,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("driver_confirm_accept_ride_button")
                    ) {
                        Text("✨ ACEPTAR VIAJE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    OutlinedButton(
                        onClick = { selectedIncomingOfferModal = null },
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("RECHAZAR / OMITIR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Driver Active Service Map Screen ("ESTADO DEL SERVICIO: EN CAMINO A RECOGER / ESPERANDO AL PASAJERO / EN VIAJE AL DESTINO")
 * matching Videos 4, 5 & 8.
 */
@Composable
private fun DriverActiveServiceMapScreen(
    request: TransportRequest,
    bcvRate: Double,
    mapProvider: MapProviderType,
    onSwitchMapProvider: (MapProviderType) -> Unit,
    onMinimizeToRadar: () -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onOpenChat: () -> Unit
) {
    val originPreset = MapPointPreset(
        name = request.originAddress.substringBefore(" —").ifBlank { "Punto A" },
        address = request.originAddress,
        lat = request.originLat,
        lng = request.originLng
    )
    val destPreset = MapPointPreset(
        name = request.destinationAddress.substringBefore(" —").ifBlank { "Destino B" },
        address = request.destinationAddress,
        lat = request.destinationLat,
        lng = request.destinationLng
    )

    val driverCoords = when (request.status) {
        "accepted" -> Pair(request.originLat + 0.0025, request.originLng - 0.0025)
        "arrived" -> Pair(request.originLat, request.originLng)
        else -> Pair(
            (request.originLat + request.destinationLat) / 2.0,
            (request.originLng + request.destinationLng) / 2.0
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Top Yellow Bar ("● SERVICIO EN CURSO ACTIVO")
        item {
            Surface(
                color = BrightYellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "● SERVICIO EN CURSO ACTIVO",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = ResolvitosSlate
                    )
                    Surface(
                        color = ResolvitosSlate,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable { onMinimizeToRadar() }
                    ) {
                        Text(
                            text = "VOLVER AL RADAR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = BrightYellow,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // 2. Integrated Map View with Route A -> B
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ModularInteractiveMapView(
                    origin = originPreset,
                    destination = destPreset,
                    distanceKm = request.distanceKm,
                    etaMinutes = maxOf(3, (request.distanceKm * 2.0).toInt()),
                    provider = mapProvider,
                    driverLocation = driverCoords,
                    driverLabel = "Tú (Piloto)",
                    showCameraPills = true,
                    onTapMapCoordinate = { _, _ -> },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Surface(
                    color = ResolvitosSlate.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, BrightYellow),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clickable {
                            val next = if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) {
                                MapProviderType.MAPBOX_PREVIEW
                            } else {
                                MapProviderType.GOOGLE_MAPS_PREVIEW
                            }
                            onSwitchMapProvider(next)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, tint = BrightYellow, modifier = Modifier.size(14.dp))
                        Text(
                            text = mapProvider.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 3. Service Status + Passenger Card + Step Button
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = ResolvitosSlate,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ESTADO DEL SERVICIO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = when (request.status) {
                                    "accepted" -> "🛵 EN CAMINO A RECOGER AL CLIENTE"
                                    "arrived" -> "📍 ESPERANDO AL PASAJERO EN PUNTO A"
                                    else -> "🚀 EN VIAJE AL DESTINO B"
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = BrightYellow
                            )
                        }
                    }

                    // Passenger Row + Yellow "💬 Chat" Button
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(ResolvitosSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = request.clientName.take(1).ifBlank { "L" },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = BrightYellow
                                )
                            }
                            Column {
                                Text(
                                    text = request.clientName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = "PASAJERO VERIFICADO • ${request.paymentMethod}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusEmerald
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onOpenChat,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrightYellow,
                                    contentColor = ResolvitosSlate
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("driver_active_service_chat_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }

                            IconButton(
                                onClick = onOpenChat,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StatusEmerald)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Llamar", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // If Encomienda, show Ficha de Encomienda / Paquete (Video 5)
                    if (request.serviceCategory == "encomienda") {
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("📦 Ficha de Encomienda / Paquete", fontWeight = FontWeight.Black, fontSize = 12.sp, color = ResolvitosSlate)
                                Text("DESTINATARIO: ${request.receiverInfo.ifBlank { "María Pérez (+58 4121234567)" }}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)
                                Text("DETALLE: ${request.packageDescription.ifBlank { "Sobre sellado con documentos" }}", fontSize = 11.sp, color = Color(0xFF475569))
                            }
                        }
                    }

                    // Dark Tariff Banner
                    Surface(
                        color = Color(0xFF0B1520),
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
                            Column {
                                Text("TARIFA DEL VIAJE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8))
                                Text(request.paymentMethod, fontSize = 11.sp, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatUsd(request.priceUsd),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = BrightYellow
                                )
                                Text(
                                    text = formatBs(request.priceUsd, bcvRate),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Primary Progression Button matching Video 4:
                    // "accepted" -> "Llegué al punto de recogida"
                    // "arrived"  -> "Iniciar Viaje con Pasajero"
                    // "in_transit" -> "Finalizar Viaje"
                    val (nextStatus, buttonLabel, buttonColor, textColor) = when (request.status) {
                        "accepted" -> listOf("arrived", "📍 Llegué al punto de recogida", BrightYellow, ResolvitosSlate)
                        "arrived" -> listOf("in_transit", "🚀 Iniciar Viaje con Pasajero", StatusEmerald, Color.White)
                        else -> listOf("completed", "🏁 Finalizar Viaje", StatusEmerald, Color.White)
                    }

                    Button(
                        onClick = { onAdvanceStatus(nextStatus as String) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor as Color,
                            contentColor = textColor as Color
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("driver_step_action_button")
                    ) {
                        Text(
                            text = buttonLabel as String,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// VIDEO 2 TAB 2: GANANCIAS ("Mis Ganancias y Tarifas" - 00:06 to 00:21)
// ============================================================================
@Composable
private fun DriverGananciasYMisTarifasTabVideo2(
    driver: DriverProfile,
    bcvRate: Double,
    onSaveTarifas: (String, Double, Double, Double) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(2) } // 0 = MIS VIAJES, 1 = AUDITORÍA PAGOS, 2 = MIS TARIFAS
    var isDayShift by remember { mutableStateOf(true) }
    var vehicleType by remember(driver.vehicleType) { mutableStateOf(driver.vehicleType) }
    var baseFareInput by remember(driver.baseFareUsd) { mutableStateOf(driver.baseFareUsd.toString()) }
    var baseIncludedKm by remember(driver.baseIncludedKm) { mutableDoubleStateOf(driver.baseIncludedKm.coerceIn(1.0, 6.0)) }
    var extraKmRateInput by remember(driver.ratePerKmUsd) { mutableStateOf(driver.ratePerKmUsd.toString()) }
    var showFareSimulator by remember { mutableStateOf(false) }
    var simulatorKm by remember { mutableDoubleStateOf(4.5) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Mis Ganancias y Tarifas",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Control de ingresos, comisiones Resolvitos y precios de tus servicios",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Green Gradient Card: GANANCIAS DE HOY ($1.50 USD ≈ Bs Tasa oficial BCV)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📈 GANANCIAS DE HOY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "1 SERVICIO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$1.50",
                            fontWeight = FontWeight.Black,
                            fontSize = 36.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "USD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text(
                        text = "≈ ${formatBs(1.50, bcvRate)} (Tasa oficial BCV)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }
        }

        // SEMANA Card ($7.00)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SEMANA • Últimos 7 días",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "$7.00",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = ResolvitosSlate
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFBEB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFFD97706))
                    }
                }
            }
        }

        // COMISIÓN ADEUDADA Card ($0.75 USD)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ COMISIÓN ADEUDADA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ResolvitosSlate
                        )
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "PENDIENTE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatUsd(driver.commissionDebtUsd),
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = ResolvitosSlate
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "USD  ≈ ${formatBs(driver.commissionDebtUsd, bcvRate)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD97706),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF59E0B),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "PAGAR COMISIONES PARA\nMANTENERTE ACTIVO",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "Paga diario al finalizar jornada o los viernes. Evita suspensión por mora.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Segmented Sub-tabs: MIS VIAJES (15) | AUDITORÍA PAGOS (4) | MIS TARIFAS
        item {
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val subTabs = listOf(
                        0 to "MIS VIAJES\n(15)",
                        1 to "AUDITORÍA\nPAGOS (4)",
                        2 to "MIS TARIFAS"
                    )
                    subTabs.forEach { (idx, label) ->
                        val active = selectedSubTab == idx
                        Surface(
                            color = if (active) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
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
                                    text = label,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp,
                                    color = if (active) ResolvitosSlate else Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedSubTab == 0) {
            // MIS VIAJES (Video 2, 00:07 - 00:08)
            val sampleTrips = listOf(
                Triple("8.91819, -67.43109", "1m 15s • 4 ★", 1.50),
                Triple("Parque Rómulo...", "0m 50s • 3 ★", 1.50),
                Triple("8.93283, -67.41438", "0m 42s • 5 ★", 2.00),
                Triple("8.92676, -67.42637", "51m 48s • 4 ★", 2.00)
            )
            items(sampleTrips) { (coord, meta, price) ->
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
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("↗", fontWeight = FontWeight.Black, color = Color(0xFF2563EB), fontSize = 18.sp)
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Carrera", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ResolvitosSlate)
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "Comisión: $0.25",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(coord, fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(meta, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${formatUsd(price)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = "COBRADO AL CLIENTE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusEmerald
                            )
                        }
                    }
                }
            }
        } else if (selectedSubTab == 1) {
            // AUDITORÍA PAGOS
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Historial de Conciliaciones", fontWeight = FontWeight.ExtraBold, color = ResolvitosSlate)
                        Text("✓ Pago Móvil #849201 • $1.25 USD Conciliado", fontSize = 12.sp, color = StatusEmerald)
                        Text("✓ Pago Móvil #772110 • $1.50 USD Conciliado", fontSize = 12.sp, color = StatusEmerald)
                    }
                }
            }
        } else {
            // MIS TARIFAS (Exact replica of Video 2, 00:09 - 00:18)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Consejos para Piloto Mototaxi & Repartidor",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "• Casco obligatorio: Lleva siempre un segundo casco limpio para tu pasajero en servicios de movilidad.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "• Bolso Térmico: Los restaurantes asignan pedidos preferentemente a pilotos con bolso térmico registrado (🎒 Bolso Térmico).",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "• Clima y Lluvia: En días lluviosos aumenta la precaución y mantén distancia de frenado.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TARIFA BASE + KM\nEXCEDENTE",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = ResolvitosSlate
                            )
                            Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(10.dp)) {
                                Text(
                                    text = "Cobertura 1\na 6 km",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StatusEmerald,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Define cuántos kilómetros incluye tu tarifa base de arranque (entre 1 y 6 km) y cuánto cobrarás por cada kilómetro adicional que sobrepase esa distancia.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        // Vehicle Category Selector (Moto Taxi vs Taxi Económico)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isMoto = vehicleType == "moto"
                            Surface(
                                color = if (isMoto) BrightYellow else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isMoto) ResolvitosSlate else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { vehicleType = "moto" }
                            ) {
                                Text(
                                    text = "🛵 Moto Taxi + Delivery",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = ResolvitosSlate,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                            Surface(
                                color = if (!isMoto) BrightYellow else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (!isMoto) ResolvitosSlate else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { vehicleType = "taxi_economico" }
                            ) {
                                Text(
                                    text = "🚕 Taxi Económico",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = ResolvitosSlate,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { showFareSimulator = !showFareSimulator },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SIMULADOR / PROBAR\nTARIFAS EN MAPA REAL",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (showFareSimulator) {
                            val parsedBase = baseFareInput.toDoubleOrNull() ?: 1.50
                            val parsedExtra = extraKmRateInput.toDoubleOrNull() ?: 0.13
                            val extraKm = maxOf(0.0, simulatorKm - baseIncludedKm)
                            val simulatedTotal = parsedBase + extraKm * parsedExtra
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Simulación de Viaje: ${String.format(Locale.US, "%.1f", simulatorKm)} km",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = ResolvitosSlate
                                    )
                                    Slider(
                                        value = simulatorKm.toFloat(),
                                        onValueChange = { simulatorKm = it.toDouble() },
                                        valueRange = 1f..12f
                                    )
                                    Text(
                                        text = "Cobrarás al cliente: ${formatDualPrice(simulatedTotal, bcvRate)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = StatusEmerald
                                    )
                                }
                            }
                        }

                        Text(
                            text = "HORARIO DE CONFIGURACIÓN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF94A3B8)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = if (isDayShift) Color(0xFFF59E0B) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isDayShift = true }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "☀️ Turno\nDiurno",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (isDayShift) Color.White else Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Surface(
                                color = if (!isDayShift) ResolvitosSlate else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isDayShift = false }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🌙 Turno\nNocturno",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (!isDayShift) BrightYellow else Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isDayShift) "Tarifa Base Diurna ($ USD)" else "Tarifa Base Nocturna ($ USD)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = ResolvitosSlate
                        )

                        OutlinedTextField(
                            value = baseFareInput,
                            onValueChange = { baseFareInput = it },
                            prefix = { Text("$ ", fontWeight = FontWeight.Black) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_base_fare_input")
                        )

                        Text(
                            text = "* Tarifa mínima obligatoria por normativa de la plataforma: $0.50 USD.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )

                        // Distancia Base Incluida (1 km .. 6 km) + Yellow Slider (Video 2, 00:13)
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                        text = "Distancia Base Incluida\nDiurna",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "${baseIncludedKm.toInt()} km",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color(0xFFD97706)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    (1..6).forEach { km ->
                                        val selected = baseIncludedKm.toInt() == km
                                        Surface(
                                            color = if (selected) Color(0xFFF59E0B) else Color.White,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, if (selected) Color(0xFFF59E0B) else Color(0xFFE2E8F0)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { baseIncludedKm = km.toDouble() }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$km km",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    color = if (selected) Color.White else ResolvitosSlate
                                                )
                                            }
                                        }
                                    }
                                }

                                Slider(
                                    value = baseIncludedKm.toFloat(),
                                    onValueChange = { baseIncludedKm = it.toInt().toDouble() },
                                    valueRange = 1f..6f,
                                    steps = 4,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFFF59E0B),
                                        activeTrackColor = Color(0xFFF59E0B)
                                    )
                                )

                                Text(
                                    text = "Tu tarifa base cubrirá hasta ${baseIncludedKm.toInt()} km. En viajes de ${baseIncludedKm.toInt()} km o menos, el cliente solo paga la tarifa base ($$baseFareInput).",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Text(
                            text = "Precio por km Excedente Diurno ($ USD / km)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = ResolvitosSlate
                        )

                        OutlinedTextField(
                            value = extraKmRateInput,
                            onValueChange = { extraKmRateInput = it },
                            prefix = { Text("$ ", fontWeight = FontWeight.Black) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_per_km_input")
                        )

                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Rango sugerido por la plataforma: $0.13 - $0.22 / km",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                onSaveTarifas(
                                    vehicleType,
                                    baseFareInput.toDoubleOrNull() ?: 1.50,
                                    baseIncludedKm,
                                    extraKmRateInput.toDoubleOrNull() ?: 0.13
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ResolvitosSlate,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_driver_rates_button")
                        ) {
                            Text("GUARDAR MIS TARIFAS", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// VIDEO 2 TAB 3: LOGROS ("Metas e Incentivos para Conductores" - 00:22 to 00:25)
// ============================================================================
@Composable
private fun DriverLogrosTabVideo2(
    onBackToRadar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07131D))
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "←  Volver al Radar",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.clickable { onBackToRadar() }
        )

        Text(
            text = "Metas e Incentivos\npara Conductores",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            color = Color.White
        )

        Text(
            text = "Monitorea tu calificación oficial, opiniones de clientes y desbloquea bonos por mérito y rendimiento.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        // 3 Metric Cards: VIAJES (15) | REPUTACIÓN (★ 3.4) | PUNTOS (0)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DriverMetricDarkCard("VIAJES", "🚀 15", Modifier.weight(1f))
            DriverMetricDarkCard("REPUTACIÓN", "★ 4.9", Modifier.weight(1f))
            DriverMetricDarkCard("PUNTOS", "🏃 2", Modifier.weight(1f))
        }

        // Sub-tabs: 🏆 Misiones (1) | ☆ Reseñas (8) | 🎁 Incentivos (0)
        Surface(
            color = Color(0xFF0F2231),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color(0xFFF59E0B),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🏆 Misiones (1)",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = ResolvitosSlate,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
                Text(
                    text = "☆ Reseñas (8)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                )
                Text(
                    text = "🎁 Incentivos (0)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                )
            }
        }

        // Achievement Card: "Novato al volante" (Video 2, 00:22-00:25)
        Surface(
            color = Color(0xFF082826),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, StatusEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(StatusEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    }
                    Column {
                        Text(
                            text = "Novato al volante",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Completa tus primeros 10 viajes o entregas para desbloquear este logro",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "¡COMPLETADO!",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = StatusEmerald
                    )
                    Text(
                        text = "15 / 10",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(StatusEmerald)
                )

                Surface(
                    color = Color(0xFF78350F).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Text(
                        text = "RECOMPENSA: 2 PUNTOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = BrightYellow,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DriverMetricDarkCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F2231),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        }
    }
}

// ============================================================================
// VIDEO 2 TAB 4: PERFIL / REGISTRO DE PILOTO + SUICHE DE APLICACIÓN (00:31-00:32)
// ============================================================================
@Composable
private fun DriverPerfilAndRegistrationTabVideo2(
    driver: DriverProfile,
    activeRole: String,
    onSaveDriverProfile: (String, String, String, String) -> Unit,
    onSelectRole: (String) -> Unit
) {
    var name by remember(driver.name) { mutableStateOf(driver.name) }
    var birthDate by remember(driver.birthDate) { mutableStateOf(driver.birthDate) }
    var cedula by remember(driver.cedula) { mutableStateOf(driver.cedula.removePrefix("V-")) }
    var phone by remember(driver.phone) { mutableStateOf(driver.phone) }
    var vehicleType by remember(driver.vehicleType) { mutableStateOf(driver.vehicleType) }
    var model by remember(driver.vehicleModel) { mutableStateOf(driver.vehicleModel) }
    var plate by remember(driver.vehiclePlate) { mutableStateOf(driver.vehiclePlate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Suiche de Aplicación inside Driver Settings/Perfil
        SettingsRoleSwitcherCard(
            activeRole = activeRole,
            onSelectRole = onSelectRole
        )

        // Registro de Piloto Card matching Video 2 (00:31 - 00:32)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResolvitosLogoBadge(boxSize = 40.dp, showTaglineRight = false)
                    Column {
                        Text(
                            text = "Registro de Piloto",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "DATOS PERSONALES Y UNIDAD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Yellow Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(BrightYellow)
                )

                Text(
                    text = "Datos Personales",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Ingresa tus datos reales exactamente como aparecen en tus documentos.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Text("NOMBRE Y APELLIDO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Ej: Juan Vicente Gómez") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("📅 FECHA DE NACIMIENTO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    placeholder = { Text("dd/mm/aaaa") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("CÉDULA DE IDENTIDAD", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BrightYellow,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "V\n(Venezolano)",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = ResolvitosSlate,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                    OutlinedTextField(
                        value = cedula,
                        onValueChange = { cedula = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("TELÉFONO MÓVIL (WHATSAPP)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = "🇻🇪 +58",
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)
                        )
                    }
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Modelo Vehículo") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it },
                        label = { Text("Placa") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.7f)
                    )
                }

                Button(
                    onClick = { onSaveDriverProfile(name, vehicleType, model, plate) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightYellow,
                        contentColor = ResolvitosSlate
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Guardar Datos de Piloto  >", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

// ============================================================================
// VIDEO 3: INTERFAZ DEL NEGOCIO ("Resolvitos Negocios - BUSINESS HUB")
// ============================================================================
enum class BusinessBottomTab(val label: String) {
    RESUMEN("Resumen"),
    PEDIDOS("Pedidos"),
    MENU("Menú"),
    CAJA("Caja"),
    AVISOS("Avisos")
}

@Composable
fun BusinessModeScreen(
    viewModel: ResolvitosViewModel,
    onRequestRoleSwitch: (String) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val liveBcv by viewModel.liveBcvRate.collectAsStateWithLifecycle()
    val comercios by viewModel.comercios.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val missions by viewModel.businessMissions.collectAsStateWithLifecycle()

    val bcvRate = liveBcv ?: settings.bcvRate
    var selectedStoreId by remember { mutableStateOf("store_sound_art") }
    val activeStore = comercios.firstOrNull { it.id == selectedStoreId }
        ?: comercios.firstOrNull()
        ?: Comercio(id = "store_sound_art", name = "Sound and Art Publicidad", category = "Supermercados")

    val storeProducts = products.filter { it.comercioId == activeStore.id }
    val storeOrders = orders.filter { it.comercioId == activeStore.id || orders.size <= 4 }
    val storeMissions = missions.filter { it.comercioId == activeStore.id }

    var activeTab by remember { mutableStateOf(BusinessBottomTab.RESUMEN) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    var showFidelizacionModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            // Video 3 Top Bar with Hamburger Menu ☰
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isDrawerOpen = true },
                        modifier = Modifier.testTag("business_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú Negocio", tint = ResolvitosSlate)
                    }

                    // Quick Store Selector Pill
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        comercios.forEach { store ->
                            val isSelected = store.id == activeStore.id
                            Surface(
                                color = if (isSelected) BrightYellow else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.clickable { selectedStoreId = store.id }
                            ) {
                                Text(
                                    text = store.name.take(14),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Main Business Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    BusinessBottomTab.RESUMEN -> {
                        BusinessResumenTabVideo3(
                            store = activeStore,
                            productsCount = storeProducts.size,
                            orders = storeOrders,
                            onSeeAllOrders = { activeTab = BusinessBottomTab.PEDIDOS }
                        )
                    }
                    BusinessBottomTab.PEDIDOS -> {
                        BusinessPedidosTabVideo3(
                            orders = storeOrders,
                            bcvRate = bcvRate,
                            onAdvanceOrder = { ord, next ->
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.CYCLE_READY)
                                viewModel.advanceOrderStatus(ord, next)
                            }
                        )
                    }
                    BusinessBottomTab.MENU -> {
                        BusinessCartaProductosTabVideo3(
                            store = activeStore,
                            products = storeProducts,
                            onAddProduct = { name, desc, price, cat ->
                                viewModel.addStoreProduct(
                                    comercioId = activeStore.id,
                                    comercioName = activeStore.name,
                                    name = name,
                                    description = desc,
                                    priceUsd = price,
                                    category = cat,
                                    stock = 50L
                                )
                            }
                        )
                    }
                    BusinessBottomTab.CAJA -> {
                        BusinessFinanzasYCajaTabVideo3(
                            orders = storeOrders,
                            bcvRate = bcvRate
                        )
                    }
                    BusinessBottomTab.AVISOS -> {
                        BusinessCentroNotificacionesTabVideo3(
                            orders = storeOrders,
                            activeRole = profile?.activeRole ?: "business",
                            onPlayChime = {
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.TICKET_BELL)
                            },
                            onSelectRole = onRequestRoleSwitch
                        )
                    }
                }
            }

            // Video 3 Bottom Bar: Resumen | Pedidos | Menú | Caja | Avisos
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        Triple(BusinessBottomTab.RESUMEN, "Resumen", Icons.Default.GridView),
                        Triple(BusinessBottomTab.PEDIDOS, "Pedidos", Icons.Default.ReceiptLong),
                        Triple(BusinessBottomTab.MENU, "Menú", Icons.Default.RestaurantMenu),
                        Triple(BusinessBottomTab.CAJA, "Caja", Icons.Default.AttachMoney),
                        Triple(BusinessBottomTab.AVISOS, "Avisos", Icons.Default.NotificationsNone)
                    )
                    tabs.forEach { (tab, label, icon) ->
                        val selected = activeTab == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { activeTab = tab }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("business_tab_${tab.name.lowercase()}")
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) ResolvitosSlate else Color(0xFF94A3B8),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (selected) ResolvitosSlate else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Video 3 Dark Lateral Drawer ("Resolvitos Negocios - BUSINESS HUB" - 00:31 to 00:38)
        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { isDrawerOpen = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(Color(0xFF0B1320))
                        .clickable(enabled = false) {}
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ResolvitosLogoBadge(boxSize = 40.dp, showTaglineRight = false)
                            Column {
                                Text(
                                    text = "Resolvitos Negocios",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "BUSINESS HUB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        IconButton(onClick = { isDrawerOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar menú", tint = Color(0xFF94A3B8))
                        }
                    }

                    // Scrollable Drawer Menu Items matching Video 3 (00:31 - 00:38)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val drawerItems = listOf(
                            Triple("Dashboard", BusinessBottomTab.RESUMEN, false),
                            Triple("Pedidos", BusinessBottomTab.PEDIDOS, false),
                            Triple("Caja y Finanzas", BusinessBottomTab.CAJA, false),
                            Triple("Productos", BusinessBottomTab.MENU, false),
                            Triple("Notificaciones", BusinessBottomTab.AVISOS, false),
                            Triple("Clientes", BusinessBottomTab.RESUMEN, false),
                            Triple("Fidelización (Misiones y Premios)", BusinessBottomTab.RESUMEN, true),
                            Triple("Meseros", BusinessBottomTab.PEDIDOS, false),
                            Triple("Cajeras", BusinessBottomTab.CAJA, false),
                            Triple("Mesas", BusinessBottomTab.PEDIDOS, false),
                            Triple("Estaciones", BusinessBottomTab.PEDIDOS, false),
                            Triple("Mi Negocio", BusinessBottomTab.RESUMEN, false),
                            Triple("Publicidad", BusinessBottomTab.RESUMEN, false),
                            Triple("Campañas Push", BusinessBottomTab.AVISOS, false),
                            Triple("Reseñas", BusinessBottomTab.AVISOS, false)
                        )
                        drawerItems.forEach { (title, targetTab, opensFidelizacion) ->
                            val isSelected = !opensFidelizacion && activeTab == targetTab &&
                                    title in listOf("Dashboard", "Pedidos", "Caja y Finanzas", "Productos", "Notificaciones")
                            Surface(
                                color = if (isSelected) BrightYellow else Color.Transparent,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isDrawerOpen = false
                                        if (opensFidelizacion) {
                                            showFidelizacionModal = true
                                        } else {
                                            activeTab = targetTab
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) ResolvitosSlate else Color(0xFFCBD5E1)
                                    )
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = if (isSelected) ResolvitosSlate else Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // Quick Role Switch inside Drawer
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isDrawerOpen = false
                                    onRequestRoleSwitch("client")
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔄 Suiche: Volver a App Usuario",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = BrightYellow
                                )
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = BrightYellow)
                            }
                        }
                    }

                    // Drawer Footer Profile Card + Cerrar Sesión
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = Color(0xFF162032),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background( Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeStore.name.take(1),
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        text = activeStore.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = profile?.email?.ifBlank { "louisnaranjo1@gmail.com" } ?: "louisnaranjo1@gmail.com",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSignOut() }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }
    }

    // Business Fidelización (Missions & Prizes) Dialog
    if (showFidelizacionModal) {
        BusinessFidelizacionMissionsDialog(
            store = activeStore,
            missions = storeMissions,
            bcvRate = bcvRate,
            onCreateMission = { title, desc, pts, targetOrders, bonusPts, reward, valUsd ->
                viewModel.createBusinessMission(
                    comercioId = activeStore.id,
                    comercioName = activeStore.name,
                    title = title,
                    description = desc,
                    targetPoints = pts,
                    targetOrders = targetOrders,
                    rewardTitle = reward,
                    rewardValueUsd = valUsd,
                    pointsReward = bonusPts,
                    badgeEmoji = "🏪"
                )
            },
            onToggleMission = { viewModel.toggleMissionActive(it) },
            onDismiss = { showFidelizacionModal = false }
        )
    }
}

// ============================================================================
// VIDEO 3 TAB 1: RESUMEN (00:08 - 00:10)
// ============================================================================
@Composable
private fun BusinessResumenTabVideo3(
    store: Comercio,
    productsCount: Int,
    orders: List<Order>,
    onSeeAllOrders: () -> Unit
) {
    val deliveredSales = orders.filter { it.status == "delivered" }.sumOf { it.totalUsd }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "¡Hola, ${store.name}! 👋",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Aquí tienes el resumen de tu negocio para hoy.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // 2x2 KPI Cards Grid (VENTAS TOTALES | PEDIDOS HOY | PRODUCTOS... | CRECIMIENTO)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BusinessKpiCardVideo3(
                        label = "VENTAS TOTALES",
                        value = formatUsd(deliveredSales),
                        badgeText = "$",
                        badgeBg = Color(0xFFECFDF5),
                        badgeColor = StatusEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    BusinessKpiCardVideo3(
                        label = "PEDIDOS HOY",
                        value = "${orders.size}",
                        badgeText = "📅",
                        badgeBg = Color(0xFFEFF6FF),
                        badgeColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BusinessKpiCardVideo3(
                        label = "PRODUCTOS...",
                        value = "$productsCount",
                        badgeText = "📈",
                        badgeBg = Color(0xFFFFFBEB),
                        badgeColor = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    BusinessKpiCardVideo3(
                        label = "CRECIMIENTO",
                        value = "+12%",
                        badgeText = "👥",
                        badgeBg = Color(0xFFF3E8FF),
                        badgeColor = Color(0xFF9333EA),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // "🕒 Pedidos Recientes" Card (Video 3, 00:08 - 00:10)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🕒  Pedidos\nRecientes",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "Ver\ntodos  >",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.clickable { onSeeAllOrders() }
                        )
                    }

                    orders.forEach { ord ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
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
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(
                                            text = "Pedido #${ord.id.takeLast(6).uppercase()}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = if (ord.status == "cancelled") "CANCELLED_BY_CLIENT" else "COMPLETED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatUsd(ord.totalUsd),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "05:17 p. m.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
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

@Composable
private fun BusinessKpiCardVideo3(
    label: String,
    value: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier
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
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badgeText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = ResolvitosSlate
            )
        }
    }
}

// ============================================================================
// VIDEO 3 TAB 2: PEDIDOS ("Gestión de Pedidos" - 00:11 to 00:15)
// ============================================================================
@Composable
private fun BusinessPedidosTabVideo3(
    orders: List<Order>,
    bcvRate: Double,
    onAdvanceOrder: (Order, String) -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0 = Pendientes, 1 = En Camino, 2 = Entregados, 3 = Mesas, 4 = Rechazados
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = orders.filter { ord ->
        when (selectedFilter) {
            0 -> ord.status in listOf("pending", "accepted", "preparing", "ready")
            1 -> ord.status == "dispatched"
            2 -> ord.status == "delivered"
            3 -> ord.deliveryType == "mesa"
            4 -> ord.status == "cancelled"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Gestión de Pedidos",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Monitorea y despacha tus órdenes al momento.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrightYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = ResolvitosSlate)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StatusEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StatusEmerald)
                        )
                    }
                }
            }
        }

        // Status Filter Tabs Row (Pendientes | En Camino | Entregados | Mesas | Rechazados)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    0 to "🕒 Pendientes  ${orders.count { it.status in listOf("pending", "accepted", "preparing", "ready") }}",
                    1 to "🚚 En Camino  ${orders.count { it.status == "dispatched" }}",
                    2 to "✓ Entregados  ${orders.count { it.status == "delivered" }}",
                    3 to "👥 Mesas  ${orders.count { it.deliveryType == "mesa" }}",
                    4 to "✕ Rechazados  ${orders.count { it.status == "cancelled" }}"
                )
                tabs.forEach { (idx, label) ->
                    val active = selectedFilter == idx
                    Surface(
                        color = if (active) Color.White else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (active) StatusEmerald else Color(0xFFE2E8F0)),
                        modifier = Modifier.clickable { selectedFilter = idx }
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (active) StatusEmerald else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Search input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por ID de pedido o dirección...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🕒", fontSize = 38.sp)
                        Text(
                            text = "Sin pedidos en esta\nsección",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Aquí aparecerán los pedidos que coincidan con el estado seleccionado.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { ord ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Pedido #${ord.id.takeLast(6).uppercase()} • ${ord.clientName}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = formatDualPrice(ord.totalUsd, bcvRate),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = StatusEmerald
                            )
                        }
                        Text(ord.itemsSummary, fontSize = 12.sp, color = Color(0xFF475569))
                        if (ord.status !in listOf("delivered", "cancelled")) {
                            Button(
                                onClick = {
                                    val next = when (ord.status) {
                                        "pending" -> "preparing"
                                        "preparing" -> "ready"
                                        "ready" -> "dispatched"
                                        else -> "delivered"
                                    }
                                    onAdvanceOrder(ord, next)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrightYellow,
                                    contentColor = ResolvitosSlate
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Avanzar Estado de Orden", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// VIDEO 3 TAB 3: MENÚ ("Carta de Productos" - 00:16 to 00:18)
// ============================================================================
@Composable
private fun BusinessCartaProductosTabVideo3(
    store: Comercio,
    products: List<Product>,
    onAddProduct: (String, String, Double, String) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("2.50") }
    var newCategory by remember { mutableStateOf(store.category) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Carta de\nProductos",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        lineHeight = 25.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Gestiona tu menú, precios y disponibilidad.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = "📦 Comunes",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Color(0xFFD97706),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }

                    Button(
                        onClick = { showAddForm = !showAddForm },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Añadir", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        if (showAddForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Nuevo Producto", fontWeight = FontWeight.ExtraBold)
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Nombre del producto") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newPrice,
                                onValueChange = { newPrice = it },
                                label = { Text("Precio USD") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newCategory,
                                onValueChange = { newCategory = it },
                                label = { Text("Categoría") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                if (newName.isNotBlank()) {
                                    onAddProduct(newName, newName, newPrice.toDoubleOrNull() ?: 1.0, newCategory)
                                    newName = ""
                                    showAddForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ResolvitosSlate, contentColor = BrightYellow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar en Carta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = ResolvitosSlate, shape = RoundedCornerShape(50)) {
                    Text(
                        text = "Todos",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = store.category,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2-Column Product Cards matching Video 3 (00:16 - 00:18)
        items(products.chunked(2)) { rowProds ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProds.forEach { prod ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF8FAFC))
                            ) {
                                // Top-Left Green Check
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusEmerald),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }

                                Text(
                                    text = prod.name.take(10).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.align(Alignment.Center)
                                )

                                // Bottom-Right Dark Price Tag
                                Surface(
                                    color = ResolvitosSlate,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "$${String.format(Locale.US, "%.1f", prod.priceUsd)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = prod.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Víveres",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFF1F2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusCoral, modifier = Modifier.size(14.dp))
                                }
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+$${String.format(Locale.US, "%.1f", prod.priceUsd)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = StatusEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
    }
}

// ============================================================================
// VIDEO 3 TAB 4: CAJA ("Finanzas y Caja" - 00:19 to 00:20)
// ============================================================================
@Composable
private fun BusinessFinanzasYCajaTabVideo3(
    orders: List<Order>,
    bcvRate: Double
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = VENTAS HOY, 1 = GASTOS, 2 = CIERRES

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$ Finanzas y\nCaja",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        lineHeight = 25.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Gestiona ingresos, gastos y cierre de caja.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3))
                    ) {
                        Text(
                            text = "↘ Gasto",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = StatusCoral,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                    Surface(
                        color = ResolvitosSlate,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💼 Caja",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // 3 Summary Cards: VENTAS HOY | GASTOS... | GANANCIA
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinanceSummaryMiniCard("↗ VENTAS HOY", "$0.00", ResolvitosSlate, Modifier.weight(1f))
                FinanceSummaryMiniCard("↘ GASTOS...", "$0.00", ResolvitosSlate, Modifier.weight(1f))
                FinanceSummaryMiniCard("$ GANANCIA", "$0.00", StatusEmerald, Modifier.weight(1f))
            }
        }

        // Sub-tabs: VENTAS HOY | GASTOS | CIERRES
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("VENTAS HOY", "GASTOS", "CIERRES").forEachIndexed { idx, label ->
                            val active = selectedSubTab == idx
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedSubTab = idx }
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = if (active) ResolvitosSlate else Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (active) {
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(2.dp)
                                            .background(ResolvitosSlate)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(42.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("$", fontWeight = FontWeight.Black, fontSize = 42.sp, color = Color(0xFFE2E8F0))
                        Text(
                            text = "No hay ventas registradas hoy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceSummaryMiniCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8), maxLines = 1)
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = valueColor)
        }
    }
}

// ============================================================================
// VIDEO 3 TAB 5: AVISOS ("Centro de Notificaciones" - 00:21 to 00:30)
// ============================================================================
@Composable
private fun BusinessCentroNotificacionesTabVideo3(
    orders: List<Order>,
    activeRole: String,
    onPlayChime: () -> Unit,
    onSelectRole: (String) -> Unit
) {
    var topMode by remember { mutableIntStateOf(0) } // 0 = Actividad (14), 1 = Ajustes
    var activityFilter by remember { mutableIntStateOf(0) } // 0 = Todo, 1 = Seguidores, 2 = Pedidos, 3 = Reseñas

    var followersNotify by remember { mutableStateOf(true) }
    var ordersNotify by remember { mutableStateOf(true) }
    var reviewsNotify by remember { mutableStateOf(true) }
    var chimeNotify by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "🔔 Centro de Notificaciones",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = "Actividad de seguidores, pedidos y preferencias de aviso",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Top Segmented Pill: [🕒 Actividad (14)] | [⚙️ Ajustes]
        item {
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    listOf(
                        0 to "🕒 Actividad (14)",
                        1 to "⚙️ Ajustes"
                    ).forEach { (idx, title) ->
                        val active = topMode == idx
                        Surface(
                            color = if (active) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = if (active) 2.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { topMode = idx }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = if (active) ResolvitosSlate else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (topMode == 0) {
            // Actividad Filter Chips: Todo | Seguidores | Pedidos | Reseñas
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todo", "Seguidores", "Pedidos", "Reseñas").forEachIndexed { idx, label ->
                        val active = activityFilter == idx
                        Surface(
                            color = if (active) ResolvitosSlate else Color.White,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, if (active) ResolvitosSlate else Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable { activityFilter = idx }
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (active) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Notification Feed Cards matching Video 3 (00:22 - 00:30)
            val feedItems = listOf(
                Triple("Nuevo Pedido", "Louis Naranjo • Total: $20.00 (Delivery) • 1 prod: 5x Mantequilla Mavesa - 500gr", "15 oct, 05:17 p. m."),
                Triple("Nueva Reseña (4★)", "Louis Naranjo: \"Listo\"", "15 oct, 12:06 a. m."),
                Triple("Nuevo Pedido", "Louis Naranjo • Total: $16.20 (PickUp) • 2 prod: 1x Mezcla hortalizas OSOLE, 1x Mantequilla Mavesa", "15 oct, 12:03 a. m."),
                Triple("Nueva Reseña (4★)", "Louis Naranjo: \"Chao\"", "14 oct, 03:40 p. m.")
            )
            items(feedItems) { (title, body, dateStr) ->
                val isReview = title.contains("Reseña")
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isReview) Color(0xFFFFFBEB) else Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isReview) Icons.Default.Star else Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = if (isReview) Color(0xFFF59E0B) else Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ResolvitosSlate)
                                Text(dateStr, fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(body, fontSize = 12.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }
        } else {
            // Preferencias de Avisos + Suiche de Aplicación (Video 3, 00:24 - 00:27)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Preferencias de Avisos",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "Elige qué eventos quieres que activen notificaciones y alertas de sonido.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        BusinessPrefToggleItem("Nuevos Seguidores", "Avisar en tiempo real cuando un usuario te siga", followersNotify) { followersNotify = it }
                        BusinessPrefToggleItem("Nuevos Pedidos y Compras", "Alertas inmediatas en pantalla al recibir pedidos", ordersNotify) { ordersNotify = it }
                        BusinessPrefToggleItem("Calificaciones y Reseñas", "Notificar opiniones dejadas por los clientes", reviewsNotify) { reviewsNotify = it }
                        BusinessPrefToggleItem("Alertas Sonoras (Chime)", "Reproducir timbre de audio al recibir eventos", chimeNotify) { chimeNotify = it }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Probar sonido de\nnotificación:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            OutlinedButton(
                                onClick = onPlayChime,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reproducir\nTimbre", fontWeight = FontWeight.Black, fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            item {
                SettingsRoleSwitcherCard(
                    activeRole = activeRole,
                    onSelectRole = onSelectRole
                )
            }
        }
    }
}

@Composable
private fun BusinessPrefToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ResolvitosSlate)
                Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ResolvitosSlate
                )
            )
        }
    }
}

@Composable
private fun BusinessFidelizacionMissionsDialog(
    store: Comercio,
    missions: List<BusinessMission>,
    bcvRate: Double,
    onCreateMission: (String, String, Long, Long, Long, String, Double) -> Unit,
    onToggleMission: (BusinessMission) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetPts by remember { mutableStateOf("100") }
    var targetOrders by remember { mutableStateOf("2") }
    var bonusPts by remember { mutableStateOf("60") }
    var rewardTitle by remember { mutableStateOf("") }
    var rewardUsd by remember { mutableStateOf("5.00") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎁 Retos Gamificados y Premios\n${store.name}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = ResolvitosSlate
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Reto (Ej: Rey del Grill)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetPts,
                        onValueChange = { targetPts = it },
                        label = { Text("Meta Pts") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetOrders,
                        onValueChange = { targetOrders = it },
                        label = { Text("Meta Pedidos") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bonusPts,
                        onValueChange = { bonusPts = it },
                        label = { Text("Pts Bono") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rewardUsd,
                        onValueChange = { rewardUsd = it },
                        label = { Text("Valor Premio $") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = rewardTitle,
                    onValueChange = { rewardTitle = it },
                    label = { Text("Premio Desbloqueable (Ej: Combo Gratis)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (title.isNotBlank() && rewardTitle.isNotBlank()) {
                            val parsedPts = targetPts.toLongOrNull() ?: 100L
                            val parsedOrd = targetOrders.toLongOrNull() ?: 2L
                            val parsedBonus = bonusPts.toLongOrNull() ?: 50L
                            onCreateMission(
                                title,
                                "Acumula $parsedPts pts o $parsedOrd pedidos en ${store.name} para ganar +$parsedBonus pts y desbloquear $rewardTitle.",
                                parsedPts,
                                parsedOrd,
                                parsedBonus,
                                rewardTitle,
                                rewardUsd.toDoubleOrNull() ?: 5.0
                            )
                            title = ""
                            rewardTitle = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightYellow, contentColor = ResolvitosSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar Reto y Premio", fontWeight = FontWeight.ExtraBold)
                }

                HorizontalDivider()

                missions.forEach { m ->
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(m.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ResolvitosSlate)
                                Text(
                                    text = "Meta: ${m.targetPoints} pts / ${m.targetOrders} ped • +${m.pointsReward} pts bono",
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569)
                                )
                                Text("🎁 ${m.rewardTitle} (${formatDualPrice(m.rewardValueUsd, bcvRate)})", fontSize = 11.sp, color = StatusEmerald)
                            }
                            Switch(checked = m.isActive, onCheckedChange = { onToggleMission(m) })
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUPERADMIN CPANEL SCREEN
// ============================================================================
@Composable
fun AdminCPanelScreen(
    viewModel: ResolvitosViewModel,
    onRequestRoleSwitch: (String) -> Unit = {}
) {
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val liveBcv by viewModel.liveBcvRate.collectAsStateWithLifecycle()
    val comercios by viewModel.comercios.collectAsStateWithLifecycle()
    val missions by viewModel.businessMissions.collectAsStateWithLifecycle()

    val effectiveBcv = liveBcv ?: settings.bcvRate
    var bcvInput by remember(effectiveBcv) { mutableStateOf(effectiveBcv.toString()) }
    var msnTitle by remember { mutableStateOf("") }
    var msnReward by remember { mutableStateOf("") }
    var msnPoints by remember { mutableStateOf("150") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingsRoleSwitcherCard(
                activeRole = "admin",
                onSelectRole = onRequestRoleSwitch
            )
        }

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
                    Text("Tasa Oficial BCV Venezuela (API en Vivo)", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ResolvitosSlate)
                    OutlinedTextField(
                        value = bcvInput,
                        onValueChange = { bcvInput = it },
                        label = { Text("Tasa Oficial BCV (Bs./USD)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_bcv_rate_input")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.refreshOfficialBcvRate() },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald, contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sincronizar API BCV", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.updateSystemSettings(
                                    bcvRate = bcvInput.toDoubleOrNull() ?: effectiveBcv,
                                    commissionPercent = settings.commissionPercent,
                                    termsClient = settings.termsClient,
                                    termsDriver = settings.termsDriver,
                                    termsBusiness = settings.termsBusiness
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightYellow, contentColor = ResolvitosSlate),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_save_settings_button")
                        ) {
                            Text("Guardar Tasa", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

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
                    Text("Crear Misión y Premio Desbloqueable (Global o Comercio)", fontWeight = FontWeight.Black, fontSize = 15.sp, color = ResolvitosSlate)
                    OutlinedTextField(
                        value = msnTitle,
                        onValueChange = { msnTitle = it },
                        label = { Text("Título de la Misión") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = msnReward,
                        onValueChange = { msnReward = it },
                        label = { Text("Premio Desbloqueable") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = msnPoints,
                        onValueChange = { msnPoints = it },
                        label = { Text("Puntos requeridos") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (msnTitle.isNotBlank() && msnReward.isNotBlank()) {
                                viewModel.createBusinessMission(
                                    comercioId = "global",
                                    comercioName = "Resolvitos Global (Superadmin)",
                                    title = msnTitle,
                                    description = "Misión oficial configurada desde el CPanel",
                                    targetPoints = msnPoints.toLongOrNull() ?: 150L,
                                    targetOrders = 1L,
                                    rewardTitle = msnReward,
                                    rewardValueUsd = 5.0
                                )
                                msnTitle = ""
                                msnReward = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResolvitosSlate, contentColor = BrightYellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_create_mission_button")
                    ) {
                        Text("Publicar Misión y Premio", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        items(missions, key = { it.id }) { m ->
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
                        Text("${m.comercioName} • ${m.title}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        Text("🎁 ${m.rewardTitle} (${m.targetPoints} pts)", fontSize = 11.sp, color = StatusEmerald)
                    }
                    Switch(checked = m.isActive, onCheckedChange = { viewModel.toggleMissionActive(m) })
                }
            }
        }
    }
}
