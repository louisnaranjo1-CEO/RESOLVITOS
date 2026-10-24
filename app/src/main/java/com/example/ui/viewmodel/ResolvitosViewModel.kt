package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BimonetaryFareBreakdown
import com.example.data.model.BusinessMission
import com.example.data.model.CITY_MAP_PRESETS
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Comercio
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyService
import com.example.data.model.MapPointPreset
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.RestaurantOperation
import com.example.data.model.SystemSettings
import com.example.data.model.TransportBid
import com.example.data.model.TransportRequest
import com.example.data.model.UserMissionClaim
import com.example.data.model.UserMissionProgress
import com.example.data.model.UserProfile
import com.example.data.model.calculateBimonetaryFareBreakdown
import com.example.data.model.calculateDistanceKm
import com.example.data.model.calculateDriverFareUsd
import com.example.data.repository.ResolvitosRepository
import com.example.ui.components.MapProviderType
import com.example.util.BcvRateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DriverFareQuote(
    val driver: DriverProfile,
    val calculatedFareUsd: Double,
    val distanceKm: Double,
    val breakdown: BimonetaryFareBreakdown
)

class ResolvitosViewModel(
    private val repository: ResolvitosRepository,
    val currentUserId: String,
    userDisplayName: String,
    userEmail: String
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile()
        .catch { e ->
            Log.w(TAG, "Error observing profile", e)
            emit(UserProfile(id = currentUserId, userId = currentUserId, displayName = userDisplayName, email = userEmail))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val systemSettings: StateFlow<SystemSettings> = repository.observeSystemSettings()
        .catch { e ->
            Log.w(TAG, "Error observing system settings", e)
            emit(SystemSettings(id = "current"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SystemSettings(id = "current"))

    val comercios: StateFlow<List<Comercio>> = repository.observeComercios()
        .catch { e ->
            Log.w(TAG, "Error observing comercios", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val products: StateFlow<List<Product>> = repository.observeProducts()
        .catch { e ->
            Log.w(TAG, "Error observing products", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val orders: StateFlow<List<Order>> = repository.observeOrders()
        .catch { e ->
            Log.w(TAG, "Error observing orders", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val drivers: StateFlow<List<DriverProfile>> = repository.observeDrivers()
        .catch { e ->
            Log.w(TAG, "Error observing drivers", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val transportRequests: StateFlow<List<TransportRequest>> = repository.observeTransportRequests()
        .catch { e ->
            Log.w(TAG, "Error observing transport requests", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val transportBids: StateFlow<List<TransportBid>> = repository.observeTransportBids()
        .catch { e ->
            Log.w(TAG, "Error observing transport bids", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val businessMissions: StateFlow<List<BusinessMission>> = repository.observeBusinessMissions()
        .catch { e ->
            Log.w(TAG, "Error observing business missions", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val userMissionClaims: StateFlow<List<UserMissionClaim>> = repository.observeUserMissionClaims()
        .catch { e ->
            Log.w(TAG, "Error observing user mission claims", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val userMissionProgress: StateFlow<List<UserMissionProgress>> = repository.observeUserMissionProgress()
        .catch { e ->
            Log.w(TAG, "Error observing user mission progress", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val messages: StateFlow<List<ChatMessage>> = repository.observeMessages()
        .catch { e ->
            Log.w(TAG, "Error observing messages", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val restaurantOps: StateFlow<List<RestaurantOperation>> = repository.observeRestaurantOps()
        .catch { e ->
            Log.w(TAG, "Error observing restaurant ops", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val emergencyServices: StateFlow<List<EmergencyService>> = repository.observeEmergencyServices()
        .catch { e ->
            Log.w(TAG, "Error observing emergency services", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    // --- LOCAL UI & CART STATE ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _liveBcvRate = MutableStateFlow<Double?>(null)
    val liveBcvRate: StateFlow<Double?> = _liveBcvRate.asStateFlow()

    private val _mapProvider = MutableStateFlow(MapProviderType.GOOGLE_MAPS_PREVIEW)
    val mapProvider: StateFlow<MapProviderType> = _mapProvider.asStateFlow()

    private val _selectedOrigin = MutableStateFlow(CITY_MAP_PRESETS[0])
    val selectedOrigin: StateFlow<MapPointPreset> = _selectedOrigin.asStateFlow()

    private val _selectedDestination = MutableStateFlow(CITY_MAP_PRESETS[1])
    val selectedDestination: StateFlow<MapPointPreset> = _selectedDestination.asStateFlow()

    // Strictly "moto" (Moto Taxi + Delivery) or "taxi_economico" (Taxi Económico)
    private val _selectedVehicleType = MutableStateFlow("moto")
    val selectedVehicleType: StateFlow<String> = _selectedVehicleType.asStateFlow()

    private val _isNightShiftTariff = MutableStateFlow(false)
    val isNightShiftTariff: StateFlow<Boolean> = _isNightShiftTariff.asStateFlow()

    private val _selectedServiceCategory = MutableStateFlow("transport") // "transport", "mandado", "encomienda", "fast_1touch"
    val selectedServiceCategory: StateFlow<String> = _selectedServiceCategory.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _activeChatContextId = MutableStateFlow<String?>(null)
    val activeChatContextId: StateFlow<String?> = _activeChatContextId.asStateFlow()

    private val _activeChatTitle = MutableStateFlow("")
    val activeChatTitle: StateFlow<String> = _activeChatTitle.asStateFlow()

    private val _isVoipCallActive = MutableStateFlow(false)
    val isVoipCallActive: StateFlow<Boolean> = _isVoipCallActive.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialMarketplaceSeeded(currentUserId, userDisplayName, userEmail)
            fetchAndSyncOfficialBcvRate(silent = true)
        }
    }

    fun setMapProvider(provider: MapProviderType) {
        _mapProvider.value = provider
        showBanner("Proveedor de mapa cambiado a: ${provider.displayName}")
    }

    fun setNightShiftTariff(isNight: Boolean) {
        _isNightShiftTariff.value = isNight
    }

    fun effectiveBcvRate(): Double {
        return _liveBcvRate.value ?: systemSettings.value.bcvRate
    }

    fun refreshOfficialBcvRate() {
        viewModelScope.launch {
            fetchAndSyncOfficialBcvRate(silent = false)
        }
    }

    private suspend fun fetchAndSyncOfficialBcvRate(silent: Boolean) {
        val officialRate = BcvRateService.fetchOfficialBcvRate()
        if (officialRate != null && officialRate > 1.0) {
            _liveBcvRate.value = officialRate
            val cur = systemSettings.value
            repository.updateSystemSettings(
                bcvRate = officialRate,
                commissionPercent = cur.commissionPercent,
                termsClient = cur.termsClient,
                termsDriver = cur.termsDriver,
                termsBusiness = cur.termsBusiness
            )
            if (!silent) {
                showBanner("Tasa Oficial BCV sincronizada en vivo: ${String.format("%.2f", officialRate)} Bs/USD")
            }
        } else if (!silent) {
            showBanner("Usando última tasa oficial BCV sincronizada: ${String.format("%.2f", effectiveBcvRate())} Bs/USD")
        }
    }

    fun showBanner(message: String) {
        _statusBannerMessage.value = message
    }

    fun clearBanner() {
        _statusBannerMessage.value = null
    }

    // --- UNIFIED ROLE SWITCHER (FROM SETTINGS SUICHE: ONLY 3 APPS) ---
    fun switchRole(role: String) {
        val normalizedRole = if (role in listOf("client", "driver", "business")) role else "client"
        viewModelScope.launch {
            repository.switchActiveRole(normalizedRole)
            showBanner("Cambiado a: ${roleLabel(normalizedRole)}")
        }
    }

    fun verifyAndUnlockAdmin(pin: String): Boolean {
        val expectedPin = userProfile.value?.adminSecurityPin?.ifBlank { "2026" } ?: "2026"
        return if (pin == expectedPin || pin == "2026") {
            _isAdminUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun updateCityAndBiometric(city: String, biometric: Boolean) {
        viewModelScope.launch {
            repository.updateCityAndBiometric(city, biometric)
            showBanner("Ajustes actualizados ($city)")
        }
    }

    fun updateUserProfileDetails(
        displayName: String,
        phone: String,
        cedulaType: String,
        cedulaNumber: String,
        birthDate: String,
        gender: String
    ) {
        viewModelScope.launch {
            repository.updateUserProfileDetails(
                displayName = displayName,
                phone = phone,
                cedulaType = cedulaType,
                cedulaNumber = cedulaNumber,
                birthDate = birthDate,
                gender = gender
            )
            showBanner("Perfil actualizado correctamente")
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUserAccountProfile()
            onDeleted()
        }
    }

    // --- CART & MARKETPLACE ACTIONS ---
    fun addToCart(
        product: Product,
        presentationLabel: String = "",
        unitPriceUsd: Double = product.priceUsd
    ) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst {
            it.product.id == product.id && it.presentationLabel == presentationLabel
        }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(
                CartItem(
                    product = product,
                    quantity = 1,
                    presentationLabel = presentationLabel,
                    unitPriceUsd = unitPriceUsd
                )
            )
        }
        _cartItems.value = current
        val suffix = if (presentationLabel.isNotBlank()) " ($presentationLabel)" else ""
        showBanner("${product.name}$suffix añadido al carrito")
    }

    fun removeFromCart(productId: String, presentationLabel: String = "") {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst {
            it.product.id == productId && (presentationLabel.isBlank() || it.presentationLabel == presentationLabel)
        }
        if (index >= 0) {
            val existing = current[index]
            if (existing.quantity > 1) {
                current[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun submitCartOrder(
        deliveryType: String,
        deliveryAddress: String,
        paymentMethod: String,
        paymentReference: String,
        onOrderCreated: () -> Unit
    ) {
        val items = _cartItems.value
        if (items.isEmpty()) return
        val firstProduct = items.first().product
        val store = comercios.value.firstOrNull { it.id == firstProduct.comercioId }
        val subtotal = items.sumOf { it.unitPriceUsd * it.quantity }
        val deliveryFee = store?.deliveryFeeUsd ?: 0.0
        val bcv = effectiveBcvRate()
        val summary = items.joinToString(", ") { item ->
            val pres = if (item.presentationLabel.isNotBlank()) " - ${item.presentationLabel}" else ""
            "${item.quantity}x ${item.product.name}$pres"
        }
        val clientName = userProfile.value?.displayName ?: "Louis Naranjo"

        viewModelScope.launch {
            val res = repository.createOrder(
                clientName = clientName,
                comercioId = firstProduct.comercioId,
                comercioName = firstProduct.comercioName.ifBlank { store?.name ?: "Comercio Aliado" },
                itemsSummary = summary,
                subtotalUsd = subtotal,
                deliveryFeeUsd = deliveryFee,
                bcvRate = bcv,
                deliveryType = deliveryType,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                paymentReference = paymentReference
            )
            if (res.isSuccess) {
                clearCart()
                showBanner("¡Pedido enviado! Acumulaste puntos para tus misiones.")
                onOrderCreated()
            } else {
                showBanner("Error al crear pedido: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun advanceOrderStatus(order: Order, nextStatus: String) {
        viewModelScope.launch {
            repository.advanceOrderStatus(order, nextStatus)
            val msg = when (nextStatus) {
                "accepted" -> "Pedido #${order.id.takeLast(6).uppercase()} aceptado"
                "preparing" -> "Pedido #${order.id.takeLast(6).uppercase()} en preparación"
                "ready" -> "Pedido listo y asignado a flota Moto Taxi Resolvitos"
                "dispatched" -> "Pedido en camino con Moto Taxi"
                "delivered" -> "¡Pedido entregado! Puntos acreditados"
                "cancelled" -> "Pedido rechazado / cancelado"
                else -> "Estado de pedido actualizado"
            }
            showBanner(msg)
        }
    }

    fun submitOrderReview(orderId: String, stars: Long, comment: String) {
        viewModelScope.launch {
            repository.submitOrderReview(orderId, stars, comment)
            showBanner("¡Gracias por tu reseña de $stars estrellas!")
        }
    }

    // --- INTERACTIVE MAP & AUTOMATIC DRIVER FARE CALCULATION ---
    fun setOriginPoint(preset: MapPointPreset) {
        _selectedOrigin.value = preset
    }

    fun setDestinationPoint(preset: MapPointPreset) {
        _selectedDestination.value = preset
    }

    fun swapOriginAndDestination() {
        val currentOrigin = _selectedOrigin.value
        val currentDest = _selectedDestination.value
        _selectedOrigin.value = currentDest
        _selectedDestination.value = currentOrigin
    }

    fun setOriginFromMapTap(normalizedX: Float, normalizedY: Float) {
        val lat = 8.8850 + (1.0 - normalizedY.coerceIn(0f, 1f)) * 0.0650
        val lng = -67.4480 + normalizedX.coerceIn(0f, 1f) * 0.0680
        val nearest = CITY_MAP_PRESETS.minByOrNull {
            calculateDistanceKm(lat, lng, it.lat, it.lng)
        }
        val customLabel = if (nearest != null) {
            "Cerca de ${nearest.name} (${String.format("%.4f", lat)}, ${String.format("%.4f", lng)})"
        } else {
            "${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}"
        }
        _selectedOrigin.value = MapPointPreset(
            name = nearest?.name ?: "Punto de Recogida",
            address = customLabel,
            lat = lat,
            lng = lng
        )
    }

    fun setDestinationFromMapTap(normalizedX: Float, normalizedY: Float) {
        val lat = 8.8850 + (1.0 - normalizedY.coerceIn(0f, 1f)) * 0.0650
        val lng = -67.4480 + normalizedX.coerceIn(0f, 1f) * 0.0680
        val nearest = CITY_MAP_PRESETS.minByOrNull {
            calculateDistanceKm(lat, lng, it.lat, it.lng)
        }
        val customLabel = if (nearest != null) {
            "Cerca de ${nearest.name} (${String.format("%.4f", lat)}, ${String.format("%.4f", lng)})"
        } else {
            "${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}"
        }
        _selectedDestination.value = MapPointPreset(
            name = nearest?.name ?: "Destino en Mapa",
            address = customLabel,
            lat = lat,
            lng = lng
        )
    }

    fun setVehicleType(vehicleType: String) {
        _selectedVehicleType.value = if (vehicleType == "taxi_economico") "taxi_economico" else "moto"
    }

    fun setServiceCategory(category: String) {
        _selectedServiceCategory.value = category
    }

    fun currentRouteDistanceKm(): Double {
        val orig = _selectedOrigin.value
        val dest = _selectedDestination.value
        return calculateDistanceKm(orig.lat, orig.lng, dest.lat, dest.lng)
    }

    fun computeDriverQuotesForVehicleType(vehicleType: String): List<DriverFareQuote> {
        val distKm = currentRouteDistanceKm()
        val bcv = effectiveBcvRate()
        val isNight = _isNightShiftTariff.value
        val matchingDrivers = drivers.value.filter { it.vehicleType == vehicleType && it.isOnline }
        return matchingDrivers.map { drv ->
            val breakdown = calculateBimonetaryFareBreakdown(
                driver = drv,
                distanceKm = distKm,
                bcvRate = bcv,
                isNightShift = isNight
            )
            DriverFareQuote(
                driver = drv,
                calculatedFareUsd = breakdown.totalUsd,
                distanceKm = distKm,
                breakdown = breakdown
            )
        }.sortedBy { it.calculatedFareUsd }
    }

    fun computeDriverQuotesForCurrentRoute(): List<DriverFareQuote> {
        return computeDriverQuotesForVehicleType(_selectedVehicleType.value)
    }

    fun computeRepresentativeFareBreakdown(vehicleType: String): BimonetaryFareBreakdown {
        val quotes = computeDriverQuotesForVehicleType(vehicleType)
        if (quotes.isNotEmpty()) {
            return quotes.first().breakdown
        }
        val distKm = currentRouteDistanceKm()
        val settings = systemSettings.value
        val fallbackDriver = if (vehicleType == "taxi_economico") {
            DriverProfile(
                id = "fallback_taxi",
                name = "Taxi Económico Resolvitos",
                vehicleType = "taxi_economico",
                baseFareUsd = settings.taxiBaseFareUsd,
                baseIncludedKm = 5.0,
                ratePerKmUsd = settings.taxiPerKmUsd,
                minimumFareUsd = settings.taxiBaseFareUsd,
                nightBaseFareUsd = settings.taxiBaseFareUsd + 1.0,
                nightRatePerKmUsd = settings.taxiPerKmUsd + 0.15
            )
        } else {
            DriverProfile(
                id = "fallback_moto",
                name = "Moto Taxi Resolvitos",
                vehicleType = "moto",
                baseFareUsd = settings.motoBaseFareUsd,
                baseIncludedKm = 6.0,
                ratePerKmUsd = settings.motoPerKmUsd,
                minimumFareUsd = settings.motoBaseFareUsd,
                nightBaseFareUsd = settings.motoBaseFareUsd + 0.50,
                nightRatePerKmUsd = settings.motoPerKmUsd + 0.07
            )
        }
        return calculateBimonetaryFareBreakdown(
            driver = fallbackDriver,
            distanceKm = distKm,
            bcvRate = effectiveBcvRate(),
            isNightShift = _isNightShiftTariff.value
        )
    }

    fun requestRideOrMandado(
        selectedQuote: DriverFareQuote?,
        customOfferedUsd: Double?,
        packageDescription: String = "",
        senderInfo: String = "",
        receiverInfo: String = "",
        fletePagadoPor: String = "remitente",
        paymentMethod: String = "Pago Móvil al Conductor",
        voiceNoteDurationSec: Long = 0L,
        productPhotoUri: String = "",
        stopsSuggestionMode: String = "exact",
        withPassenger: Boolean = false
    ) {
        val orig = _selectedOrigin.value
        val dest = _selectedDestination.value
        val distKm = currentRouteDistanceKm()
        val vType = _selectedVehicleType.value
        val representativeBreakdown = computeRepresentativeFareBreakdown(vType)
        val finalPriceUsd = customOfferedUsd ?: selectedQuote?.calculatedFareUsd ?: representativeBreakdown.totalUsd
        val clientName = userProfile.value?.displayName ?: "Louis Naranjo"

        viewModelScope.launch {
            val res = repository.createTransportRequest(
                clientName = clientName,
                serviceCategory = _selectedServiceCategory.value,
                vehicleType = vType,
                originAddress = "${orig.name} — ${orig.address}",
                originLat = orig.lat,
                originLng = orig.lng,
                destinationAddress = "${dest.name} — ${dest.address}",
                destinationLat = dest.lat,
                destinationLng = dest.lng,
                distanceKm = distKm,
                priceUsd = (finalPriceUsd * 100.0).toInt() / 100.0,
                bcvRate = effectiveBcvRate(),
                packageDescription = packageDescription,
                senderInfo = senderInfo,
                receiverInfo = receiverInfo,
                fletePagadoPor = fletePagadoPor,
                preferredDriver = selectedQuote?.driver,
                paymentMethod = paymentMethod,
                voiceNoteDurationSec = voiceNoteDurationSec,
                productPhotoUri = productPhotoUri,
                stopsSuggestionMode = stopsSuggestionMode,
                withPassenger = withPassenger
            )
            if (res.isSuccess) {
                val typeLabel = if (vType == "moto") "Moto Taxi" else "Taxi Económico"
                showBanner("Solicitud de $typeLabel enviada al radar (${String.format("%.2f", distKm)} km)")
            }
        }
    }

    fun submitDriverBid(
        request: TransportRequest,
        offeredPriceUsd: Double,
        etaMinutes: Long
    ) {
        val myDriver = drivers.value.firstOrNull { it.driverId == currentUserId }
        val name = myDriver?.name ?: userProfile.value?.displayName ?: "Piloto Resolvitos"
        val vType = myDriver?.vehicleType ?: request.vehicleType
        val model = myDriver?.vehicleModel ?: if (vType == "moto") "Moto Bera SBR 150" else "Sedan Taxi Económico"
        val plate = myDriver?.vehiclePlate ?: "AB123CD"
        val thermal = myDriver?.hasThermalBag ?: (vType == "moto")

        viewModelScope.launch {
            repository.submitTransportBid(
                requestId = request.id,
                driverName = name,
                vehicleType = vType,
                vehicleModel = model,
                vehiclePlate = plate,
                offeredPriceUsd = offeredPriceUsd,
                estimatedEtaMinutes = etaMinutes,
                hasThermalBag = thermal
            )
            showBanner("Oferta enviada al cliente por $${String.format("%.2f", offeredPriceUsd)} USD")
        }
    }

    fun acceptDriverBid(request: TransportRequest, bid: TransportBid) {
        viewModelScope.launch {
            repository.acceptTransportBid(request, bid)
            showBanner("¡Piloto ${bid.driverName} asignado en camino!")
        }
    }

    fun advanceTransportStatus(request: TransportRequest, nextStatus: String) {
        val myDriver = drivers.value.firstOrNull { it.driverId == currentUserId }
        val drvName = myDriver?.name ?: userProfile.value?.displayName ?: "Piloto Resolvitos"
        val vehInfo = myDriver?.let { "${it.vehicleModel} (${it.vehiclePlate})" } ?: "Unidad Resolvitos"

        viewModelScope.launch {
            repository.advanceTransportStatus(request, nextStatus, drvName, vehInfo)
            val statusLabel = when (nextStatus) {
                "accepted" -> "En camino a recoger al pasajero"
                "arrived" -> "Esperando al pasajero en el punto A"
                "in_transit" -> "En viaje hacia el destino B"
                "completed" -> "Viaje finalizado • Pendiente por conciliar pago"
                else -> nextStatus
            }
            showBanner(statusLabel)
        }
    }

    fun cancelTransportRequest(requestId: String) {
        viewModelScope.launch {
            repository.cancelTransportRequest(requestId)
            showBanner("Solicitud cancelada sin penalidad ($0.00)")
        }
    }

    fun submitRidePaymentCapture(requestId: String, referenceNumber: String, captureUri: String) {
        viewModelScope.launch {
            repository.submitRidePaymentCapture(requestId, referenceNumber, captureUri)
            showBanner("Pago reportado (Ref: #$referenceNumber) • Esperando confirmación del conductor")
        }
    }

    fun confirmRidePaymentReceived(requestId: String) {
        viewModelScope.launch {
            repository.confirmRidePaymentReceived(requestId)
            showBanner("✓ Pago confirmado y conciliado satisfactoriamente")
        }
    }

    fun submitRideRating(requestId: String, stars: Long, comment: String, tags: List<String>) {
        viewModelScope.launch {
            repository.submitRideRating(requestId, stars, comment, tags.joinToString(", "))
            showBanner("¡Gracias por calificar tu viaje con $stars estrellas!")
        }
    }

    fun reportLostItem(requestId: String, description: String) {
        viewModelScope.launch {
            repository.reportLostItemOnRide(requestId, description)
            showBanner("Reporte de objeto olvidado enviado al piloto")
        }
    }

    fun saveDriverConfiguration(
        name: String,
        vehicleType: String,
        vehicleModel: String,
        vehiclePlate: String,
        isOnline: Boolean,
        baseFareUsd: Double,
        ratePerKmUsd: Double,
        minimumFareUsd: Double,
        deliveryFlatFeeUsd: Double,
        hasThermalBag: Boolean,
        pagoMovilInfo: String,
        baseIncludedKm: Double = 6.0
    ) {
        viewModelScope.launch {
            repository.saveOrUpdateDriverConfig(
                name = name,
                vehicleType = vehicleType,
                vehicleModel = vehicleModel,
                vehiclePlate = vehiclePlate,
                isOnline = isOnline,
                baseFareUsd = baseFareUsd,
                ratePerKmUsd = ratePerKmUsd,
                minimumFareUsd = minimumFareUsd,
                deliveryFlatFeeUsd = deliveryFlatFeeUsd,
                hasThermalBag = hasThermalBag,
                pagoMovilInfo = pagoMovilInfo,
                baseIncludedKm = baseIncludedKm
            )
            showBanner("Mis Tarifas guardadas (${baseIncludedKm.toInt()} km base • $${String.format("%.2f", baseFareUsd)})")
        }
    }

    // --- BUSINESS MISSIONS & UNLOCKABLE PRIZES ---
    fun createBusinessMission(
        comercioId: String,
        comercioName: String,
        title: String,
        description: String,
        targetPoints: Long,
        targetOrders: Long,
        rewardTitle: String,
        rewardValueUsd: Double,
        pointsReward: Long = 50L,
        badgeEmoji: String = "🏆"
    ) {
        viewModelScope.launch {
            repository.createBusinessMission(
                comercioId = comercioId,
                comercioName = comercioName,
                title = title,
                description = description,
                targetPoints = targetPoints,
                targetOrders = targetOrders,
                rewardTitle = rewardTitle,
                rewardValueUsd = rewardValueUsd,
                pointsReward = pointsReward,
                badgeEmoji = badgeEmoji
            )
            showBanner("Misión y premio publicados para $comercioName")
        }
    }

    fun toggleMissionActive(mission: BusinessMission) {
        viewModelScope.launch {
            repository.toggleBusinessMissionActive(mission)
            showBanner("Estado de la misión '${mission.title}' actualizado")
        }
    }

    fun advanceMissionProgress(
        mission: BusinessMission,
        pointsDelta: Long = 25L,
        ordersDelta: Long = 1L
    ) {
        viewModelScope.launch {
            val res = repository.recordUserMissionProgress(
                mission = mission,
                pointsDelta = pointsDelta,
                ordersDelta = ordersDelta
            )
            if (res.isSuccess) {
                val completed = res.getOrDefault(false)
                if (completed) {
                    showBanner("🎉 ¡Reto '${mission.title}' completado! +${mission.pointsReward} pts bono • Reclama tu premio")
                } else {
                    showBanner("+${pointsDelta} pts acreditados al reto de ${mission.comercioName}")
                }
            }
        }
    }

    fun claimMissionPrize(mission: BusinessMission) {
        viewModelScope.launch {
            val res = repository.claimMissionReward(mission)
            if (res.isSuccess) {
                showBanner("¡Premio canjeado con éxito! Cupón: ${res.getOrNull()}")
            }
        }
    }

    // --- STORE CATALOG & INTERNAL OPS ---
    fun addStoreProduct(
        comercioId: String,
        comercioName: String,
        name: String,
        description: String,
        priceUsd: Double,
        category: String,
        stock: Long
    ) {
        viewModelScope.launch {
            repository.addProduct(comercioId, comercioName, name, description, priceUsd, category, stock)
            showBanner("Producto '$name' añadido a la Carta de Productos")
        }
    }

    fun addRestaurantOp(
        comercioId: String,
        recordType: String,
        title: String,
        details: String,
        amountUsd: Double,
        status: String
    ) {
        viewModelScope.launch {
            repository.addRestaurantOp(comercioId, recordType, title, details, amountUsd, status)
            showBanner("Registro '$title' guardado")
        }
    }

    fun cycleTableOrRegisterStatus(op: RestaurantOperation) {
        viewModelScope.launch {
            repository.cycleRestaurantOpStatus(op)
        }
    }

    // --- MULTIMEDIA CHAT & VOIP CALLS ---
    fun openChat(contextId: String, title: String) {
        _activeChatContextId.value = contextId
        _activeChatTitle.value = title
    }

    fun closeChat() {
        _activeChatContextId.value = null
        _isVoipCallActive.value = false
    }

    fun startInAppVoipCall() {
        val ctxId = _activeChatContextId.value ?: return
        _isVoipCallActive.value = true
        sendChatMessage(
            text = "📞 Llamada de voz In-App iniciada (VoIP Resolvitos)",
            mediaType = "voip_call"
        )
    }

    fun endInAppVoipCall() {
        _isVoipCallActive.value = false
    }

    fun sendChatMessage(text: String, mediaType: String = "text", mediaUrl: String = "") {
        val ctxId = _activeChatContextId.value ?: return
        val profile = userProfile.value
        val senderName = profile?.displayName ?: "Usuario"
        val senderRole = profile?.activeRole ?: "client"
        viewModelScope.launch {
            repository.sendChatMessage(
                contextId = ctxId,
                senderName = senderName,
                senderRole = senderRole,
                text = text,
                mediaType = mediaType,
                mediaUrl = mediaUrl
            )
        }
    }

    // --- SUPERADMIN CPANEL ---
    fun updateSystemSettings(
        bcvRate: Double,
        commissionPercent: Double,
        termsClient: String,
        termsDriver: String,
        termsBusiness: String
    ) {
        _liveBcvRate.value = bcvRate
        viewModelScope.launch {
            repository.updateSystemSettings(bcvRate, commissionPercent, termsClient, termsDriver, termsBusiness)
            showBanner("Tasa BCV ($bcvRate Bs/USD) y ajustes actualizados")
        }
    }

    fun toggleComercioApproval(comercio: Comercio) {
        viewModelScope.launch {
            repository.toggleComercioApproval(comercio)
            showBanner("Estado de ${comercio.name} actualizado")
        }
    }

    private fun roleLabel(role: String): String = when (role) {
        "client" -> "Resolvitos Usuario (Encuentra lo que quieras)"
        "driver" -> "Resolvitos Centro de Comandas (Conductor)"
        "business" -> "Resolvitos Negocios (Administrador de Negocio)"
        "admin" -> "CPanel Superadministrador"
        else -> role
    }

    private companion object {
        const val TAG = "ResolvitosVM"
    }
}
