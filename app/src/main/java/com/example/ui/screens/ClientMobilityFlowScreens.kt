package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CITY_MAP_PRESETS
import com.example.data.model.MapPointPreset
import com.example.data.model.TransportBid
import com.example.data.model.TransportRequest
import com.example.data.model.formatBs
import com.example.data.model.formatDualPrice
import com.example.data.model.formatUsd
import com.example.ui.components.BrightYellow
import com.example.ui.components.MapProviderType
import com.example.ui.components.ModularInteractiveMapView
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCoral
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.DriverFareQuote
import com.example.ui.viewmodel.ResolvitosViewModel
import com.example.util.ResolvitosSoundEvent
import com.example.util.SoundAndHapticManager
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ClientTaxiServiceHubScreen(
    viewModel: ResolvitosViewModel,
    bcvRate: Double,
    transportRequests: List<TransportRequest>,
    onBack: () -> Unit,
    onSelectService: (String, String) -> Unit
) {
    val context = LocalContext.current
    val mapProvider by viewModel.mapProvider.collectAsStateWithLifecycle()
    var showFastOneTouchModal by remember { mutableStateOf(false) }

    val activeOrPendingRide = transportRequests.firstOrNull { req ->
        req.status in listOf("searching", "accepted", "arrived", "in_transit") ||
            (req.status == "completed" && (req.paymentStatus != "confirmed" || req.rating == 0L))
    }

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

            // Map Provider Switcher Pill (Google Maps Preview <-> Mapbox Preview)
            Surface(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, BrightYellow.copy(alpha = 0.6f)),
                modifier = Modifier
                    .clickable {
                        val next = if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) {
                            MapProviderType.MAPBOX_PREVIEW
                        } else {
                            MapProviderType.GOOGLE_MAPS_PREVIEW
                        }
                        viewModel.setMapProvider(next)
                    }
                    .testTag("switch_map_provider_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = null,
                        tint = BrightYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) "Google Maps" else "Mapbox",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Active Ride Banner if user has an ongoing or unconciliated trip
        if (activeOrPendingRide != null) {
            Surface(
                color = BrightYellow,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectService(activeOrPendingRide.serviceCategory, activeOrPendingRide.vehicleType)
                    }
                    .testTag("resume_active_ride_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "● SERVICIO EN CURSO / POR CONCILIAR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "${activeOrPendingRide.destinationAddress.take(34)} • ${formatUsd(activeOrPendingRide.priceUsd)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                    }
                    Surface(
                        color = ResolvitosSlate,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "VER EN MAPA →",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = BrightYellow,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
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

        // 2x2 Grid of 4 Bright Yellow Cards matching Videos 1, 4, 5, 6, 7
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HubYellowServiceCard(
                badge = "PASAJEROS",
                title = "Taxi / Mototaxi",
                subtitle = "Viaja seguro por la ciudad al mejor precio",
                chips = listOf("Moto", "Carro"),
                icon = Icons.Default.DirectionsCar,
                onClick = { onSelectService("transport", "moto") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_pasajeros")
            )
            HubYellowServiceCard(
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
            HubYellowServiceCard(
                badge = "DILIGENCIAS",
                title = "Muchacho e' Mandao",
                subtitle = "Compras, pagos y diligencias a tu medida",
                chips = listOf("Compras", "Trámites"),
                icon = Icons.Default.ShoppingBag,
                onClick = { onSelectService("mandado", "moto") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobility_card_shopper")
            )
            HubYellowServiceCard(
                badge = "⚡ 1 TOQUE",
                title = "Transporte Rápido",
                subtitle = "Pide en segundos con radar masivo sin esperas",
                chips = listOf("GPS Inmediato", "Sin escribir"),
                icon = Icons.Default.Bolt,
                onClick = { showFastOneTouchModal = true },
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
                            text = "CARRO TAXI EXPRESS",
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
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
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
                        text = "Tarifas transparentes • Sin cargos ocultos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }

    // Flow 4 Modal: "⚡ Transporte Rápido — Solicitud en 1 toque directo al radar" (Video 7)
    if (showFastOneTouchModal) {
        FastOneTouchTransportModal(
            bcvRate = bcvRate,
            onRequestQuickTransport = { vehicleType, basePriceUsd ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.ALERT_LOOP)
                showFastOneTouchModal = false
                viewModel.setServiceCategory("fast_1touch")
                viewModel.setVehicleType(vehicleType)
                viewModel.requestRideOrMandado(
                    selectedQuote = null,
                    customOfferedUsd = basePriceUsd,
                    packageDescription = "Transporte Rápido 1 Toque (GPS Directo)",
                    paymentMethod = "Pago Móvil al Conductor"
                )
                onSelectService("fast_1touch", vehicleType)
            },
            onDismiss = { showFastOneTouchModal = false }
        )
    }
}

@Composable
private fun HubYellowServiceCard(
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
                color = ResolvitosSlate.copy(alpha = 0.85f),
                minLines = 2,
                maxLines = 2
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                chips.forEach { chip ->
                    Surface(
                        color = ResolvitosSlate.copy(alpha = 0.12f),
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
 * Modal Flow 4 (Video 7): "⚡ Transporte Rápido — Solicitud en 1 toque directo al radar"
 */
@Composable
private fun FastOneTouchTransportModal(
    bcvRate: Double,
    onRequestQuickTransport: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ResolvitosSlate),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightYellow)
                        }
                        Column {
                            Text(
                                text = "Transporte Rápido",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = "Solicitud en 1 toque directo al radar",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                    }
                }

                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = StatusEmerald, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Ubicación GPS actual detectada • Sin escribir dirección",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                    }
                }

                // 2 Cards: Moto ($0.50) and Carro Económico ($1.50)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, ResolvitosSlate),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onRequestQuickTransport("moto", 0.50) }
                            .testTag("fast_1touch_moto_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🛵", fontSize = 32.sp)
                            Text(
                                text = "Moto",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = ResolvitosSlate
                            )
                            Surface(color = ResolvitosSlate, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Desde $0.50",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = formatBs(0.50, bcvRate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "1 Pasajero • ~3 min",
                                fontSize = 10.sp,
                                color = StatusEmerald,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color(0xFF2563EB)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onRequestQuickTransport("taxi_economico", 1.50) }
                            .testTag("fast_1touch_carro_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🚕", fontSize = 32.sp)
                            Text(
                                text = "Carro",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = ResolvitosSlate
                            )
                            Surface(color = Color(0xFF1D4ED8), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Desde $1.50",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = formatBs(1.50, bcvRate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "4 Pasajeros • Confort",
                                fontSize = 10.sp,
                                color = Color(0xFF1D4ED8),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Unified Client Mobility Screen handling:
 * - Active Ride Live Map Tracking + Driver Profile + Pago Móvil Copy Card + Capture Upload Conciliation + Star Rating
 * - Flow 1: Pasajeros ("Taxi / Mototaxi")
 * - Flow 2: Express ("Envío de Paquete / Ficha de Encomienda")
 * - Flow 3: Diligencias ("Muchacho e' Mandao" 4-Step Wizard + Live Subasta de Tarifas)
 */
@Composable
fun ClientMobilityInteractiveFlowScreen(
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
    val mapProvider by viewModel.mapProvider.collectAsStateWithLifecycle()

    // Check if there is an active ride or a completed ride awaiting payment conciliation / rating
    val activeRequest = transportRequests.firstOrNull { req ->
        req.status in listOf("searching", "accepted", "arrived", "in_transit") ||
            (req.status == "completed" && (req.paymentStatus != "confirmed" || req.rating == 0L))
    }

    if (activeRequest != null) {
        val activeBids = transportBids.filter { it.requestId == activeRequest.id && it.status == "pending" }
        ClientActiveRideTrackingAndConciliationScreen(
            request = activeRequest,
            bids = activeBids,
            bcvRate = bcvRate,
            mapProvider = mapProvider,
            onSwitchMapProvider = { next -> viewModel.setMapProvider(next) },
            onAcceptBid = { bid ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                viewModel.acceptDriverBid(activeRequest, bid)
            },
            onSimulateIncomingBid = {
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.CYCLE_DRIVER)
                viewModel.submitDriverBid(activeRequest, activeRequest.priceUsd, 4L)
            },
            onCancelRequest = {
                viewModel.cancelTransportRequest(activeRequest.id)
            },
            onOpenChat = {
                viewModel.openChat(activeRequest.id, activeRequest.assignedDriverName.ifBlank { "Conductor Resolvitos" })
            },
            onSubmitPaymentCapture = { ref, uri ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                viewModel.submitRidePaymentCapture(activeRequest.id, ref, uri)
            },
            onSubmitRating = { stars, comment, tags ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.DONE_SUCCESS)
                viewModel.submitRideRating(activeRequest.id, stars, comment, tags)
            },
            onBackToHub = onBackToHub
        )
        return
    }

    // If category == "mandado", show the 4-Step "Muchacho e' Mandao" Wizard (Video 6)
    if (selectedServiceCategory == "mandado") {
        MuchachoEMandaoWizardScreen(
            viewModel = viewModel,
            bcvRate = bcvRate,
            origin = selectedOrigin,
            destination = selectedDestination,
            mapProvider = mapProvider,
            onBack = onBackToHub
        )
        return
    }

    // Flow 1 (Pasajeros: Taxi / Mototaxi) & Flow 2 (Express: Envío de Paquete / Encomienda)
    val isNightShift by viewModel.isNightShiftTariff.collectAsStateWithLifecycle()
    val distanceKm = remember(selectedOrigin, selectedDestination) {
        viewModel.currentRouteDistanceKm()
    }
    val driverQuotes = remember(selectedOrigin, selectedDestination, selectedVehicleType, drivers, isNightShift, bcvRate) {
        viewModel.computeDriverQuotesForCurrentRoute()
    }
    val motoQuotes = remember(selectedOrigin, selectedDestination, drivers, isNightShift, bcvRate) {
        viewModel.computeDriverQuotesForVehicleType("moto")
    }
    val taxiQuotes = remember(selectedOrigin, selectedDestination, drivers, isNightShift, bcvRate) {
        viewModel.computeDriverQuotesForVehicleType("taxi_economico")
    }
    var selectedDriverQuote by remember(driverQuotes) {
        mutableStateOf(driverQuotes.firstOrNull())
    }
    val activeBreakdown = remember(selectedDriverQuote, selectedOrigin, selectedDestination, selectedVehicleType, isNightShift, bcvRate) {
        selectedDriverQuote?.breakdown ?: viewModel.computeRepresentativeFareBreakdown(selectedVehicleType)
    }

    var selectingOriginOnMap by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("Pago Móvil al Conductor") }
    var showPaymentMethodModal by remember { mutableStateOf(false) }

    // Flow 2 (Encomienda) state fields matching Video 5
    var receiverName by remember { mutableStateOf("María Pérez") }
    var receiverPhone by remember { mutableStateOf("4121234567") }
    var packageDetail by remember { mutableStateOf("Sobre sellado con documentos y llaves") }
    var freightPaidBy by remember { mutableStateOf("remitente") }

    val etaMinutes = maxOf(3, (distanceKm * 2.2).toInt())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Integrated Street Map Header (Google Maps Preview / Mapbox Preview)
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                val origPreset = selectedOrigin
                val destPreset = selectedDestination
                ModularInteractiveMapView(
                    origin = origPreset,
                    destination = destPreset,
                    distanceKm = distanceKm,
                    etaMinutes = etaMinutes,
                    provider = mapProvider,
                    selectingOriginMode = selectingOriginOnMap,
                    onTapMapCoordinate = { normX, normY ->
                        if (selectingOriginOnMap) {
                            viewModel.setOriginFromMapTap(normX, normY)
                        } else {
                            viewModel.setDestinationFromMapTap(normX, normY)
                        }
                    },
                    onRecenter = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(275.dp)
                )

                // Back button + Provider toggle overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ResolvitosSlate.copy(alpha = 0.88f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onBackToHub() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }

                    Surface(
                        color = ResolvitosSlate.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, BrightYellow),
                        modifier = Modifier.clickable {
                            val next = if (mapProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) {
                                MapProviderType.MAPBOX_PREVIEW
                            } else {
                                MapProviderType.GOOGLE_MAPS_PREVIEW
                            }
                            viewModel.setMapProvider(next)
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
        }

        // 2. Origin (A) & Destination (B) Card matching Video 4 & Video 5
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
                        Text(
                            text = if (selectedServiceCategory == "encomienda") {
                                "¿Qué paquete deseas enviar?"
                            } else {
                                "¿A dónde vamos?"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .clickable { viewModel.swapOriginAndDestination() }
                                    .testTag("swap_origin_destination_button")
                            ) {
                                Text(
                                    text = "⇅ A↔B",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ResolvitosSlate,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                color = ResolvitosSlate,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .clickable { selectingOriginOnMap = !selectingOriginOnMap }
                                    .testTag("toggle_map_selection_target_button")
                            ) {
                                Text(
                                    text = if (selectingOriginOnMap) "📍 Tocando Origen A" else "🏁 Tocando Destino B",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Punto A (Origen)
                    Surface(
                        color = if (selectingOriginOnMap) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (selectingOriginOnMap) StatusEmerald else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectingOriginOnMap = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(StatusEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PUNTO DE RECOGIDA (ORIGEN)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${selectedOrigin.name} • ${selectedOrigin.address}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Preset chips for Origin/Destination
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CITY_MAP_PRESETS, key = { "preset_${it.name}" }) { preset ->
                            val isSelected = if (selectingOriginOnMap) {
                                selectedOrigin.name == preset.name
                            } else {
                                selectedDestination.name == preset.name
                            }
                            Surface(
                                color = if (isSelected) ResolvitosSlate else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .clickable {
                                        if (selectingOriginOnMap) {
                                            viewModel.setOriginPoint(preset)
                                        } else {
                                            viewModel.setDestinationPoint(preset)
                                        }
                                    }
                                    .testTag("map_preset_chip_${preset.name}")
                            ) {
                                Text(
                                    text = preset.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BrightYellow else ResolvitosSlate,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Punto B (Destino)
                    Surface(
                        color = if (!selectingOriginOnMap) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (!selectingOriginOnMap) ResolvitosSlate else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectingOriginOnMap = false }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(ResolvitosSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", fontWeight = FontWeight.Black, fontSize = 12.sp, color = BrightYellow)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedServiceCategory == "encomienda") "PUNTO DE ENTREGA (DESTINO)" else "¿A DÓNDE VAS? (DESTINO)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${selectedDestination.name} • ${selectedDestination.address}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Vehicle Type Cards (Mototaxi / Moto Envíos vs Taxi Económico / Auto Envíos)
        item {
            val motoBreakdown = remember(selectedOrigin, selectedDestination, drivers, isNightShift, bcvRate, selectedDriverQuote, selectedVehicleType) {
                if (selectedVehicleType == "moto" && selectedDriverQuote != null) {
                    selectedDriverQuote!!.breakdown
                } else {
                    motoQuotes.firstOrNull()?.breakdown ?: viewModel.computeRepresentativeFareBreakdown("moto")
                }
            }
            val taxiBreakdown = remember(selectedOrigin, selectedDestination, drivers, isNightShift, bcvRate, selectedDriverQuote, selectedVehicleType) {
                if (selectedVehicleType == "taxi_economico" && selectedDriverQuote != null) {
                    selectedDriverQuote!!.breakdown
                } else {
                    taxiQuotes.firstOrNull()?.breakdown ?: viewModel.computeRepresentativeFareBreakdown("taxi_economico")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Mototaxi / Moto Envíos
                val isMoto = selectedVehicleType == "moto"
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = if (isMoto) 2.5.dp else 1.dp,
                        color = if (isMoto) ResolvitosSlate else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (isMoto) 4.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setVehicleType("moto") }
                        .testTag("vehicle_type_moto")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isMoto) ResolvitosSlate else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = if (isMoto) BrightYellow else ResolvitosSlate,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (selectedServiceCategory == "encomienda") "Moto Envíos" else "Mototaxi",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = ResolvitosSlate
                                    )
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "⚡ ~$etaMinutes min",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = StatusEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Base ${formatUsd(motoBreakdown.appliedBaseFareUsd)} (${motoBreakdown.baseIncludedKm.toInt()} km) + ${formatUsd(motoBreakdown.appliedRatePerKmUsd)}/km",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatUsd(motoBreakdown.totalUsd),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = formatBs(motoBreakdown.totalUsd, bcvRate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                // Card 2: Taxi Económico / Auto Envíos
                val isTaxi = selectedVehicleType == "taxi_economico"
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = if (isTaxi) 2.5.dp else 1.dp,
                        color = if (isTaxi) ResolvitosSlate else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (isTaxi) 4.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setVehicleType("taxi_economico") }
                        .testTag("vehicle_type_taxi_economico")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isTaxi) ResolvitosSlate else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = if (isTaxi) BrightYellow else ResolvitosSlate,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (selectedServiceCategory == "encomienda") "Auto Envíos" else "Taxi Económico",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = ResolvitosSlate
                                    )
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "⚡ ~$etaMinutes min",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = StatusEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Base ${formatUsd(taxiBreakdown.appliedBaseFareUsd)} (${taxiBreakdown.baseIncludedKm.toInt()} km) + ${formatUsd(taxiBreakdown.appliedRatePerKmUsd)}/km",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatUsd(taxiBreakdown.totalUsd),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = formatBs(taxiBreakdown.totalUsd, bcvRate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        }

        // 3B. Dedicated Bimonetary Fare Breakdown Card (Distancia + Tarifa Base + Excedente USD & Bs. BCV)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResolvitosSlate),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("bimonetary_fare_breakdown_card")
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
                                text = "CÁLCULO DE TARIFA BIMONETARIA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow
                            )
                            Text(
                                text = "Distancia en mapa: ${String.format(Locale.US, "%.2f", activeBreakdown.distanceKm)} km • ~$etaMinutes min",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Day / Night shift toggle pill
                        Surface(
                            color = Color.White.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, BrightYellow.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .clickable { viewModel.setNightShiftTariff(!isNightShift) }
                                .testTag("toggle_shift_tariff_button")
                        ) {
                            Text(
                                text = if (isNightShift) "🌙 Nocturna" else "☀️ Diurna",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrightYellow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Arranque base (incluye ${activeBreakdown.baseIncludedKm.toInt()} km)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "${formatUsd(activeBreakdown.appliedBaseFareUsd)} (${String.format(Locale.US, "%.2f Bs", activeBreakdown.baseFareBs)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Km adicional (${String.format(Locale.US, "%.2f", activeBreakdown.extraDistanceKm)} km × ${formatUsd(activeBreakdown.appliedRatePerKmUsd)}/km)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "+${formatUsd(activeBreakdown.extraDistanceChargeUsd)} (${String.format(Locale.US, "%.2f Bs", activeBreakdown.extraDistanceChargeBs)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeBreakdown.extraDistanceChargeUsd > 0.0) BrightYellow else Color.White
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL ESTIMADO (BCV ${String.format(Locale.US, "%.2f", bcvRate)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrightYellow
                        )
                        Text(
                            text = "${formatUsd(activeBreakdown.totalUsd)} | ${String.format(Locale.US, "%.2f Bs", activeBreakdown.totalBs)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = BrightYellow
                        )
                    }
                }
            }
        }

        // 4. Per-Driver Configured Tariff Selector
        if (driverQuotes.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pilotos en Línea y sus Tarifas (${String.format(Locale.US, "%.2f", distanceKm)} km)",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = ResolvitosSlate
                        )
                        driverQuotes.forEach { quote ->
                            val isSelected = selectedDriverQuote?.driver?.id == quote.driver.id
                            Surface(
                                color = if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ResolvitosSlate else Color(0xFFE2E8F0)
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
                                            text = "Base $${String.format(Locale.US, "%.2f", quote.breakdown.appliedBaseFareUsd)} (${quote.breakdown.baseIncludedKm.toInt()} km) + $${String.format(Locale.US, "%.2f", quote.breakdown.appliedRatePerKmUsd)}/km",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = formatUsd(quote.calculatedFareUsd),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
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
                    }
                }
            }
        }

        // 5. Flow 2 Exclusive Card: "📦 Ficha de Encomienda / Paquete [OBLIGATORIO]" (Video 5)
        if (selectedServiceCategory == "encomienda") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                            Text(
                                text = "📦 Ficha de Encomienda / Paquete",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = ResolvitosSlate
                            )
                            Surface(color = ResolvitosSlate, shape = RoundedCornerShape(50)) {
                                Text(
                                    text = "OBLIGATORIO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = "DESTINATARIO (QUIÉN RECIBE)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                        OutlinedTextField(
                            value = receiverName,
                            onValueChange = { receiverName = it },
                            placeholder = { Text("Ej: María Pérez") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(14.dp))
                        )

                        Text(
                            text = "TELÉFONO DEL DESTINATARIO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = ResolvitosSlate,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "🇻🇪 +58",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                                )
                            }
                            OutlinedTextField(
                                value = receiverPhone,
                                onValueChange = { receiverPhone = it },
                                placeholder = { Text("412 1234567") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White, RoundedCornerShape(14.dp))
                            )
                        }

                        Text(
                            text = "¿QUÉ PAQUETE O ARTÍCULO ENVÍAS?",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                        OutlinedTextField(
                            value = packageDetail,
                            onValueChange = { packageDetail = it },
                            placeholder = { Text("Ej: Sobre con documentos, llaves, caja pequeña...") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(14.dp))
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "remitente" to "Paga quien envía",
                                "destinatario" to "Paga quien recibe"
                            ).forEach { (key, label) ->
                                val active = freightPaidBy == key
                                Surface(
                                    color = if (active) ResolvitosSlate else Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, ResolvitosSlate),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { freightPaidBy = key }
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (active) BrightYellow else ResolvitosSlate,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Payment Method Selector + Primary CTA Button ("PEDIR TAXI →" / "ENVIAR ENCOMIENDA →")
        item {
            val activeQuote = selectedDriverQuote ?: driverQuotes.firstOrNull()
            val finalFareUsd = activeQuote?.calculatedFareUsd ?: if (selectedVehicleType == "moto") 2.00 else 3.00

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPaymentMethodModal = true }
                            .testTag("select_payment_method_row")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MÉTODO DE PAGO AL CONDUCTOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "💳 $paymentMethod",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = ResolvitosSlate
                                )
                            }
                            Surface(color = ResolvitosSlate, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Cambiar",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightYellow,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.ALERT_LOOP)
                            viewModel.requestRideOrMandado(
                                selectedQuote = activeQuote,
                                customOfferedUsd = finalFareUsd,
                                packageDescription = if (selectedServiceCategory == "encomienda") packageDetail else "",
                                senderInfo = "Louis Naranjo (+58 4127786837)",
                                receiverInfo = if (selectedServiceCategory == "encomienda") "$receiverName (+58 $receiverPhone)" else "",
                                fletePagadoPor = freightPaidBy,
                                paymentMethod = paymentMethod
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("request_ride_button")
                    ) {
                        Text(
                            text = if (selectedServiceCategory == "encomienda") {
                                "ENVIAR ENCOMIENDA • ${formatDualPrice(finalFareUsd, bcvRate)} →"
                            } else {
                                "PEDIR TAXI • ${formatDualPrice(finalFareUsd, bcvRate)} →"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showPaymentMethodModal) {
        RidePaymentMethodSelectorModal(
            selectedMethod = paymentMethod,
            onSelectMethod = {
                paymentMethod = it
                showPaymentMethodModal = false
            },
            onDismiss = { showPaymentMethodModal = false }
        )
    }
}

@Composable
private fun RidePaymentMethodSelectorModal(
    selectedMethod: String,
    onSelectMethod: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val methods = listOf(
        Triple("Pago Móvil al Conductor", "Transferencia inmediata Bs. a tasa BCV", "📱"),
        Triple("Efectivo Divisas ($)", "Pago en dólares en efectivo al conductor", "💵"),
        Triple("Efectivo Bolívares (BCV)", "Pago en bolívares en efectivo", "💴")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp),
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
                    Column {
                        Text(
                            text = "Método de Pago",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "Selecciona cómo pagarás al conductor",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                methods.forEach { (title, subtitle, emoji) ->
                    val isSelected = selectedMethod == title
                    Surface(
                        color = if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ResolvitosSlate else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectMethod(title) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(emoji, fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusEmerald)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Flow 3 (Video 6): 4-Step Wizard "Muchacho e' Mandao" + Live Subasta de Tarifas
 */
@Composable
private fun MuchachoEMandaoWizardScreen(
    viewModel: ResolvitosViewModel,
    bcvRate: Double,
    origin: MapPointPreset,
    destination: MapPointPreset,
    mapProvider: MapProviderType,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    var mandadoDescription by remember { mutableStateOf("Comprar 2 bolsas de hielo, 1 refresco de 2L y retirar factura en farmacia") }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var recordedSeconds by remember { mutableLongStateOf(0L) }
    var attachedPhotoUri by remember { mutableStateOf("") }

    var stopsMode by remember { mutableStateOf("exact") } // "exact" or "driver_suggest"
    val stopsList = remember { mutableStateListOf("Farmacia Central — Av. Miranda", "Bodegón El Llanero — Centro") }
    var newStopName by remember { mutableStateOf("") }
    var newStopAddress by remember { mutableStateOf("") }

    var withPassenger by remember { mutableStateOf(false) }
    var deliveryMode by remember { mutableStateOf("gps") } // "gps" or "custom_map"

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedPhotoUri = uri.toString()
        }
    }

    LaunchedEffect(isRecordingAudio) {
        if (isRecordingAudio) {
            while (isRecordingAudio) {
                delay(1000L)
                recordedSeconds += 1L
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Wizard Top Header
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
                    color = ResolvitosSlate,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            if (currentStep > 1) currentStep -= 1 else onBack()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = BrightYellow)
                    }
                }
                Column {
                    Text(
                        text = "Muchacho e' Mandao",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "PASO $currentStep DE 4 • DILIGENCIAS A TU MEDIDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Surface(
                color = ResolvitosSlate,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "$currentStep / 4",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = BrightYellow,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }

        // 4-Step Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (s in 1..4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (s <= currentStep) ResolvitosSlate else Color(0xFFE2E8F0))
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (currentStep) {
                    1 -> {
                        Text(
                            text = "¿Qué necesitas que hagamos por ti?",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "DESCRIBE TU MANDADO O DILIGENCIA *",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF64748B)
                        )
                        OutlinedTextField(
                            value = mandadoDescription,
                            onValueChange = { mandadoDescription = it },
                            placeholder = { Text("Ej: Comprar hielo, retirar encomienda, pagar recibo...") },
                            minLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // NOTA DE VOZ (OPCIONAL)
                        Text(
                            text = "NOTA DE VOZ (OPCIONAL)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isRecordingAudio) StatusCoral else ResolvitosSlate),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (recordedSeconds > 0 && !isRecordingAudio) Icons.Default.PlayArrow else Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = if (isRecordingAudio) Color.White else BrightYellow
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = when {
                                                isRecordingAudio -> "Grabando nota de voz... 0:0${recordedSeconds}s"
                                                recordedSeconds > 0 -> "Nota de voz grabada (0:0${recordedSeconds}s)"
                                                else -> "Grabar instrucciones por voz"
                                            },
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = "Explica los detalles rápidamente al piloto",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (isRecordingAudio) {
                                            isRecordingAudio = false
                                        } else {
                                            recordedSeconds = 5L
                                            isRecordingAudio = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ResolvitosSlate,
                                        contentColor = BrightYellow
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (recordedSeconds > 0) "Regrabar" else "Grabar",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // FOTO O REFERENCIA DEL PRODUCTO (OPCIONAL)
                        Text(
                            text = "FOTO O REFERENCIA DEL PRODUCTO (OPCIONAL)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF64748B)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Galería / Cámara", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = { attachedPhotoUri = "sample://referencia_producto_mandado.jpg" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ResolvitosSlate,
                                    contentColor = BrightYellow
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (attachedPhotoUri.isNotBlank()) "✓ Foto Lista" else "Adjuntar Demo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    2 -> {
                        Text(
                            text = "¿Tienes los lugares exactos de compra definidos?",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = if (stopsMode == "exact") Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (stopsMode == "exact") 2.dp else 1.dp,
                                    color = if (stopsMode == "exact") ResolvitosSlate else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { stopsMode = "exact" }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "📍 Sí, yo sé dónde comprar",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "Indica los comercios o paradas exactas para el piloto",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Surface(
                                color = if (stopsMode == "driver_suggest") Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (stopsMode == "driver_suggest") 2.dp else 1.dp,
                                    color = if (stopsMode == "driver_suggest") ResolvitosSlate else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { stopsMode = "driver_suggest" }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "🧭 No estoy seguro / Que el conductor sugiera",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "El piloto buscará el mejor lugar disponible en la zona",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        if (stopsMode == "exact") {
                            stopsList.forEachIndexed { idx, stop ->
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Parada #${idx + 1}: $stop",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ResolvitosSlate,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newStopName,
                                    onValueChange = { newStopName = it },
                                    placeholder = { Text("Comercio") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = newStopAddress,
                                    onValueChange = { newStopAddress = it },
                                    placeholder = { Text("Referencia") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    if (newStopName.isNotBlank()) {
                                        stopsList.add("$newStopName — ${newStopAddress.ifBlank { "Centro" }}")
                                        newStopName = ""
                                        newStopAddress = ""
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Agregar otra parada", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    3 -> {
                        Text(
                            text = "¿Se transportará a alguna persona durante este mandado?",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )

                        Surface(
                            color = if (!withPassenger) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (!withPassenger) 2.dp else 1.dp,
                                color = if (!withPassenger) ResolvitosSlate else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { withPassenger = false }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "📦 No, solo diligencias",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = "El conductor realiza todo el mandado y entrega en tu puerta",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            color = if (withPassenger) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (withPassenger) 2.dp else 1.dp,
                                color = if (withPassenger) ResolvitosSlate else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { withPassenger = true }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🧑 Sí, irá un pasajero",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = "Te buscamos o llevamos a alguien durante la diligencia",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    4 -> {
                        Text(
                            text = "¿Dónde finaliza el mandado?",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ResolvitosSlate
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "gps" to "📍 Mi ubicación GPS",
                                "custom_map" to "🗺️ Otra dirección"
                            ).forEach { (mode, label) ->
                                val active = deliveryMode == mode
                                Surface(
                                    color = if (active) ResolvitosSlate else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { deliveryMode = mode }
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (active) BrightYellow else ResolvitosSlate,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }

                        ModularInteractiveMapView(
                            origin = origin,
                            destination = destination,
                            distanceKm = viewModel.currentRouteDistanceKm(),
                            etaMinutes = 6,
                            provider = mapProvider,
                            onTapMapCoordinate = { nx, ny -> viewModel.setDestinationFromMapTap(nx, ny) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        )

                        // Dark Summary Box matching Video 6 ("RESUMEN DE TU SOLICITUD")
                        Surface(
                            color = ResolvitosSlate,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "RESUMEN DE TU SOLICITUD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightYellow
                                )
                                Text(
                                    text = "• TAREA: $mandadoDescription",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "• PARADAS: ${if (stopsMode == "exact") "${stopsList.size} definidas" else "Sugeridas por piloto"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "• PASAJERO: ${if (withPassenger) "Sí (1 persona)" else "No, solo diligencias"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "• ENTREGA FINAL: ${destination.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusEmerald
                                )
                            }
                        }
                    }
                }

                // Bottom Wizard Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(0.4f)
                                .height(52.dp)
                        ) {
                            Text("Atrás", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < 4) {
                                currentStep += 1
                            } else {
                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.ALERT_LOOP)
                                viewModel.requestRideOrMandado(
                                    selectedQuote = null,
                                    customOfferedUsd = 2.50,
                                    packageDescription = mandadoDescription,
                                    senderInfo = if (stopsMode == "exact") stopsList.joinToString(" -> ") else "Sugerido por Piloto",
                                    receiverInfo = destination.name,
                                    paymentMethod = "Pago Móvil al Conductor",
                                    voiceNoteDurationSec = recordedSeconds,
                                    productPhotoUri = attachedPhotoUri,
                                    stopsSuggestionMode = stopsMode,
                                    withPassenger = withPassenger
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("mandado_wizard_next_button")
                    ) {
                        Text(
                            text = if (currentStep < 4) {
                                "Siguiente →"
                            } else {
                                "⚡ INICIAR AHORA (RECIBIR OFERTAS)"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Live Ride Tracking, Subasta de Tarifas, Driver Profile, Pago Móvil Copy Card,
 * Post-Trip Capture Upload Conciliation, and Star Rating (Videos 4, 5, 6, 7, 8)
 */
@Composable
private fun ClientActiveRideTrackingAndConciliationScreen(
    request: TransportRequest,
    bids: List<TransportBid>,
    bcvRate: Double,
    mapProvider: MapProviderType,
    onSwitchMapProvider: (MapProviderType) -> Unit,
    onAcceptBid: (TransportBid) -> Unit,
    onSimulateIncomingBid: () -> Unit,
    onCancelRequest: () -> Unit,
    onOpenChat: () -> Unit,
    onSubmitPaymentCapture: (String, String) -> Unit,
    onSubmitRating: (Long, String, List<String>) -> Unit,
    onBackToHub: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var captureUri by remember(request.paymentCaptureUri) { mutableStateOf(request.paymentCaptureUri) }
    var paymentRefInput by remember(request.paymentReference) { mutableStateOf(request.paymentReference) }
    var selectedStars by remember(request.rating) { mutableLongStateOf(if (request.rating > 0) request.rating else 5L) }
    var reviewComment by remember(request.reviewComment) { mutableStateOf(request.reviewComment) }
    val selectedTags = remember { mutableStateListOf("Excelente servicio", "Llegó rápido") }

    val capturePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            captureUri = uri.toString()
        }
    }

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

    // Simulated live driver position along the route
    val driverPosition = when (request.status) {
        "accepted" -> Pair(
            request.originLat + 0.0035,
            request.originLng - 0.0030
        )
        "arrived" -> Pair(request.originLat, request.originLng)
        "in_transit" -> Pair(
            (request.originLat + request.destinationLat) / 2.0,
            (request.originLng + request.destinationLng) / 2.0
        )
        "completed" -> Pair(request.destinationLat, request.destinationLng)
        else -> null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Live Map Header with Camera Pills ("Mi Posición", "Chofer", "Ver Ambos")
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ModularInteractiveMapView(
                    origin = originPreset,
                    destination = destPreset,
                    distanceKm = request.distanceKm,
                    etaMinutes = maxOf(3, (request.distanceKm * 2.0).toInt()),
                    provider = mapProvider,
                    driverLocation = driverPosition,
                    driverLabel = request.assignedDriverName.ifBlank { "Piloto" },
                    showCameraPills = request.status in listOf("accepted", "arrived", "in_transit"),
                    onTapMapCoordinate = { _, _ -> },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ResolvitosSlate.copy(alpha = 0.88f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onBackToHub() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }

                    Surface(
                        color = ResolvitosSlate.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, BrightYellow),
                        modifier = Modifier.clickable {
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
        }

        // 2. State-Specific Bottom Cards
        when {
            // Flow 3 Live Auction ("SUBASTA DE TARIFAS — Muchacho e' Mandao", Video 6)
            request.serviceCategory == "mandado" && request.status == "searching" -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BrightYellow.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                                Column {
                                    Text(
                                        text = "Muchacho e' Mandao",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "SUBASTA DE TARIFAS EN VIVO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrightYellow
                                    )
                                }
                                CircularProgressIndicator(
                                    color = BrightYellow,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "TU MANDADO SOLICITADO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = request.packageDescription.ifBlank { "Diligencia personalizada en la ciudad" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = "OFERTAS DE PILOTOS (${bids.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow
                            )

                            if (bids.isEmpty()) {
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Esperando ofertas de conductores...",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Los conductores cercanos están revisando los detalles de tu mandado para enviarte su tarifa.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.Center
                                        )
                                        OutlinedButton(
                                            onClick = onSimulateIncomingBid,
                                            border = BorderStroke(1.dp, BrightYellow),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightYellow)
                                        ) {
                                            Text("Recibir Oferta de Piloto Ahora", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                bids.forEach { bid ->
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = bid.driverName,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = ResolvitosSlate
                                                )
                                                Text(
                                                    text = "${bid.vehicleModel} (${bid.vehiclePlate}) • Llega en ~${bid.estimatedEtaMinutes} min",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF475569)
                                                )
                                                Text(
                                                    text = formatDualPrice(bid.offeredPriceUsd, bcvRate),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = StatusEmerald
                                                )
                                            }
                                            Button(
                                                onClick = { onAcceptBid(bid) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = StatusEmerald,
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Aceptar", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { showCancelConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusCoral,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("✕ CANCELAR MANDADO", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Searching for driver (Pasajeros / Encomienda / Transporte Rápido)
            request.status == "searching" -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = ResolvitosSlate,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Buscando tu transporte...",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "Notificando a los conductores más cercanos en el radar (Cambiar a App Conductor para aceptar)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            Text("🟢 Recogida: ${request.originAddress}", fontSize = 12.sp, color = Color(0xFF475569))
                            Text("🏁 Destino: ${request.destinationAddress}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ResolvitosSlate)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tarifa ofrecida:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = formatDualPrice(request.priceUsd, bcvRate),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = StatusEmerald
                                )
                            }

                            Button(
                                onClick = { showCancelConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusCoral,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("cancel_searching_ride_button")
                            ) {
                                Text("✕ Cancelar Solicitud", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Driver Assigned ("accepted", "arrived", "in_transit") — Videos 4 & 5
            request.status in listOf("accepted", "arrived", "in_transit") -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Status Banner
                            Surface(
                                color = when (request.status) {
                                    "arrived" -> StatusEmerald
                                    "in_transit" -> Color(0xFF1D4ED8)
                                    else -> ResolvitosSlate
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = when (request.status) {
                                            "arrived" -> "📍 ¡El transporte está afuera!"
                                            "in_transit" -> "🚀 Viaje en Curso"
                                            else -> "🛵 Conductor en Camino • Llega en ~4 min"
                                        },
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (request.status == "accepted") BrightYellow else Color.White
                                    )
                                    Text(
                                        text = when (request.status) {
                                            "arrived" -> "Tu conductor te espera en el punto de recogida A"
                                            "in_transit" -> "En camino hacia ${request.destinationAddress.take(32)}"
                                            else -> "Preparando llegada al punto de recogida"
                                        },
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            // Driver Profile & Vehicle Info Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(ResolvitosSlate),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = request.assignedDriverName.take(1).ifBlank { "L" },
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp,
                                            color = BrightYellow
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = request.assignedDriverName.ifBlank { "Louis Naranjo" },
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = "★ 5.0 (42 viajes) • CONDUCTOR VERIFICADO",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD97706)
                                        )
                                        Text(
                                            text = request.assignedVehicleInfo.ifBlank { "Empire Keeway Horse 150 • Placa AB123CD" },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }
                            }

                            // Comfort Badges (Aire A/A, Buena Música, Wi-Fi)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("❄️ Aire A/A", "🎵 Buena Música", "📶 Wi-Fi").forEach { badge ->
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = badge,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ResolvitosSlate,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Chat & Call Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenChat,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrightYellow,
                                        contentColor = ResolvitosSlate
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("client_open_ride_chat_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chat con el Conductor", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }

                                IconButton(
                                    onClick = onOpenChat,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(StatusEmerald)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Llamar", tint = Color.White)
                                }
                            }

                            // If Encomienda, show Ficha de Encomienda / Paquete (Video 5)
                            if (request.serviceCategory == "encomienda") {
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "📦 Ficha de Encomienda / Paquete",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = "DESTINATARIO: ${request.receiverInfo.ifBlank { "María Pérez (+58 4121234567)" }}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ResolvitosSlate
                                        )
                                        Text(
                                            text = "DETALLE: ${request.packageDescription.ifBlank { "Sobre sellado con documentos" }}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }

                            // Purple Gradient Card: "Pago Móvil al Conductor" with "Copiar Todo" (Videos 4 & 5)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF4C1D95), Color(0xFF6D28D9))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📱 Pago Móvil al Conductor",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Surface(
                                            color = BrightYellow,
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier.clickable {
                                                val copyText = "Banesco (0134) • 04127786837 • V-26177935 • Monto: ${formatBs(request.priceUsd, bcvRate)}"
                                                clipboardManager.setText(AnnotatedString(copyText))
                                                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.PUSH_CHIME)
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ResolvitosSlate, modifier = Modifier.size(12.dp))
                                                Text(
                                                    text = "Copiar Todo",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = ResolvitosSlate
                                                )
                                            }
                                        }
                                    }
                                    Text("BANCO: Banesco (0134)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                    Text("TELÉFONO: 04127786837  •  CÉDULA: V-26177935", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                    Text(
                                        text = "MONTO EXACTO (BCV): ${formatBs(request.priceUsd, bcvRate)} (${formatUsd(request.priceUsd)})",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = BrightYellow
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Completed Ride -> Post-Trip Payment Conciliation & Star Rating (Videos 4 & 5, 01:36 - 02:27)
            request.status == "completed" -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Top Completion Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(StatusEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusEmerald, modifier = Modifier.size(28.dp))
                                }
                                Column {
                                    Text(
                                        text = "¡Llegamos a tu destino!",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "Viaje finalizado • Conciliación y Calificación",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            // STEP A: PAYMENT CONCILIATION
                            when (request.paymentStatus) {
                                "pending" -> {
                                    Surface(
                                        color = Color(0xFFFFFBEB),
                                        shape = RoundedCornerShape(18.dp),
                                        border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
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
                                                    text = "⏳ Pago Pendiente de Conciliación",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp,
                                                    color = ResolvitosSlate
                                                )
                                                Text(
                                                    text = formatDualPrice(request.priceUsd, bcvRate),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp,
                                                    color = StatusEmerald
                                                )
                                            }

                                            Text(
                                                text = "1. SUBE EL CAPTURE DEL PAGO (OBLIGATORIO)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ResolvitosSlate
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedButton(
                                                    onClick = {
                                                        capturePickerLauncher.launch(
                                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Elegir Capture", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }

                                                Button(
                                                    onClick = {
                                                        captureUri = "capture://banesco_pago_movil_${System.currentTimeMillis()}.png"
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = ResolvitosSlate,
                                                        contentColor = BrightYellow
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.testTag("attach_demo_capture_button")
                                                ) {
                                                    Text(
                                                        text = if (captureUri.isNotBlank()) "✓ Capture Adjunto" else "Adjuntar Capture",
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "2. NÚMERO DE REFERENCIA (OPCIONAL)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ResolvitosSlate
                                            )
                                            OutlinedTextField(
                                                value = paymentRefInput,
                                                onValueChange = { paymentRefInput = it },
                                                placeholder = { Text("Ej: 123456") },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                    .testTag("ride_payment_reference_input")
                                            )

                                            Button(
                                                onClick = {
                                                    val finalCapture = captureUri.ifBlank { "capture://comprobante_pago_movil.png" }
                                                    val finalRef = paymentRefInput.ifBlank { "123456" }
                                                    onSubmitPaymentCapture(finalRef, finalCapture)
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = StatusEmerald,
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .testTag("submit_payment_capture_button")
                                            ) {
                                                Text(
                                                    text = "✓ ENVIAR COMPROBANTE DE PAGO",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                "reported" -> {
                                    Surface(
                                        color = Color(0xFFFFFBEB),
                                        shape = RoundedCornerShape(18.dp),
                                        border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "⏳ Pago Reportado",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = ResolvitosSlate
                                                )
                                                Surface(color = ResolvitosSlate, shape = RoundedCornerShape(50)) {
                                                    Text(
                                                        text = "POR CONCILIAR",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = BrightYellow,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Capture enviado (Ref: #${request.paymentReference.ifBlank { "123456" }}) • Esperando confirmación del conductor en su Centro de Comandas.",
                                                fontSize = 11.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                }

                                "confirmed" -> {
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(18.dp),
                                        border = BorderStroke(1.5.dp, StatusEmerald),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusEmerald)
                                            Column {
                                                Text(
                                                    text = "¡Pago Confirmado por el Conductor!",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF065F46)
                                                )
                                                Text(
                                                    text = "Comprobante conciliado exitosamente • Ref #${request.paymentReference}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF047857)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            // STEP B: 1-5 STAR RATING & TAG CHIPS (Video 4, 02:13 - 02:27)
                            Text(
                                text = "¿Cómo estuvo tu viaje con ${request.assignedDriverName.ifBlank { "Louis Naranjo" }}?",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = ResolvitosSlate,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (star in 1L..5L) {
                                    IconButton(onClick = { selectedStars = star }) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "$star estrellas",
                                            tint = if (star <= selectedStars) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }

                            val availableTags = if (selectedStars >= 4L) {
                                listOf("Excelente servicio", "Conducción segura", "Vehículo limpio", "Muy amable", "Llegó rápido")
                            } else {
                                listOf("Llegó tarde", "Conducción brusca", "Vehículo sucio", "Ruta incorrecta")
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                availableTags.forEach { tag ->
                                    val isSelected = tag in selectedTags
                                    Surface(
                                        color = if (isSelected) ResolvitosSlate else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.clickable {
                                            if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                        }
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BrightYellow else ResolvitosSlate,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = reviewComment,
                                onValueChange = { reviewComment = it },
                                placeholder = { Text("Escribe un comentario opcional sobre el viaje...") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    onSubmitRating(
                                        selectedStars,
                                        reviewComment.ifBlank { "Excelente servicio" },
                                        selectedTags.toList()
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
                                    .testTag("submit_ride_rating_button")
                            ) {
                                Text(
                                    text = "Enviar Calificación",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancellation Modal ("⚠️ ¿Cancelar viaje? Sin penalidad ($0.00)", Video 7)
    if (showCancelConfirmDialog) {
        Dialog(onDismissRequest = { showCancelConfirmDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCoral, modifier = Modifier.size(28.dp))
                    }
                    Text(
                        text = "¿Cancelar viaje?",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Estás a punto de cancelar tu solicitud activa. Aún no se ha asignado conductor.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "✓ Sin penalidad ($0.00)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = StatusEmerald,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCancelConfirmDialog = false },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Volver", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                showCancelConfirmDialog = false
                                onCancelRequest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusCoral,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("confirm_cancel_ride_button")
                        ) {
                            Text("Sí, cancelar", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
