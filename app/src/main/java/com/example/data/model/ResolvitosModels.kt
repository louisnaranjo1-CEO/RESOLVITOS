package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

const val TENANT_KEY = "resolvitos_live"

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val phone: String = "4127786837",
    val cedulaType: String = "V",
    val cedulaNumber: String = "26177935",
    val birthDate: String = "17/10/1997",
    val gender: String = "MASCULINO",
    val city: String = "Calabozo",
    val role: String = "admin",
    val activeRole: String = "client",
    val enabledRoles: List<String> = listOf("client", "driver", "business", "admin"),
    val points: Long = 266L,
    val referralCode: String = "RESOLV-2026",
    val biometricLockEnabled: Boolean = false,
    val adminSecurityPin: String = "2026",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SystemSettings(
    @DocumentId val id: String = "",
    val bcvRate: Double = 886.23,
    val commissionPercent: Double = 10.0,
    val motoBaseFareUsd: Double = 1.50,
    val motoPerKmUsd: Double = 0.13,
    val taxiBaseFareUsd: Double = 2.50,
    val taxiPerKmUsd: Double = 0.45,
    val termsClient: String = DEFAULT_TERMS_CLIENT,
    val termsDriver: String = DEFAULT_TERMS_DRIVER,
    val termsBusiness: String = DEFAULT_TERMS_BUSINESS,
    val updatedBy: String = "system",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Comercio(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val ownerId: String = "",
    val name: String = "",
    val category: String = "Restaurantes",
    val city: String = "Calabozo",
    val address: String = "Calabozo, Guárico",
    val distanceLabel: String = "457 m",
    val rating: Double = 5.0,
    val deliveryFeeUsd: Double = 0.0,
    val freeShippingThresholdUsd: Double = 20.0,
    val etaMinutes: Long = 35L,
    val followersCount: Long = 1L,
    val hasCashea: Boolean = true,
    val hasTwoByThree: Boolean = true,
    @get:PropertyName("isActive") @PropertyName("isActive") val isActive: Boolean = true,
    @get:PropertyName("isApproved") @PropertyName("isApproved") val isApproved: Boolean = true,
    val workingHours: String = "08:00 AM - 11:00 PM",
    val pagoMovilInfo: String = "Banesco (0134) • 0412-7786837 • J-40991122-3",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ProductPresentation(
    val label: String = "250GR",
    val priceUsd: Double = 1.00
)

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val comercioId: String = "",
    val comercioName: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val priceUsd: Double = 0.0,
    val category: String = "Supermercados",
    val stock: Long = 40L,
    @get:PropertyName("isAvailable") @PropertyName("isAvailable") val isAvailable: Boolean = true,
    val freeShipping: Boolean = true,
    val hasPresentations: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val clientId: String = "",
    val clientName: String = "",
    val comercioId: String = "",
    val comercioName: String = "",
    val itemsSummary: String = "",
    val subtotalUsd: Double = 0.0,
    val deliveryFeeUsd: Double = 0.0,
    val totalUsd: Double = 0.0,
    val bcvRateApplied: Double = 886.23,
    val deliveryType: String = "delivery", // delivery, pickup, mesa
    val deliveryAddress: String = "Ubicación GPS confirmada - Calabozo",
    val paymentMethod: String = "Pago Móvil",
    val paymentReference: String = "",
    val paymentStatus: String = "verified", // pending_verification, verified
    val status: String = "pending", // pending, accepted, preparing, ready, dispatched, delivered, cancelled
    val cancelReason: String = "",
    val stockConfirmed: Boolean = true,
    val isExternalDelivery: Boolean = true,
    val transportRequestId: String = "",
    val deliveryDriverId: String = "",
    val driverName: String = "Simón Bolívar",
    val vehiclePlate: String = "AB123CD",
    val pointsEarned: Long = 20L,
    val rating: Long = 5L,
    val reviewComment: String = "Excelente",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DriverProfile(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val driverId: String = "",
    val name: String = "",
    val phone: String = "4127786837",
    val cedula: String = "V-26177935",
    val birthDate: String = "17/10/1997",
    val vehicleType: String = "moto", // "moto" (Moto Taxi + Delivery) or "taxi_economico" (Taxi Económico)
    val vehicleModel: String = "Bera SBR 150",
    val vehiclePlate: String = "AB9K12M",
    @get:PropertyName("isOnline") @PropertyName("isOnline") val isOnline: Boolean = true,
    val availability: String = "disponible", // disponible, ocupado, fuera_de_linea
    val baseFareUsd: Double = 1.50,
    val baseIncludedKm: Double = 6.0,
    val ratePerKmUsd: Double = 0.13,
    val minimumFareUsd: Double = 1.50,
    val nightBaseFareUsd: Double = 2.00,
    val nightRatePerKmUsd: Double = 0.20,
    val deliveryFlatFeeUsd: Double = 1.50,
    val hasThermalBag: Boolean = true,
    val rating: Double = 4.9,
    val completedTripsCount: Long = 15L,
    val latitude: Double = 8.9242,
    val longitude: Double = -67.4293,
    val commissionDebtUsd: Double = 0.75,
    val pagoMovilInfo: String = "Mercantil (0105) • 0412-7786837 • V-26177935",
    val status: String = "approved",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TransportRequest(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val clientId: String = "",
    val clientName: String = "",
    val serviceCategory: String = "transport", // transport, delivery, mandado, encomienda, rapido
    val vehicleType: String = "moto", // "moto" or "taxi_economico"
    val originAddress: String = "",
    val originLat: Double = 8.9242,
    val originLng: Double = -67.4293,
    val destinationAddress: String = "",
    val destinationLat: Double = 8.9182,
    val destinationLng: Double = -67.4311,
    val distanceKm: Double = 2.3,
    val priceUsd: Double = 1.50,
    val bcvRateApplied: Double = 886.23,
    val status: String = "searching", // searching, accepted, arrived, in_transit, completed, cancelled
    val assignedDriverId: String = "",
    val assignedDriverName: String = "",
    val assignedVehicleInfo: String = "",
    val packageDescription: String = "",
    val senderInfo: String = "",
    val receiverInfo: String = "",
    val fletePagadoPor: String = "remitente",
    val linkedOrderId: String = "",
    val paymentMethod: String = "Pago Móvil al Conductor",
    val paymentStatus: String = "pending", // pending, reported, verified
    val paymentReference: String = "",
    val paymentCaptureUri: String = "",
    val rating: Long = 0L,
    val reviewComment: String = "",
    val reviewTags: String = "",
    val voiceNoteDurationSec: Long = 0L,
    val productPhotoUri: String = "",
    val stopsSuggestionMode: String = "driver_suggests",
    val withPassenger: Boolean = false,
    val lostItemReport: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TransportBid(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val requestId: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val vehicleType: String = "moto", // "moto" or "taxi_economico"
    val vehicleModel: String = "",
    val vehiclePlate: String = "",
    val driverRating: Double = 4.9,
    val offeredPriceUsd: Double = 1.50,
    val estimatedEtaMinutes: Long = 4L,
    val hasThermalBag: Boolean = true,
    val status: String = "pending", // pending, accepted, rejected
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BusinessMission(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val comercioId: String = "",
    val comercioName: String = "",
    val creatorId: String = "",
    val title: String = "",
    val description: String = "",
    val targetPoints: Long = 100L,
    val targetOrders: Long = 2L,
    val pointsReward: Long = 50L,
    val badgeEmoji: String = "🏆",
    val rewardTitle: String = "",
    val rewardValueUsd: Double = 5.0,
    @get:PropertyName("isActive") @PropertyName("isActive") val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserMissionClaim(
    @DocumentId val id: String = "",
    val userId: String = "",
    val missionId: String = "",
    val comercioId: String = "",
    val comercioName: String = "",
    val rewardTitle: String = "",
    val couponCode: String = "",
    val status: String = "unlocked", // unlocked, redeemed
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserMissionProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val missionId: String = "",
    val comercioId: String = "",
    val comercioName: String = "",
    val missionTitle: String = "",
    val currentPoints: Long = 0L,
    val targetPoints: Long = 100L,
    val currentOrders: Long = 0L,
    val targetOrders: Long = 2L,
    val pointsReward: Long = 50L,
    @get:PropertyName("isCompleted") @PropertyName("isCompleted") val isCompleted: Boolean = false,
    val rewardClaimed: Boolean = false,
    val couponCode: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val pointsFraction: Float
        get() = if (targetPoints <= 0L) 1f else (currentPoints.toFloat() / targetPoints.toFloat()).coerceIn(0f, 1f)

    val ordersFraction: Float
        get() = if (targetOrders <= 0L) 1f else (currentOrders.toFloat() / targetOrders.toFloat()).coerceIn(0f, 1f)

    val overallProgressFraction: Float
        get() = maxOf(pointsFraction, ordersFraction)
}

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val contextId: String = "", // orderId or requestId
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "client",
    val text: String = "",
    val mediaType: String = "text", // text, image, audio, video, voip_call
    val mediaUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class RestaurantOperation(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val comercioId: String = "",
    val ownerId: String = "",
    val recordType: String = "table_qr", // table_qr, waiter, expense, cash_register
    val title: String = "",
    val details: String = "",
    val amountUsd: Double = 0.0,
    val status: String = "libre",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class EmergencyService(
    @DocumentId val id: String = "",
    val tenantKey: String = TENANT_KEY,
    val name: String = "",
    val category: String = "Salud",
    val scope: String = "Nacional",
    val phone: String = "911",
    val whatsapp: String = "+584120000911",
    val isAvailable24h: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val presentationLabel: String = "",
    val unitPriceUsd: Double = product.priceUsd,
    val notes: String = ""
)

data class MapPointPreset(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

val CITY_MAP_PRESETS = listOf(
    MapPointPreset("Centro de Calabozo", "Casco Central, Plaza Bolívar, Calabozo", 8.9242, -67.4293),
    MapPointPreset("Parque Rómulo Gallegos", "Av. Octavio Viana, Calabozo", 8.9182, -67.4311),
    MapPointPreset("Urb. Misión Arriba", "Av. Principal Misión Arriba, Calabozo", 8.9328, -67.4144),
    MapPointPreset("911 Grill & Paseo", "Av. 23 de Enero, Calabozo (457 m)", 8.9278, -67.4264),
    MapPointPreset("Vicario II Sector Centro", "Carrera 12 con Calle 5, Calabozo", 8.9485, -67.4020),
    MapPointPreset("Hospital Dr. Francisco Urdaneta", "Av. Miranda, Calabozo", 8.9110, -67.4480),
    MapPointPreset("Represa Generoso Campilongo", "Vía Paso Real, Calabozo (8.2 km)", 8.8850, -67.3810)
)

fun calculateDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val rawKm = earthRadiusKm * c * 1.32
    return max(0.8, (rawKm * 100.0).roundToInt() / 100.0)
}

data class BimonetaryFareBreakdown(
    val distanceKm: Double,
    val isNightShift: Boolean,
    val baseIncludedKm: Double,
    val extraDistanceKm: Double,
    val appliedBaseFareUsd: Double,
    val appliedRatePerKmUsd: Double,
    val extraDistanceChargeUsd: Double,
    val minimumFareUsd: Double,
    val totalUsd: Double,
    val bcvRate: Double,
    val baseFareBs: Double,
    val extraDistanceChargeBs: Double,
    val totalBs: Double
)

/**
 * Calculates the detailed bimonetary fare breakdown (USD + Bs. BCV) according to the driver's
 * configured Base Fare + Included Base Distance (e.g. 5..6 km) + Extra Rate per Km above base distance,
 * supporting both Day and Night shift tariffs for Moto Taxi and Taxi Económico.
 */
fun calculateBimonetaryFareBreakdown(
    driver: DriverProfile,
    distanceKm: Double,
    bcvRate: Double,
    isNightShift: Boolean = false
): BimonetaryFareBreakdown {
    val defaultIncludedKm = if (driver.vehicleType == "taxi_economico") 5.0 else 6.0
    val includedKm = if (driver.baseIncludedKm > 0.0) driver.baseIncludedKm else defaultIncludedKm
    val safeDistanceKm = max(0.8, (distanceKm * 100.0).roundToInt() / 100.0)
    val extraKm = ((max(0.0, safeDistanceKm - includedKm)) * 100.0).roundToInt() / 100.0

    val baseUsd = if (isNightShift && driver.nightBaseFareUsd > 0.0) {
        driver.nightBaseFareUsd
    } else {
        driver.baseFareUsd
    }
    val ratePerKmUsd = if (isNightShift && driver.nightRatePerKmUsd > 0.0) {
        driver.nightRatePerKmUsd
    } else {
        driver.ratePerKmUsd
    }

    val extraChargeUsd = ((extraKm * ratePerKmUsd) * 100.0).roundToInt() / 100.0
    val rawTotalUsd = baseUsd + extraChargeUsd
    val minUsd = if (isNightShift && driver.nightBaseFareUsd > driver.minimumFareUsd) {
        driver.nightBaseFareUsd
    } else {
        driver.minimumFareUsd
    }
    val finalUsd = ((max(minUsd, rawTotalUsd)) * 100.0).roundToInt() / 100.0
    val safeBcv = if (bcvRate > 0.0) bcvRate else 1.0

    return BimonetaryFareBreakdown(
        distanceKm = safeDistanceKm,
        isNightShift = isNightShift,
        baseIncludedKm = includedKm,
        extraDistanceKm = extraKm,
        appliedBaseFareUsd = baseUsd,
        appliedRatePerKmUsd = ratePerKmUsd,
        extraDistanceChargeUsd = extraChargeUsd,
        minimumFareUsd = minUsd,
        totalUsd = finalUsd,
        bcvRate = safeBcv,
        baseFareBs = ((baseUsd * safeBcv) * 100.0).roundToInt() / 100.0,
        extraDistanceChargeBs = ((extraChargeUsd * safeBcv) * 100.0).roundToInt() / 100.0,
        totalBs = ((finalUsd * safeBcv) * 100.0).roundToInt() / 100.0
    )
}

/**
 * Calculates the fare in USD according to the driver's configured Base Fare + Included Base Distance
 * + Extra Rate per Km above the base distance.
 */
fun calculateDriverFareUsd(
    driver: DriverProfile,
    distanceKm: Double,
    isNightShift: Boolean = false
): Double {
    return calculateBimonetaryFareBreakdown(
        driver = driver,
        distanceKm = distanceKm,
        bcvRate = 1.0,
        isNightShift = isNightShift
    ).totalUsd
}

fun formatUsd(amountUsd: Double): String =
    String.format(Locale.US, "$%.2f", amountUsd)

fun formatBs(amountUsd: Double, bcvRate: Double): String =
    String.format(Locale.US, "%.2f Bs", amountUsd * bcvRate)

fun formatDualPrice(amountUsd: Double, bcvRate: Double): String =
    "${formatUsd(amountUsd)} | ${formatBs(amountUsd, bcvRate)}"

const val DEFAULT_TERMS_CLIENT = """
# Términos y Condiciones — Usuarios y Clientes (Resolvitos Taxi Deliverys)
1. **Economía Bimonetaria Oficial BCV**: Todos los precios se muestran en Dólares (USD) y su equivalente automático en Bolívares (Bs.) sincronizado directamente con la tasa oficial vigente del Banco Central de Venezuela (BCV).
2. **Marketplace y Pagos**: Grupo Resolvitos VE C.A. conecta a clientes con comercios y conductores afiliados. Los clientes depositan sus pagos directamente en las cuentas bancarias o Pago Móvil de los comercios o conductores.
3. **Movilidad en Moto Taxi y Taxi Económico**: Al seleccionar tu destino en el mapa interactivo, el monto se calcula automáticamente según la tarifa base y tarifa por kilómetro excedente configurada por cada conductor.
4. **Programa de Puntos y Misiones por Negocio**: Acumula puntos automáticamente con cada compra o viaje y desbloquea premios configurables por cada negocio y la plataforma.
5. **Privacidad y Eliminación de Cuenta**: Puedes editar o eliminar tu cuenta en cualquier momento desde tu Perfil.
"""

const val DEFAULT_TERMS_DRIVER = """
# Términos y Condiciones — Conductores (Resolvitos Centro de Comandas)
1. **Modalidades Autorizadas**: En Resolvitos operan exclusivamente dos categorías: **Moto Taxi** (traslado de pasajeros, Delivery de comercios y Mandados) y **Taxi Económico** (traslado de pasajeros y encomiendas).
2. **Tarifa Base + Km Excedente**: El piloto define cuántos kilómetros incluye su tarifa base de arranque (entre 1 y 6 km) y cuánto cobra por cada kilómetro adicional en turno diurno y nocturno.
3. **Garantía Resolvitos 48h y Comisiones**: Tus ganancias están respaldadas. La comisión de plataforma se concilia al finalizar la jornada o los viernes.
"""

const val DEFAULT_TERMS_BUSINESS = """
# Términos y Condiciones — Comercios Aliados (Resolvitos Negocios Business Hub)
1. **Carta de Productos y Stock**: El comercio gestiona su menú, presentaciones, precios en USD y disponibilidad en tiempo real.
2. **Red de Entregas Resolvitos**: Los pedidos de delivery son despachados con la flota local de Moto Taxis con seguimiento GPS en tiempo real.
3. **Fidelización y Misiones del Negocio**: El administrador del negocio configura misiones y premios desbloqueables para premiar a sus clientes frecuentes.
"""
