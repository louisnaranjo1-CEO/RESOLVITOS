package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.BusinessMission
import com.example.data.model.ChatMessage
import com.example.data.model.Comercio
import com.example.data.model.DEFAULT_TERMS_BUSINESS
import com.example.data.model.DEFAULT_TERMS_CLIENT
import com.example.data.model.DEFAULT_TERMS_DRIVER
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyService
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.RestaurantOperation
import com.example.data.model.SystemSettings
import com.example.data.model.TENANT_KEY
import com.example.data.model.TransportBid
import com.example.data.model.TransportRequest
import com.example.data.model.UserMissionClaim
import com.example.data.model.UserMissionProgress
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class ResolvitosRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth
        get() = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- 1. USER PROFILE & UNIFIED MULTI-ROLE ACCOUNT ---

    fun observeUserProfile(): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        val path = "profiles/$uid"
        emitAll(
            db.collection("profiles").document(uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun getOrCreateUserProfile(
        displayName: String = "Usuario Resolvitos",
        email: String = ""
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("profiles").document(uid)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    ?: UserProfile(id = uid, userId = uid, displayName = displayName, email = email)
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to displayName.ifBlank { "Usuario Resolvitos" },
                    "phone" to "0414-0000000",
                    "city" to "Caracas",
                    "role" to "admin",
                    "activeRole" to "client",
                    "enabledRoles" to listOf("client", "driver", "business", "admin"),
                    "points" to 150L,
                    "referralCode" to "RES-${uid.take(5).uppercase()}",
                    "biometricLockEnabled" to false,
                    "adminSecurityPin" to "2026",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                UserProfile(
                    id = uid,
                    userId = uid,
                    email = email,
                    displayName = displayName.ifBlank { "Usuario Resolvitos" },
                    points = 150L
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun switchActiveRole(newRole: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("profiles").document(uid)
        try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val existing = existingSnap.toObject(UserProfile::class.java) ?: UserProfile(userId = uid)
                val updatedRoles = (existing.enabledRoles + newRole).distinct()
                docRef.update(
                    mapOf(
                        "activeRole" to newRole,
                        "enabledRoles" to updatedRoles,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateCityAndBiometric(city: String, biometricEnabled: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("profiles").document(uid)
        try {
            docRef.update(
                mapOf(
                    "city" to city,
                    "biometricLockEnabled" to biometricEnabled,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateUserProfileDetails(
        displayName: String,
        phone: String,
        cedulaType: String,
        cedulaNumber: String,
        birthDate: String,
        gender: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("profiles").document(uid)
        try {
            docRef.update(
                mapOf(
                    "displayName" to displayName,
                    "phone" to phone,
                    "cedulaType" to cedulaType,
                    "cedulaNumber" to cedulaNumber,
                    "birthDate" to birthDate,
                    "gender" to gender,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteUserAccountProfile(): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("profiles").document(uid)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- 2. SYSTEM SETTINGS (BCV EXCHANGE RATE & LEGAL MARKDOWN) ---

    fun observeSystemSettings(): Flow<SystemSettings> = flow {
        requireUserId()
        val path = "system_settings/current"
        emitAll(
            db.collection("system_settings").document("current")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject(SystemSettings::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        ?: SystemSettings(id = "current")
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun updateSystemSettings(
        bcvRate: Double,
        commissionPercent: Double,
        termsClient: String,
        termsDriver: String,
        termsBusiness: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("system_settings").document("current")
        val payload = mapOf(
            "bcvRate" to bcvRate,
            "commissionPercent" to commissionPercent,
            "motoBaseFareUsd" to 1.20,
            "motoPerKmUsd" to 0.45,
            "taxiBaseFareUsd" to 2.50,
            "taxiPerKmUsd" to 0.85,
            "termsClient" to termsClient,
            "termsDriver" to termsDriver,
            "termsBusiness" to termsBusiness,
            "updatedBy" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload, SetOptions.merge()).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- 3. MARKETPLACE COMERCIOS & PRODUCTS ---

    fun observeComercios(): Flow<List<Comercio>> = flow {
        requireUserId()
        val path = "comercios"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Comercio::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeProducts(): Flow<List<Product>> = flow {
        requireUserId()
        val path = "products"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Product::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun addProduct(
        comercioId: String,
        comercioName: String,
        name: String,
        description: String,
        priceUsd: Double,
        category: String,
        stock: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docId = "prod_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("products").document(docId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "comercioId" to comercioId,
            "comercioName" to comercioName,
            "ownerId" to uid,
            "name" to name,
            "description" to description,
            "priceUsd" to priceUsd,
            "category" to category,
            "stock" to stock,
            "isAvailable" to (stock > 0),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun toggleComercioApproval(comercio: Comercio): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("comercios").document(comercio.id)
        try {
            docRef.update(
                mapOf(
                    "isApproved" to !comercio.isApproved,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 4. ORDERS & CONNECTED MOTO TAXI FLEET DISPATCH ---

    fun observeOrders(): Flow<List<Order>> = flow {
        requireUserId()
        val path = "orders"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Order::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createOrder(
        clientName: String,
        comercioId: String,
        comercioName: String,
        itemsSummary: String,
        subtotalUsd: Double,
        deliveryFeeUsd: Double,
        bcvRate: Double,
        deliveryType: String,
        deliveryAddress: String,
        paymentMethod: String,
        paymentReference: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("orders").document(orderId)
        val totalUsd = subtotalUsd + if (deliveryType == "delivery") deliveryFeeUsd else 0.0
        val pointsEarned = (totalUsd * 10).toLong().coerceAtLeast(15L)

        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "clientId" to uid,
            "clientName" to clientName,
            "comercioId" to comercioId,
            "comercioName" to comercioName,
            "itemsSummary" to itemsSummary,
            "subtotalUsd" to subtotalUsd,
            "deliveryFeeUsd" to if (deliveryType == "delivery") deliveryFeeUsd else 0.0,
            "totalUsd" to totalUsd,
            "bcvRateApplied" to bcvRate,
            "deliveryType" to deliveryType,
            "deliveryAddress" to deliveryAddress,
            "paymentMethod" to paymentMethod,
            "paymentReference" to paymentReference,
            "paymentStatus" to if (paymentReference.isNotBlank()) "verified" else "pending_verification",
            "status" to "pending",
            "stockConfirmed" to true,
            "isExternalDelivery" to (deliveryType == "delivery"),
            "transportRequestId" to "",
            "deliveryDriverId" to "",
            "driverName" to "",
            "vehiclePlate" to "",
            "pointsEarned" to pointsEarned,
            "rating" to 0L,
            "reviewComment" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            // Automatically update gamified mission progress for this store & global challenges
            advanceUserMissionsForOrderInternal(
                uid = uid,
                comercioId = comercioId,
                pointsEarned = pointsEarned
            )
            orderId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun advanceOrderStatus(order: Order, nextStatus: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val orderRef = db.collection("orders").document(order.id)
        try {
            val updates = mutableMapOf<String, Any>(
                "status" to nextStatus,
                "paymentStatus" to "verified",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            // When store marks order ready and uses external fleet, auto-create Moto Taxi delivery request
            if (nextStatus == "ready" && order.isExternalDelivery && order.transportRequestId.isBlank()) {
                val reqId = "req_dlv_${UUID.randomUUID().toString().replace("-", "").take(8)}"
                val reqRef = db.collection("transport_requests").document(reqId)
                val reqPayload = mapOf(
                    "tenantKey" to TENANT_KEY,
                    "clientId" to uid,
                    "clientName" to "Delivery: ${order.comercioName}",
                    "serviceCategory" to "delivery",
                    "vehicleType" to "moto",
                    "originAddress" to order.comercioName,
                    "originLat" to 10.4958,
                    "originLng" to -66.8489,
                    "destinationAddress" to order.deliveryAddress.ifBlank { "Dirección del Cliente" },
                    "destinationLat" to 10.4892,
                    "destinationLng" to -66.8544,
                    "distanceKm" to 2.4,
                    "priceUsd" to order.deliveryFeeUsd.coerceAtLeast(1.50),
                    "bcvRateApplied" to order.bcvRateApplied,
                    "status" to "searching",
                    "assignedDriverId" to "",
                    "assignedDriverName" to "",
                    "assignedVehicleInfo" to "",
                    "packageDescription" to "Pedido #${order.id.takeLast(5)}: ${order.itemsSummary}",
                    "senderInfo" to order.comercioName,
                    "receiverInfo" to order.clientName,
                    "fletePagadoPor" to "remitente",
                    "linkedOrderId" to order.id,
                    "paymentReference" to order.paymentReference,
                    "lostItemReport" to "",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                reqRef.set(reqPayload).await()
                updates["transportRequestId"] = reqId
            }
            orderRef.update(updates).await()

            // When delivered, credit points to the ordering user profile if same user
            if (nextStatus == "delivered" && order.clientId == uid) {
                val profileRef = db.collection("profiles").document(uid)
                val profileSnap = profileRef.get().await()
                if (profileSnap.exists()) {
                    val currentPts = profileSnap.getLong("points") ?: 0L
                    profileRef.update(
                        mapOf(
                            "points" to (currentPts + order.pointsEarned),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, orderRef.path)
            throw e
        }
    }

    suspend fun submitOrderReview(orderId: String, stars: Long, comment: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        try {
            docRef.update(
                mapOf(
                    "rating" to stars,
                    "reviewComment" to comment,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 5. DRIVERS (MOTO TAXI & TAXI ECONÓMICO) + CUSTOM FARE RATES ---

    fun observeDrivers(): Flow<List<DriverProfile>> = flow {
        requireUserId()
        val path = "drivers"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(DriverProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveOrUpdateDriverConfig(
        name: String,
        vehicleType: String, // "moto" or "taxi_economico"
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
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("drivers").document(uid)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "name" to name,
                        "vehicleType" to vehicleType,
                        "vehicleModel" to vehicleModel,
                        "vehiclePlate" to vehiclePlate,
                        "isOnline" to isOnline,
                        "availability" to if (isOnline) "disponible" else "fuera_de_linea",
                        "baseFareUsd" to baseFareUsd,
                        "baseIncludedKm" to baseIncludedKm,
                        "ratePerKmUsd" to ratePerKmUsd,
                        "minimumFareUsd" to minimumFareUsd,
                        "deliveryFlatFeeUsd" to deliveryFlatFeeUsd,
                        "hasThermalBag" to hasThermalBag,
                        "pagoMovilInfo" to pagoMovilInfo,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "tenantKey" to TENANT_KEY,
                    "driverId" to uid,
                    "name" to name.ifBlank { "Conductor Resolvitos" },
                    "phone" to "4127786837",
                    "vehicleType" to vehicleType,
                    "vehicleModel" to vehicleModel,
                    "vehiclePlate" to vehiclePlate,
                    "isOnline" to isOnline,
                    "availability" to if (isOnline) "disponible" else "fuera_de_linea",
                    "baseFareUsd" to baseFareUsd,
                    "baseIncludedKm" to baseIncludedKm,
                    "ratePerKmUsd" to ratePerKmUsd,
                    "minimumFareUsd" to minimumFareUsd,
                    "deliveryFlatFeeUsd" to deliveryFlatFeeUsd,
                    "hasThermalBag" to hasThermalBag,
                    "rating" to 4.95,
                    "latitude" to 8.9242,
                    "longitude" to -67.4293,
                    "commissionDebtUsd" to 0.75,
                    "pagoMovilInfo" to pagoMovilInfo,
                    "status" to "approved",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- 6. MOBILITY & MANDADOS WITH INTERACTIVE MAP & LIVE AUCTION ---

    fun observeTransportRequests(): Flow<List<TransportRequest>> = flow {
        requireUserId()
        val path = "transport_requests"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(TransportRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeTransportBids(): Flow<List<TransportBid>> = flow {
        requireUserId()
        val path = "transport_bids"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(TransportBid::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createTransportRequest(
        clientName: String,
        serviceCategory: String, // "transport", "mandado", "encomienda", "rapido"
        vehicleType: String, // "moto" or "taxi_economico"
        originAddress: String,
        originLat: Double,
        originLng: Double,
        destinationAddress: String,
        destinationLat: Double,
        destinationLng: Double,
        distanceKm: Double,
        priceUsd: Double,
        bcvRate: Double,
        packageDescription: String = "",
        senderInfo: String = "",
        receiverInfo: String = "",
        fletePagadoPor: String = "remitente",
        paymentMethod: String = "Pago Móvil al Conductor",
        voiceNoteDurationSec: Long = 0L,
        productPhotoUri: String = "",
        stopsSuggestionMode: String = "driver_suggests",
        withPassenger: Boolean = false,
        preferredDriver: DriverProfile? = null
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val reqId = "req_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("transport_requests").document(reqId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "clientId" to uid,
            "clientName" to clientName,
            "serviceCategory" to serviceCategory,
            "vehicleType" to vehicleType,
            "originAddress" to originAddress,
            "originLat" to originLat,
            "originLng" to originLng,
            "destinationAddress" to destinationAddress,
            "destinationLat" to destinationLat,
            "destinationLng" to destinationLng,
            "distanceKm" to distanceKm,
            "priceUsd" to priceUsd,
            "bcvRateApplied" to bcvRate,
            "status" to "searching",
            "assignedDriverId" to (preferredDriver?.driverId ?: ""),
            "assignedDriverName" to (preferredDriver?.name ?: ""),
            "assignedVehicleInfo" to (preferredDriver?.let { "${it.vehicleModel} • ${it.vehiclePlate}" } ?: ""),
            "packageDescription" to packageDescription,
            "senderInfo" to senderInfo,
            "receiverInfo" to receiverInfo,
            "fletePagadoPor" to fletePagadoPor,
            "linkedOrderId" to "",
            "paymentMethod" to paymentMethod,
            "paymentStatus" to "pending",
            "paymentReference" to "",
            "paymentCaptureUri" to "",
            "rating" to 0L,
            "reviewComment" to "",
            "reviewTags" to "",
            "voiceNoteDurationSec" to voiceNoteDurationSec,
            "productPhotoUri" to productPhotoUri,
            "stopsSuggestionMode" to stopsSuggestionMode,
            "withPassenger" to withPassenger,
            "lostItemReport" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            if (preferredDriver != null) {
                submitTransportBid(
                    requestId = reqId,
                    driverName = preferredDriver.name,
                    vehicleType = preferredDriver.vehicleType,
                    vehicleModel = preferredDriver.vehicleModel,
                    vehiclePlate = preferredDriver.vehiclePlate,
                    offeredPriceUsd = priceUsd,
                    estimatedEtaMinutes = 4L,
                    hasThermalBag = preferredDriver.hasThermalBag
                )
            }
            reqId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun submitTransportBid(
        requestId: String,
        driverName: String,
        vehicleType: String,
        vehicleModel: String,
        vehiclePlate: String,
        offeredPriceUsd: Double,
        estimatedEtaMinutes: Long,
        hasThermalBag: Boolean
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val bidId = "bid_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("transport_bids").document(bidId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "requestId" to requestId,
            "driverId" to uid,
            "driverName" to driverName,
            "vehicleType" to vehicleType,
            "vehicleModel" to vehicleModel,
            "vehiclePlate" to vehiclePlate,
            "driverRating" to 4.9,
            "offeredPriceUsd" to offeredPriceUsd,
            "estimatedEtaMinutes" to estimatedEtaMinutes.coerceAtLeast(1L),
            "hasThermalBag" to hasThermalBag,
            "status" to "pending",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            bidId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun acceptTransportBid(request: TransportRequest, bid: TransportBid): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(request.id)
        val bidRef = db.collection("transport_bids").document(bid.id)
        try {
            bidRef.update(
                mapOf(
                    "status" to "accepted",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            reqRef.update(
                mapOf(
                    "status" to "accepted",
                    "priceUsd" to bid.offeredPriceUsd,
                    "assignedDriverId" to bid.driverId,
                    "assignedDriverName" to bid.driverName,
                    "assignedVehicleInfo" to "${bid.vehicleModel} (${bid.vehiclePlate})",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            if (request.linkedOrderId.isNotBlank()) {
                db.collection("orders").document(request.linkedOrderId).update(
                    mapOf(
                        "status" to "dispatched",
                        "deliveryDriverId" to bid.driverId,
                        "driverName" to bid.driverName,
                        "vehiclePlate" to bid.vehiclePlate,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun advanceTransportStatus(
        request: TransportRequest,
        nextStatus: String,
        driverName: String = "",
        vehicleInfo: String = ""
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val reqRef = db.collection("transport_requests").document(request.id)
        try {
            val updates = mutableMapOf<String, Any>(
                "status" to nextStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (request.assignedDriverId.isBlank() && driverName.isNotBlank()) {
                updates["assignedDriverId"] = uid
                updates["assignedDriverName"] = driverName
                updates["assignedVehicleInfo"] = vehicleInfo
            }
            reqRef.update(updates).await()

            if (request.linkedOrderId.isNotBlank()) {
                val mappedOrderStatus = when (nextStatus) {
                    "accepted", "arrived", "in_transit" -> "dispatched"
                    "completed" -> "delivered"
                    else -> null
                }
                if (mappedOrderStatus != null) {
                    db.collection("orders").document(request.linkedOrderId).update(
                        mapOf(
                            "status" to mappedOrderStatus,
                            "driverName" to (driverName.ifBlank { request.assignedDriverName }),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun submitRidePaymentCapture(
        requestId: String,
        referenceNumber: String,
        captureUri: String
    ): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(requestId)
        try {
            reqRef.update(
                mapOf(
                    "paymentStatus" to "reported",
                    "paymentReference" to referenceNumber,
                    "paymentCaptureUri" to captureUri,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun confirmRidePaymentReceived(requestId: String): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(requestId)
        try {
            reqRef.update(
                mapOf(
                    "paymentStatus" to "verified",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun submitRideRating(
        requestId: String,
        stars: Long,
        comment: String,
        tags: String
    ): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(requestId)
        try {
            reqRef.update(
                mapOf(
                    "rating" to stars,
                    "reviewComment" to comment,
                    "reviewTags" to tags,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun cancelTransportRequest(requestId: String): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(requestId)
        try {
            reqRef.update(
                mapOf(
                    "status" to "cancelled",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    suspend fun reportLostItemOnRide(requestId: String, description: String): Result<Unit> = runCatching {
        requireUserId()
        val reqRef = db.collection("transport_requests").document(requestId)
        try {
            reqRef.update(
                mapOf(
                    "lostItemReport" to description,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reqRef.path)
            throw e
        }
    }

    // --- 7. POINTS, BUSINESS MISSIONS & UNLOCKABLE PRIZES (NO RAFFLES) ---

    fun observeBusinessMissions(): Flow<List<BusinessMission>> = flow {
        requireUserId()
        val path = "business_missions"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(BusinessMission::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeUserMissionClaims(): Flow<List<UserMissionClaim>> = flow {
        val uid = requireUserId()
        val path = "user_mission_claims"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(UserMissionClaim::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeUserMissionProgress(): Flow<List<UserMissionProgress>> = flow {
        val uid = requireUserId()
        val path = "user_mission_progress"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(UserMissionProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createBusinessMission(
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
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val missionId = "msn_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("business_missions").document(missionId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "comercioId" to comercioId,
            "comercioName" to comercioName,
            "creatorId" to uid,
            "title" to title,
            "description" to description,
            "targetPoints" to targetPoints.coerceAtLeast(0L),
            "targetOrders" to targetOrders.coerceAtLeast(0L),
            "pointsReward" to pointsReward.coerceAtLeast(0L),
            "badgeEmoji" to badgeEmoji,
            "rewardTitle" to rewardTitle,
            "rewardValueUsd" to rewardValueUsd.coerceAtLeast(0.0),
            "isActive" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            missionId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun toggleBusinessMissionActive(mission: BusinessMission): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("business_missions").document(mission.id)
        try {
            docRef.update(
                mapOf(
                    "isActive" to !mission.isActive,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun recordUserMissionProgress(
        mission: BusinessMission,
        pointsDelta: Long,
        ordersDelta: Long
    ): Result<Boolean> = runCatching {
        val uid = requireUserId()
        val progressId = "ump_${uid}_${mission.id}".take(120)
        val docRef = db.collection("user_mission_progress").document(progressId)
        try {
            val snap = docRef.get().await()
            val prevPts = if (snap.exists()) (snap.getLong("currentPoints") ?: 0L) else 0L
            val prevOrd = if (snap.exists()) (snap.getLong("currentOrders") ?: 0L) else 0L
            val wasCompleted = if (snap.exists()) (snap.getBoolean("isCompleted") ?: false) else false
            val wasClaimed = if (snap.exists()) (snap.getBoolean("rewardClaimed") ?: false) else false
            val existingCoupon = if (snap.exists()) (snap.getString("couponCode") ?: "") else ""

            val newPoints = (prevPts + pointsDelta).coerceAtLeast(0L)
            val newOrders = (prevOrd + ordersDelta).coerceAtLeast(0L)
            val meetsPoints = mission.targetPoints > 0L && newPoints >= mission.targetPoints
            val meetsOrders = mission.targetOrders > 0L && newOrders >= mission.targetOrders
            val nowCompleted = wasCompleted || meetsPoints || meetsOrders

            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "userId" to uid,
                        "missionId" to mission.id,
                        "comercioId" to mission.comercioId,
                        "comercioName" to mission.comercioName,
                        "missionTitle" to mission.title,
                        "currentPoints" to newPoints,
                        "targetPoints" to mission.targetPoints.coerceAtLeast(1L),
                        "currentOrders" to newOrders,
                        "targetOrders" to mission.targetOrders.coerceAtLeast(1L),
                        "pointsReward" to mission.pointsReward.coerceAtLeast(0L),
                        "isCompleted" to nowCompleted,
                        "rewardClaimed" to wasClaimed,
                        "couponCode" to existingCoupon,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "missionId" to mission.id,
                        "comercioId" to mission.comercioId,
                        "comercioName" to mission.comercioName,
                        "missionTitle" to mission.title,
                        "currentPoints" to newPoints,
                        "targetPoints" to mission.targetPoints.coerceAtLeast(1L),
                        "currentOrders" to newOrders,
                        "targetOrders" to mission.targetOrders.coerceAtLeast(1L),
                        "pointsReward" to mission.pointsReward.coerceAtLeast(0L),
                        "isCompleted" to nowCompleted,
                        "rewardClaimed" to false,
                        "couponCode" to "",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // Credit points to user's unified profile
            val profileRef = db.collection("profiles").document(uid)
            val profileSnap = profileRef.get().await()
            if (profileSnap.exists()) {
                val curProfilePts = profileSnap.getLong("points") ?: 0L
                val bonusPts = if (!wasCompleted && nowCompleted) mission.pointsReward else 0L
                profileRef.update(
                    mapOf(
                        "points" to (curProfilePts + pointsDelta.coerceAtLeast(0L) + bonusPts),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            nowCompleted
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    private suspend fun advanceUserMissionsForOrderInternal(
        uid: String,
        comercioId: String,
        pointsEarned: Long
    ) {
        try {
            val missionsSnap = db.collection("business_missions")
                .whereEqualTo("tenantKey", TENANT_KEY)
                .get()
                .await()
            val allMissions = missionsSnap.toObjects(
                BusinessMission::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            val relevantMissions = allMissions.filter { m ->
                m.isActive && (m.comercioId == comercioId || m.comercioId == "global")
            }
            for (mission in relevantMissions) {
                recordUserMissionProgress(
                    mission = mission,
                    pointsDelta = pointsEarned,
                    ordersDelta = 1L
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("ResolvitosRepository", "Non-fatal error updating user_mission_progress after order", e)
        }
    }

    suspend fun claimMissionReward(mission: BusinessMission): Result<String> = runCatching {
        val uid = requireUserId()
        val claimId = "clm_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val couponCode = "PREMIO-${UUID.randomUUID().toString().take(4).uppercase()}"
        val docRef = db.collection("user_mission_claims").document(claimId)
        val payload = mapOf(
            "userId" to uid,
            "missionId" to mission.id,
            "comercioId" to mission.comercioId,
            "comercioName" to mission.comercioName,
            "rewardTitle" to mission.rewardTitle,
            "couponCode" to couponCode,
            "status" to "unlocked",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            // Sync user_mission_progress document so the progress card reflects claimed state
            val progressId = "ump_${uid}_${mission.id}".take(120)
            val progRef = db.collection("user_mission_progress").document(progressId)
            val progSnap = progRef.get().await()
            if (progSnap.exists()) {
                progRef.update(
                    mapOf(
                        "userId" to uid,
                        "missionId" to mission.id,
                        "comercioId" to mission.comercioId,
                        "comercioName" to mission.comercioName,
                        "missionTitle" to mission.title,
                        "currentPoints" to (progSnap.getLong("currentPoints") ?: mission.targetPoints).coerceAtLeast(mission.targetPoints),
                        "targetPoints" to mission.targetPoints.coerceAtLeast(1L),
                        "currentOrders" to (progSnap.getLong("currentOrders") ?: mission.targetOrders).coerceAtLeast(mission.targetOrders),
                        "targetOrders" to mission.targetOrders.coerceAtLeast(1L),
                        "pointsReward" to mission.pointsReward.coerceAtLeast(0L),
                        "isCompleted" to true,
                        "rewardClaimed" to true,
                        "couponCode" to couponCode,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                progRef.set(
                    mapOf(
                        "userId" to uid,
                        "missionId" to mission.id,
                        "comercioId" to mission.comercioId,
                        "comercioName" to mission.comercioName,
                        "missionTitle" to mission.title,
                        "currentPoints" to mission.targetPoints.coerceAtLeast(1L),
                        "targetPoints" to mission.targetPoints.coerceAtLeast(1L),
                        "currentOrders" to mission.targetOrders.coerceAtLeast(1L),
                        "targetOrders" to mission.targetOrders.coerceAtLeast(1L),
                        "pointsReward" to mission.pointsReward.coerceAtLeast(0L),
                        "isCompleted" to true,
                        "rewardClaimed" to true,
                        "couponCode" to couponCode,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            couponCode
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- 8. UNIFIED MULTIMEDIA CHAT & VOIP CALL SIGNALING ---

    fun observeMessages(): Flow<List<ChatMessage>> = flow {
        requireUserId()
        val path = "messages"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(ChatMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun sendChatMessage(
        contextId: String,
        senderName: String,
        senderRole: String,
        text: String,
        mediaType: String = "text",
        mediaUrl: String = ""
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val msgId = "msg_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("messages").document(msgId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "contextId" to contextId,
            "senderId" to uid,
            "senderName" to senderName,
            "senderRole" to senderRole,
            "text" to text,
            "mediaType" to mediaType,
            "mediaUrl" to mediaUrl,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            msgId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- 9. RESTAURANT INTERNAL OPERATIONS (QR TABLES, WAITERS, EXPENSES, CASH REGISTER) ---

    fun observeRestaurantOps(): Flow<List<RestaurantOperation>> = flow {
        requireUserId()
        val path = "restaurant_ops"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(RestaurantOperation::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun addRestaurantOp(
        comercioId: String,
        recordType: String,
        title: String,
        details: String,
        amountUsd: Double,
        status: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val opId = "rop_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val docRef = db.collection("restaurant_ops").document(opId)
        val payload = mapOf(
            "tenantKey" to TENANT_KEY,
            "comercioId" to comercioId,
            "ownerId" to uid,
            "recordType" to recordType,
            "title" to title,
            "details" to details,
            "amountUsd" to amountUsd,
            "status" to status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            opId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun cycleRestaurantOpStatus(op: RestaurantOperation): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("restaurant_ops").document(op.id)
        val nextStatus = when (op.status) {
            "libre" -> "ocupada"
            "ocupada" -> "pagando"
            "pagando" -> "libre"
            else -> "cerrado"
        }
        try {
            docRef.update(
                mapOf(
                    "status" to nextStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 10. 24/7 EMERGENCY SERVICES DIRECTORY ---

    fun observeEmergencyServices(): Flow<List<EmergencyService>> = flow {
        requireUserId()
        val path = "emergency_services"
        emitAll(
            db.collection(path)
                .whereEqualTo("tenantKey", TENANT_KEY)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(EmergencyService::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // --- 11. AUTOMATIC INITIAL SEEDING FOR REAL-TIME SUPER APP EXPERIENCE ---

    suspend fun ensureInitialMarketplaceSeeded(uid: String, displayName: String, email: String) {
        try {
            getOrCreateUserProfile(displayName, email)

            val settingsRef = db.collection("system_settings").document("current")
            if (!settingsRef.get().await().exists()) {
                settingsRef.set(
                    mapOf(
                        "bcvRate" to 46.85,
                        "commissionPercent" to 10.0,
                        "motoBaseFareUsd" to 1.20,
                        "motoPerKmUsd" to 0.45,
                        "taxiBaseFareUsd" to 2.50,
                        "taxiPerKmUsd" to 0.85,
                        "termsClient" to DEFAULT_TERMS_CLIENT,
                        "termsDriver" to DEFAULT_TERMS_DRIVER,
                        "termsBusiness" to DEFAULT_TERMS_BUSINESS,
                        "updatedBy" to uid,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            val grillDoc = db.collection("comercios")
                .document("store_911_grill")
                .get()
                .await()

            if (!grillDoc.exists()) {
                // Seed Comercios Aliados matching Video 1 & Video 3
                val stores = listOf(
                    mapOf(
                        "id" to "store_911_grill",
                        "name" to "911 Grill",
                        "cat" to "Restaurantes",
                        "dist" to "457 m",
                        "eta" to 35L
                    ),
                    mapOf(
                        "id" to "store_sound_art",
                        "name" to "Sound and Art Publicidad",
                        "cat" to "Supermercados",
                        "dist" to "1.0 km",
                        "eta" to 30L
                    ),
                    mapOf(
                        "id" to "store_arepera",
                        "name" to "Arepera & Grill El Resolvito",
                        "cat" to "Restaurantes",
                        "dist" to "650 m",
                        "eta" to 25L
                    )
                )
                for (s in stores) {
                    val id = s["id"] as String
                    db.collection("comercios").document(id).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "ownerId" to uid,
                            "name" to (s["name"] as String),
                            "category" to (s["cat"] as String),
                            "city" to "Calabozo",
                            "address" to "Calabozo, Guárico",
                            "distanceLabel" to (s["dist"] as String),
                            "rating" to 5.0,
                            "deliveryFeeUsd" to 0.0,
                            "freeShippingThresholdUsd" to 20.0,
                            "etaMinutes" to (s["eta"] as Long),
                            "followersCount" to 1L,
                            "hasCashea" to true,
                            "hasTwoByThree" to true,
                            "isActive" to true,
                            "isApproved" to true,
                            "workingHours" to "08:00 AM - 11:30 PM",
                            "pagoMovilInfo" to "Banesco (0134) • 0412-7786837 • J-50123456-7",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed Catalog Products matching Video 1 & Video 3
                val initialProducts = listOf(
                    mapOf(
                        "id" to "prod_osole",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "name" to "Mezcla hortalizas OSOLE",
                        "description" to "Mezcla de Hortalizas Mixed Vegetables OSOLE 280g.",
                        "priceUsd" to 1.20,
                        "category" to "Supermercados",
                        "hasPresentations" to false
                    ),
                    mapOf(
                        "id" to "prod_mavesa",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "name" to "Mantequilla Mavesa",
                        "description" to "Margarina Mavesa todo uso, el rico sabor de Mavesa.",
                        "priceUsd" to 1.00,
                        "category" to "Supermercados",
                        "hasPresentations" to true
                    ),
                    mapOf(
                        "id" to "prod_pollo_broaster",
                        "comercioId" to "store_911_grill",
                        "comercioName" to "911 Grill",
                        "name" to "Pollo Broaster",
                        "description" to "Pollo Broaster crujiente con papas fritas y ensalada coleslaw.",
                        "priceUsd" to 5.00,
                        "category" to "Restaurantes",
                        "hasPresentations" to false
                    ),
                    mapOf(
                        "id" to "prod_reina",
                        "comercioId" to "store_arepera",
                        "comercioName" to "Arepera & Grill El Resolvito",
                        "name" to "Combo Arepa Reina Pepiada + Papelón",
                        "description" to "Pollo mechado cremoso con aguacate fresco, queso gouda y papelón con limón.",
                        "priceUsd" to 4.50,
                        "category" to "Restaurantes",
                        "hasPresentations" to false
                    )
                )
                for (p in initialProducts) {
                    db.collection("products").document(p["id"] as String).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "comercioId" to (p["comercioId"] as String),
                            "comercioName" to (p["comercioName"] as String),
                            "ownerId" to uid,
                            "name" to (p["name"] as String),
                            "description" to (p["description"] as String),
                            "priceUsd" to (p["priceUsd"] as Double),
                            "category" to (p["category"] as String),
                            "stock" to 40L,
                            "isAvailable" to true,
                            "freeShipping" to true,
                            "hasPresentations" to (p["hasPresentations"] as Boolean),
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed Moto Taxi (Passengers + Delivery) and Taxi Económico drivers with Base + Excess Km rates
                val initialDrivers = listOf(
                    mapOf(
                        "id" to "drv_moto_simon",
                        "name" to "Simón Bolívar (Moto Taxi & Delivery)",
                        "vehicleType" to "moto",
                        "vehicleModel" to "Bera SBR 150 Amarilla",
                        "vehiclePlate" to "AB9K12M",
                        "baseFareUsd" to 1.50,
                        "baseIncludedKm" to 6.0,
                        "ratePerKmUsd" to 0.13,
                        "minimumFareUsd" to 1.50,
                        "deliveryFlatFeeUsd" to 1.50,
                        "hasThermalBag" to true
                    ),
                    mapOf(
                        "id" to "drv_moto_yorvis",
                        "name" to "Yorvis Rivas (Moto Taxi Express)",
                        "vehicleType" to "moto",
                        "vehicleModel" to "Empire Horse 150 Negra",
                        "vehiclePlate" to "AC4M88D",
                        "baseFareUsd" to 1.50,
                        "baseIncludedKm" to 5.0,
                        "ratePerKmUsd" to 0.15,
                        "minimumFareUsd" to 1.50,
                        "deliveryFlatFeeUsd" to 1.50,
                        "hasThermalBag" to true
                    ),
                    mapOf(
                        "id" to "drv_taxi_luis",
                        "name" to "Luis Salazar (Taxi Económico)",
                        "vehicleType" to "taxi_economico",
                        "vehicleModel" to "Hyundai Accent Gris A/A",
                        "vehiclePlate" to "AA882TA",
                        "baseFareUsd" to 2.50,
                        "baseIncludedKm" to 5.0,
                        "ratePerKmUsd" to 0.35,
                        "minimumFareUsd" to 2.50,
                        "deliveryFlatFeeUsd" to 2.50,
                        "hasThermalBag" to false
                    ),
                    mapOf(
                        "id" to "drv_taxi_ana",
                        "name" to "Ana Bolívar (Taxi Económico)",
                        "vehicleType" to "taxi_economico",
                        "vehicleModel" to "Toyota Corolla Blanco A/A",
                        "vehiclePlate" to "AD410TX",
                        "baseFareUsd" to 2.80,
                        "baseIncludedKm" to 6.0,
                        "ratePerKmUsd" to 0.40,
                        "minimumFareUsd" to 2.80,
                        "deliveryFlatFeeUsd" to 2.50,
                        "hasThermalBag" to false
                    )
                )
                for (d in initialDrivers) {
                    db.collection("drivers").document(d["id"] as String).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "driverId" to (d["id"] as String),
                            "name" to (d["name"] as String),
                            "phone" to "0412-7786837",
                            "vehicleType" to (d["vehicleType"] as String),
                            "vehicleModel" to (d["vehicleModel"] as String),
                            "vehiclePlate" to (d["vehiclePlate"] as String),
                            "isOnline" to true,
                            "availability" to "disponible",
                            "baseFareUsd" to (d["baseFareUsd"] as Double),
                            "baseIncludedKm" to (d["baseIncludedKm"] as Double),
                            "ratePerKmUsd" to (d["ratePerKmUsd"] as Double),
                            "minimumFareUsd" to (d["minimumFareUsd"] as Double),
                            "deliveryFlatFeeUsd" to (d["deliveryFlatFeeUsd"] as Double),
                            "hasThermalBag" to (d["hasThermalBag"] as Boolean),
                            "rating" to 4.9,
                            "latitude" to 8.9242,
                            "longitude" to -67.4293,
                            "commissionDebtUsd" to 0.75,
                            "pagoMovilInfo" to "Pago Móvil Mercantil 0105 • 0412-7786837",
                            "status" to "approved",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed Initial Orders matching Video 1 & Video 3 history
                val initialOrders = listOf(
                    mapOf(
                        "id" to "ord_77d388",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "itemsSummary" to "1x Mezcla hortalizas OSOLE, 1x Mantequilla Mavesa - 500gr",
                        "subtotalUsd" to 16.20,
                        "totalUsd" to 16.20,
                        "status" to "delivered",
                        "cancelReason" to "",
                        "driverName" to "Simón Bolívar",
                        "rating" to 5L,
                        "reviewComment" to "Excelente"
                    ),
                    mapOf(
                        "id" to "ord_673c82",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "itemsSummary" to "1x Mantequilla Mavesa - 250gr, 1x Mezcla hortalizas OSOLE",
                        "subtotalUsd" to 2.20,
                        "totalUsd" to 2.20,
                        "status" to "delivered",
                        "cancelReason" to "",
                        "driverName" to "Simón Bolívar",
                        "rating" to 4L,
                        "reviewComment" to "Listo"
                    ),
                    mapOf(
                        "id" to "ord_1b1d3c",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "itemsSummary" to "5x Mantequilla Mavesa - 500gr",
                        "subtotalUsd" to 20.00,
                        "totalUsd" to 20.00,
                        "status" to "cancelled",
                        "cancelReason" to "Ya no deseo realizar esta compra",
                        "driverName" to "",
                        "rating" to 0L,
                        "reviewComment" to ""
                    )
                )
                for (o in initialOrders) {
                    db.collection("orders").document(o["id"] as String).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "clientId" to uid,
                            "clientName" to displayName.ifBlank { "Louis Naranjo" },
                            "comercioId" to (o["comercioId"] as String),
                            "comercioName" to (o["comercioName"] as String),
                            "itemsSummary" to (o["itemsSummary"] as String),
                            "subtotalUsd" to (o["subtotalUsd"] as Double),
                            "deliveryFeeUsd" to 0.0,
                            "totalUsd" to (o["totalUsd"] as Double),
                            "bcvRateApplied" to 886.23,
                            "deliveryType" to "delivery",
                            "deliveryAddress" to "Ubicación GPS confirmada - Calabozo",
                            "paymentMethod" to "Pago Móvil",
                            "paymentReference" to "884421",
                            "paymentStatus" to "verified",
                            "status" to (o["status"] as String),
                            "cancelReason" to (o["cancelReason"] as String),
                            "stockConfirmed" to true,
                            "isExternalDelivery" to true,
                            "transportRequestId" to "",
                            "deliveryDriverId" to "drv_moto_simon",
                            "driverName" to (o["driverName"] as String),
                            "vehiclePlate" to "AB9K12M",
                            "pointsEarned" to 25L,
                            "rating" to (o["rating"] as Long),
                            "reviewComment" to (o["reviewComment"] as String),
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed Business Missions & Unlockable Prizes (Configurable by Store Admin & Superadmin)
                val initialMissions = listOf(
                    mapOf(
                        "id" to "msn_911_1",
                        "comercioId" to "store_911_grill",
                        "comercioName" to "911 Grill",
                        "title" to "Reto 911 Grill: Rey del Broaster",
                        "description" to "Acumula 100 pts o 2 pedidos en 911 Grill y desbloquea un Combo Pollo Broaster + Refresco gratis.",
                        "targetPoints" to 100L,
                        "targetOrders" to 2L,
                        "pointsReward" to 60L,
                        "badgeEmoji" to "🍗",
                        "rewardTitle" to "Combo Pollo Broaster Gratis",
                        "rewardValueUsd" to 5.00,
                        "initialUserPts" to 75L,
                        "initialUserOrd" to 1L
                    ),
                    mapOf(
                        "id" to "msn_sound_1",
                        "comercioId" to "store_sound_art",
                        "comercioName" to "Sound and Art Publicidad",
                        "title" to "Misión Supermercado Aliado: 2 Pedidos",
                        "description" to "Completa 2 compras o 120 puntos en Sound and Art Publicidad para desbloquear un Pack Mavesa + Osole Gratis.",
                        "targetPoints" to 120L,
                        "targetOrders" to 2L,
                        "pointsReward" to 80L,
                        "badgeEmoji" to "🛒",
                        "rewardTitle" to "Pack Mavesa + Osole + Delivery Gratis",
                        "rewardValueUsd" to 4.50,
                        "initialUserPts" to 120L,
                        "initialUserOrd" to 2L
                    ),
                    mapOf(
                        "id" to "msn_global_1",
                        "comercioId" to "global",
                        "comercioName" to "Resolvitos Global",
                        "title" to "Misión Nivel Oro Movilidad & Delivery",
                        "description" to "Alcanza 150 puntos o 3 servicios en tu cuenta unificada para desbloquear un cupón de $3.00 USD en Moto Taxi o Taxi Económico.",
                        "targetPoints" to 150L,
                        "targetOrders" to 3L,
                        "pointsReward" to 100L,
                        "badgeEmoji" to "🛵",
                        "rewardTitle" to "Bono $3.00 USD en Moto Taxi / Taxi Económico",
                        "rewardValueUsd" to 3.00,
                        "initialUserPts" to 90L,
                        "initialUserOrd" to 2L
                    )
                )
                for (m in initialMissions) {
                    val mId = m["id"] as String
                    val targetPts = m["targetPoints"] as Long
                    val targetOrd = m["targetOrders"] as Long
                    val ptsReward = m["pointsReward"] as Long
                    val initPts = m["initialUserPts"] as Long
                    val initOrd = m["initialUserOrd"] as Long

                    db.collection("business_missions").document(mId).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "comercioId" to (m["comercioId"] as String),
                            "comercioName" to (m["comercioName"] as String),
                            "creatorId" to uid,
                            "title" to (m["title"] as String),
                            "description" to (m["description"] as String),
                            "targetPoints" to targetPts,
                            "targetOrders" to targetOrd,
                            "pointsReward" to ptsReward,
                            "badgeEmoji" to (m["badgeEmoji"] as String),
                            "rewardTitle" to (m["rewardTitle"] as String),
                            "rewardValueUsd" to (m["rewardValueUsd"] as Double),
                            "isActive" to true,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()

                    val progressId = "ump_${uid}_${mId}".take(120)
                    db.collection("user_mission_progress").document(progressId).set(
                        mapOf(
                            "userId" to uid,
                            "missionId" to mId,
                            "comercioId" to (m["comercioId"] as String),
                            "comercioName" to (m["comercioName"] as String),
                            "missionTitle" to (m["title"] as String),
                            "currentPoints" to initPts,
                            "targetPoints" to targetPts,
                            "currentOrders" to initOrd,
                            "targetOrders" to targetOrd,
                            "pointsReward" to ptsReward,
                            "isCompleted" to (initPts >= targetPts || initOrd >= targetOrd),
                            "rewardClaimed" to false,
                            "couponCode" to "",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed Restaurant QR Tables, Waiters & Cash Register
                val ops = listOf(
                    mapOf("id" to "rop_mesa_1", "type" to "table_qr", "title" to "Mesa QR #01 (Terraza)", "details" to "QR-MESA-01 • Mesonero: Gabriel", "amount" to 18.50, "status" to "ocupada"),
                    mapOf("id" to "rop_mesa_2", "type" to "table_qr", "title" to "Mesa QR #02 (Salón VIP)", "details" to "QR-MESA-02 • Disponible para escanear", "amount" to 0.0, "status" to "libre"),
                    mapOf("id" to "rop_caja_1", "type" to "cash_register", "title" to "Caja Principal Turno Día", "details" to "Cajero: Mariana • 14 órdenes procesadas", "amount" to 184.40, "status" to "abierta")
                )
                for (o in ops) {
                    db.collection("restaurant_ops").document(o["id"] as String).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "comercioId" to "store_arepera",
                            "ownerId" to uid,
                            "recordType" to (o["type"] as String),
                            "title" to (o["title"] as String),
                            "details" to (o["details"] as String),
                            "amountUsd" to (o["amount"] as Double),
                            "status" to (o["status"] as String),
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed 24/7 Emergency Services Directory
                val emergencies = listOf(
                    mapOf("id" to "emg_911", "name" to "VEN 911 — Central Nacional de Emergencias", "cat" to "Seguridad", "scope" to "Nacional", "phone" to "911"),
                    mapOf("id" to "emg_salud", "name" to "Ambulancias & Paramédicos 24/7", "cat" to "Salud", "scope" to "Caracas", "phone" to "0212-2662211"),
                    mapOf("id" to "emg_bomberos", "name" to "Cuerpo de Bomberos y Rescate", "cat" to "Bomberos", "scope" to "Nacional", "phone" to "0212-5454545"),
                    mapOf("id" to "emg_gruas", "name" to "Auxilio Vial & Grúas Resolvitos 24h", "cat" to "Vial", "scope" to "Caracas", "phone" to "0414-9110000")
                )
                for (e in emergencies) {
                    db.collection("emergency_services").document(e["id"] as String).set(
                        mapOf(
                            "tenantKey" to TENANT_KEY,
                            "name" to (e["name"] as String),
                            "category" to (e["cat"] as String),
                            "scope" to (e["scope"] as String),
                            "phone" to (e["phone"] as String),
                            "whatsapp" to (e["phone"] as String),
                            "isAvailable24h" to true,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("ResolvitosRepo", "Initial seed check skipped or partial: ${e.message}")
        }
    }
}
