package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapPointPreset
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.StatusEmerald
import java.util.Locale

/**
 * Pluggable Map Provider architecture:
 * Default is GOOGLE_MAPS_PREVIEW, and can be switched to MAPBOX_PREVIEW at any time.
 */
enum class MapProviderType(val displayName: String) {
    GOOGLE_MAPS_PREVIEW("Google Maps"),
    MAPBOX_PREVIEW("Mapbox")
}

object MapProviderConfig {
    var activeProvider: MapProviderType = MapProviderType.GOOGLE_MAPS_PREVIEW
}

data class StreetPoi(
    val name: String,
    val normX: Float,
    val normY: Float,
    val color: Color,
    val categoryIcon: String
)

private val CALABOZO_MAP_POIS = listOf(
    StreetPoi("Catedral Metropolitana", 0.34f, 0.36f, Color(0xFF0D9488), "⛪"),
    StreetPoi("Plaza Bolívar", 0.28f, 0.40f, Color(0xFF16A34A), "🌳"),
    StreetPoi("911 Grill & Paseo", 0.32f, 0.34f, Color(0xFFEA580C), "🍔"),
    StreetPoi("Parque Rómulo Gallegos", 0.25f, 0.49f, Color(0xFF15803D), "🌲"),
    StreetPoi("Urb. Misión Arriba", 0.50f, 0.26f, Color(0xFFD97706), "🏘️"),
    StreetPoi("Vicario II Sector Centro", 0.68f, 0.14f, Color(0xFF2563EB), "🏨"),
    StreetPoi("Hospital Urdaneta Delgado", 0.14f, 0.62f, Color(0xFFDC2626), "🏥"),
    StreetPoi("Represa Campilongo", 0.86f, 0.86f, Color(0xFF0284C7), "🌊")
)

private const val MAP_MIN_LAT = 8.8850
private const val MAP_LAT_SPAN = 0.0650
private const val MAP_MIN_LNG = -67.4480
private const val MAP_LNG_SPAN = 0.0680

@Composable
fun ModularInteractiveMapView(
    origin: MapPointPreset,
    destination: MapPointPreset,
    distanceKm: Double,
    etaMinutes: Int,
    provider: MapProviderType = MapProviderConfig.activeProvider,
    selectingOriginMode: Boolean = false,
    driverLocation: Pair<Double, Double>? = null,
    driverLabel: String = "Piloto",
    showCameraPills: Boolean = false,
    onTapMapCoordinate: (Float, Float) -> Unit = { _, _ -> },
    onRecenter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val modeLabel = if (selectingOriginMode) "📍 Toca el mapa: Origen A" else "🏁 Toca el mapa: Destino B"
    val bannerText = "${String.format(Locale.US, "%.2f", distanceKm)} km • ~$etaMinutes min • $modeLabel"
    ModularInteractiveMapView(
        originLat = origin.lat,
        originLng = origin.lng,
        destinationLat = destination.lat,
        destinationLng = destination.lng,
        hasDestination = true,
        vehicleType = "moto",
        showDriverControls = showCameraPills,
        driverStatusBanner = if (driverLocation != null) "$driverLabel • $bannerText" else bannerText,
        provider = provider,
        onMapTap = { lat, lng ->
            val normX = ((lng - MAP_MIN_LNG) / MAP_LNG_SPAN).toFloat().coerceIn(0.04f, 0.96f)
            val normY = (1.0 - ((lat - MAP_MIN_LAT) / MAP_LAT_SPAN)).toFloat().coerceIn(0.06f, 0.94f)
            onTapMapCoordinate(normX, normY)
        },
        modifier = modifier
    )
}

/**
 * Modular Interactive Street Map Component (Google Maps Preview / Mapbox swap-ready).
 * Renders the urban street grid, avenues, parks, river, POIs, Origin Pin (A), Destination Pin (B),
 * Live Driver Moto/Taxi marker, Zoom/Layer controls, and street route line.
 */
@Composable
fun ModularInteractiveMapView(
    originLat: Double,
    originLng: Double,
    destinationLat: Double?,
    destinationLng: Double?,
    hasDestination: Boolean,
    vehicleType: String = "moto",
    showDriverControls: Boolean = false,
    driverStatusBanner: String? = null,
    provider: MapProviderType = MapProviderConfig.activeProvider,
    onMapTap: ((Double, Double) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var cameraFocusMode by remember { mutableStateOf("both") } // "origin", "driver", "both"
    var isSatelliteMode by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableFloatStateOf(15f) }

    fun latLngToNorm(lat: Double, lng: Double): Offset {
        val nx = ((lng - MAP_MIN_LNG) / MAP_LNG_SPAN).toFloat().coerceIn(0.06f, 0.94f)
        val ny = (1.0f - ((lat - MAP_MIN_LAT) / MAP_LAT_SPAN).toFloat()).coerceIn(0.08f, 0.92f)
        return Offset(nx, ny)
    }

    val originNorm = latLngToNorm(originLat, originLng)
    val destNorm = if (hasDestination && destinationLat != null && destinationLng != null) {
        latLngToNorm(destinationLat, destinationLng)
    } else {
        null
    }

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .background(
                when {
                    isSatelliteMode -> Color(0xFF1B2E26)
                    provider == MapProviderType.GOOGLE_MAPS_PREVIEW -> Color(0xFFE8EAED)
                    else -> Color(0xFFF3EFE6)
                }
            )
            .pointerInput(onMapTap) {
                if (onMapTap != null) {
                    detectTapGestures { tapOffset ->
                        val nx = (tapOffset.x / size.width.toFloat()).coerceIn(0.04f, 0.96f)
                        val ny = (tapOffset.y / size.height.toFloat()).coerceIn(0.06f, 0.94f)
                        val tappedLat = MAP_MIN_LAT + (1.0 - ny) * MAP_LAT_SPAN
                        val tappedLng = MAP_MIN_LNG + nx * MAP_LNG_SPAN
                        onMapTap(tappedLat, tappedLng)
                    }
                }
            }
            .testTag("interactive_destination_map")
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Background city blocks
            drawRect(
                color = when {
                    isSatelliteMode -> Color(0xFF1D332A)
                    provider == MapProviderType.GOOGLE_MAPS_PREVIEW -> Color(0xFFE9EEF4)
                    else -> Color(0xFFF1EFEA)
                },
                size = size
            )

            // 2. Green Park Zones (Plaza Bolívar, Parque Rómulo Gallegos, Embalse)
            val parkColor = if (isSatelliteMode) Color(0xFF14532D) else Color(0xFFC8E6C9)
            drawRect(
                color = parkColor,
                topLeft = Offset(w * 0.22f, h * 0.34f),
                size = Size(w * 0.16f, h * 0.16f)
            )
            drawRect(
                color = if (isSatelliteMode) Color(0xFF166534) else Color(0xFFD1FAE5),
                topLeft = Offset(w * 0.62f, h * 0.10f),
                size = Size(w * 0.24f, h * 0.18f)
            )

            // 3. Río Guárico & Represa Generoso Campilongo water curves
            val waterColor = if (isSatelliteMode) Color(0xFF1E3A8A) else Color(0xFF93C5FD)
            val riverPath = Path().apply {
                moveTo(0f, h * 0.18f)
                quadraticTo(w * 0.18f, h * 0.12f, w * 0.32f, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(riverPath, color = waterColor)

            val damPath = Path().apply {
                moveTo(w * 0.72f, h)
                quadraticTo(w * 0.80f, h * 0.75f, w, h * 0.70f)
                lineTo(w, h)
                close()
            }
            drawPath(damPath, color = waterColor)

            // 4. Urban Streets Grid (Calles & Carreras of Calabozo)
            val streetColor = if (isSatelliteMode) Color(0xFF475569) else Color.White
            val mainAvenueColor = if (isSatelliteMode) Color(0xFFD97706) else Color(0xFFFDE047)

            val horizontalStreets = listOf(0.14f, 0.24f, 0.34f, 0.44f, 0.54f, 0.64f, 0.74f, 0.84f)
            horizontalStreets.forEach { yf ->
                drawLine(
                    color = streetColor,
                    start = Offset(0f, h * yf),
                    end = Offset(w, h * yf),
                    strokeWidth = 12f
                )
            }

            val verticalStreets = listOf(0.14f, 0.28f, 0.42f, 0.56f, 0.70f, 0.84f)
            verticalStreets.forEach { xf ->
                drawLine(
                    color = streetColor,
                    start = Offset(w * xf, 0f),
                    end = Offset(w * xf, h),
                    strokeWidth = 12f
                )
            }

            // Diagonal / Trunk Avenue (Av. Octavio Viana / Troncal 2)
            drawLine(
                color = mainAvenueColor,
                start = Offset(w * 0.68f, 0f),
                end = Offset(w * 0.18f, h),
                strokeWidth = 18f
            )
            drawLine(
                color = streetColor,
                start = Offset(w * 0.68f, 0f),
                end = Offset(w * 0.18f, h),
                strokeWidth = 9f
            )

            // 5. Street Labels
            val streetLabelStyle = TextStyle(
                color = if (isSatelliteMode) Color(0xFFE2E8F0) else Color(0xFF475569),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
            val streetNames = listOf(
                Triple("Av. Principal Misión Arriba", 0.46f, 0.22f),
                Triple("Av. 23 de Enero", 0.34f, 0.32f),
                Triple("Casco Central • Calle 5", 0.28f, 0.42f),
                Triple("Av. Octavio Viana", 0.22f, 0.52f),
                Triple("Av. Francisco de Miranda", 0.14f, 0.62f),
                Triple("Vía Paso Real / Represa", 0.58f, 0.76f)
            )
            streetNames.forEach { (name, sx, sy) ->
                drawText(
                    textMeasurer = textMeasurer,
                    text = name,
                    topLeft = Offset(w * sx, h * sy),
                    style = streetLabelStyle
                )
            }

            // 6. POI Labels
            CALABOZO_MAP_POIS.forEach { poi ->
                val px = w * poi.normX
                val py = h * poi.normY
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = poi.color,
                    radius = 6f,
                    center = Offset(px, py)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = poi.name,
                    topLeft = Offset(px + 9f, py - 7f),
                    style = TextStyle(
                        color = if (isSatelliteMode) Color.White else poi.color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            // 7. Route Polyline from A to B (Google Maps Blue or Vibrant Orange with border)
            val origPt = Offset(w * originNorm.x, h * originNorm.y)
            if (destNorm != null) {
                val destPt = Offset(w * destNorm.x, h * destNorm.y)
                val midPt = Offset(
                    (origPt.x + destPt.x) * 0.5f,
                    minOf(origPt.y, destPt.y) + kotlin.math.abs(origPt.y - destPt.y) * 0.35f
                )
                val routePath = Path().apply {
                    moveTo(origPt.x, origPt.y)
                    quadraticTo(midPt.x, midPt.y, destPt.x, destPt.y)
                }
                // Outer casing
                drawPath(
                    path = routePath,
                    color = if (provider == MapProviderType.GOOGLE_MAPS_PREVIEW) Color(0xFF1D4ED8) else Color(0xFFEA580C),
                    style = Stroke(width = 14f, cap = StrokeCap.Round)
                )
                // Inner highlight
                drawPath(
                    path = routePath,
                    color = if (provider == MapProviderType.GOOGLE_MAPS_PREVIEW) Color(0xFF60A5FA) else Color(0xFFFB923C),
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                    )
                )
            }
        }

        // 8. Origin Pin (Green "A" + Vehicle Circle Badge) and Destination Pin (Orange/Red "B")
        Box(modifier = Modifier.fillMaxSize()) {
            // Origin Marker A
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = (containerWidth * originNorm.x) - 18.dp,
                        y = (containerHeight * originNorm.y) - 34.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(StatusEmerald)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(y = (-4).dp)
                        .size(34.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(ResolvitosSlate)
                        .border(2.dp, BrightYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (vehicleType == "moto") Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = BrightYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Destination Marker B
            if (destNorm != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (containerWidth * destNorm.x) - 16.dp,
                            y = (containerHeight * destNorm.y) - 32.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFEA4335))
                            .border(2.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "B",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 9. Top Status Pill on Map ("2.45 km • ~6 min • 📍 Toca el mapa: Destino B")
        if (!driverStatusBanner.isNullOrBlank()) {
            Surface(
                color = ResolvitosSlate,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, BrightYellow),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 58.dp, end = 58.dp)
            ) {
                Text(
                    text = driverStatusBanner,
                    color = BrightYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        // 10. Floating Camera Pills on Top-Right (Mi Posición | Chofer | Ver Ambos)
        if (showDriverControls) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50),
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { cameraFocusMode = "origin" }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mi Posición",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = ResolvitosSlate
                        )
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50),
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { cameraFocusMode = "driver" }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚕", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chofer",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = ResolvitosSlate
                        )
                    }
                }

                Surface(
                    color = Color(0xFF2563EB),
                    shape = RoundedCornerShape(50),
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { cameraFocusMode = "both" }
                ) {
                    Text(
                        text = "Ver Ambos",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 11. Google Maps Preview Interactive Controls (Bottom-End: Satellite toggle + Zoom +/-)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = ResolvitosSlate.copy(alpha = 0.90f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BrightYellow.copy(alpha = 0.7f)),
                modifier = Modifier
                    .clickable { isSatelliteMode = !isSatelliteMode }
                    .testTag("toggle_map_satellite_mode")
            ) {
                Text(
                    text = if (isSatelliteMode) "🗺️ Mapa" else "🛰️ Satélite",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrightYellow,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { zoomLevel = (zoomLevel - 1f).coerceAtLeast(12f) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Alejar",
                            tint = ResolvitosSlate,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${zoomLevel.toInt()}x",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResolvitosSlate
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { zoomLevel = (zoomLevel + 1f).coerceAtMost(19f) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Acercar",
                            tint = ResolvitosSlate,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 12. Provider Watermark Badge (Google Maps Preview / Switchable to Mapbox)
        Surface(
            color = Color.White.copy(alpha = 0.94f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clickable {
                    MapProviderConfig.activeProvider =
                        if (MapProviderConfig.activeProvider == MapProviderType.GOOGLE_MAPS_PREVIEW) {
                            MapProviderType.MAPBOX_PREVIEW
                        } else {
                            MapProviderType.GOOGLE_MAPS_PREVIEW
                        }
                }
                .testTag("map_provider_watermark_badge")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (provider == MapProviderType.GOOGLE_MAPS_PREVIEW) Color(0xFFEA4335) else Color(0xFF2563EB),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${provider.displayName} Preview",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ResolvitosSlate
                )
            }
        }
    }
}
