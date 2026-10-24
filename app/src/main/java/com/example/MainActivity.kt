package com.example

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.model.DEFAULT_TERMS_BUSINESS
import com.example.data.model.DEFAULT_TERMS_CLIENT
import com.example.data.model.DEFAULT_TERMS_DRIVER
import com.example.data.repository.ResolvitosRepository
import com.example.ui.components.AdminPinGateDialog
import com.example.ui.components.BcvCalculatorDialog
import com.example.ui.components.BrightYellow
import com.example.ui.components.EmergencyDirectoryDialog
import com.example.ui.components.LegalTermsMarkdownDialog
import com.example.ui.components.ResolvitosLogoBadge
import com.example.ui.components.ResolvitosSplashScreen
import com.example.ui.components.UnifiedChatAndVoipDialog
import com.example.ui.screens.AdminCPanelScreen
import com.example.ui.screens.BusinessModeScreen
import com.example.ui.screens.ClientModeScreen
import com.example.ui.screens.DriverModeScreen
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.ResolvitosTheme
import com.example.ui.theme.StatusCoral
import com.example.ui.theme.StatusEmerald
import com.example.ui.viewmodel.ResolvitosViewModel
import com.example.util.ResolvitosSoundEvent
import com.example.util.SoundAndHapticManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResolvitosTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthWelcomeScreen()
    } else {
        ResolvitosSuperAppContainer(
            currentUserId = user.uid,
            userDisplayName = user.displayName ?: "Louis Naranjo",
            userEmail = user.email ?: "louisnaranjo1@gmail.com"
        )
    }
}

@Composable
fun AuthWelcomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showTermsPreview by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {},
            onUnauthenticated = {},
            scope = scope
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightYellow)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ResolvitosLogoBadge(boxSize = 96.dp, showTaglineRight = false)

                Text(
                    text = "Resolvitos Taxi Deliverys",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = ResolvitosSlate,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Encuentra lo que quieras • Marketplace Bimonetario (USD / Bs. BCV en vivo), Moto Taxi, Taxi Económico con Mapa, Misiones de Negocios y Asistencia 24h.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = Color(0xFFFFF8CC),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✨ Cuenta Unificada Multi-Rol: Inicia sesión con Google y usa el Suiche en Ajustes para cambiar entre Usuario, Conductor (Centro de Comandas) y Administrador de Negocios.",
                        style = MaterialTheme.typography.labelSmall,
                        color = ResolvitosSlate,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = StatusCoral,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = { isLoading = false },
                            onAuthError = { msg ->
                                isLoading = false
                                errorMessage = msg
                            },
                            scope = scope,
                            onAuthCancelled = { isLoading = false }
                        )
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightYellow,
                        contentColor = ResolvitosSlate
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("google_sign_in_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = ResolvitosSlate,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión con Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showTermsPreview = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Términos y Condiciones")
                }
            }
        }
    }

    if (showTermsPreview) {
        LegalTermsMarkdownDialog(
            activeRole = "client",
            termsClient = DEFAULT_TERMS_CLIENT,
            termsDriver = DEFAULT_TERMS_DRIVER,
            termsBusiness = DEFAULT_TERMS_BUSINESS,
            onDeleteAccount = {},
            onSignOut = { showTermsPreview = false },
            onDismiss = { showTermsPreview = false }
        )
    }
}

@Composable
fun ResolvitosSuperAppContainer(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val viewModel: ResolvitosViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                ResolvitosViewModel(
                    repository = ResolvitosRepository(db),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName,
                    userEmail = userEmail
                )
            }
        }
    )

    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val liveBcv by viewModel.liveBcvRate.collectAsStateWithLifecycle()
    val emergencies by viewModel.emergencyServices.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val activeChatContextId by viewModel.activeChatContextId.collectAsStateWithLifecycle()
    val activeChatTitle by viewModel.activeChatTitle.collectAsStateWithLifecycle()
    val isVoipActive by viewModel.isVoipCallActive.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }
    var showBcvCalculator by remember { mutableStateOf(false) }
    var showEmergencyDirectory by remember { mutableStateOf(false) }
    var showLegalModal by remember { mutableStateOf(false) }

    val rawRole = profile?.activeRole ?: "client"
    val activeRole = if (rawRole in listOf("client", "driver", "business")) rawRole else "client"
    val effectiveBcv = liveBcv ?: settings.bcvRate

    LaunchedEffect(activeRole) {
        showSplash = true
        delay(650L)
        showSplash = false
    }

    BackHandler(enabled = activeRole != "client") {
        viewModel.switchRole("client")
    }

    val handleRoleSwitchRequest: (String) -> Unit = { requestedRole ->
        val validRole = if (requestedRole in listOf("client", "driver", "business")) requestedRole else "client"
        viewModel.switchRole(validRole)
    }

    val handleSignOut: () -> Unit = {
        signOut(context, credentialManager, {}, scope)
    }

    if (showSplash) {
        ResolvitosSplashScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            AnimatedVisibility(visible = bannerMessage != null) {
                Surface(
                    color = StatusEmerald,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bannerMessage ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearBanner() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar aviso", tint = Color.White)
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeRole) {
                    "driver" -> DriverModeScreen(
                        viewModel = viewModel,
                        onRequestRoleSwitch = handleRoleSwitchRequest
                    )
                    "business" -> BusinessModeScreen(
                        viewModel = viewModel,
                        onRequestRoleSwitch = handleRoleSwitchRequest,
                        onSignOut = handleSignOut
                    )
                    else -> ClientModeScreen(
                        viewModel = viewModel,
                        onOpenBcvCalculator = { showBcvCalculator = true },
                        onOpenEmergencyDirectory = { showEmergencyDirectory = true },
                        onRequestRoleSwitch = handleRoleSwitchRequest,
                        onSignOut = handleSignOut
                    )
                }
            }
        }
    }

    if (showBcvCalculator) {
        BcvCalculatorDialog(
            bcvRate = effectiveBcv,
            onRefreshOfficialRate = { viewModel.refreshOfficialBcvRate() },
            onDismiss = { showBcvCalculator = false }
        )
    }

    if (showEmergencyDirectory) {
        EmergencyDirectoryDialog(
            services = emergencies,
            onDismiss = { showEmergencyDirectory = false }
        )
    }

    if (showLegalModal) {
        LegalTermsMarkdownDialog(
            activeRole = activeRole,
            termsClient = settings.termsClient,
            termsDriver = settings.termsDriver,
            termsBusiness = settings.termsBusiness,
            onDeleteAccount = {
                showLegalModal = false
                viewModel.deleteAccount(handleSignOut)
            },
            onSignOut = {
                showLegalModal = false
                handleSignOut()
            },
            onDismiss = { showLegalModal = false }
        )
    }

    if (activeChatContextId != null) {
        val contextMessages = messages.filter { it.contextId == activeChatContextId }
        UnifiedChatAndVoipDialog(
            title = activeChatTitle,
            messages = contextMessages,
            isVoipActive = isVoipActive,
            onSendMessage = { text, mediaType ->
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.CHAT_POP)
                viewModel.sendChatMessage(text, mediaType)
            },
            onStartVoipCall = {
                SoundAndHapticManager.trigger(context, ResolvitosSoundEvent.ALERT_LOOP)
                viewModel.startInAppVoipCall()
            },
            onEndVoipCall = { viewModel.endInAppVoipCall() },
            onDismiss = { viewModel.closeChat() }
        )
    }
}

// --- CREDENTIAL MANAGER GOOGLE SIGN-IN HELPERS ---

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onUnauthenticated()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (e: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onAuthError("Configuración de Google Sign-In no encontrada (default_web_client_id)")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context as Activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onAuthSuccess()
            } else {
                onAuthError("Credencial no reconocida")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Error al iniciar sesión con Google")
        }
    }
}

fun signOut(
    context: Context,
    credentialManager: CredentialManager,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    Firebase.auth.signOut()
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("Auth", "Failed to clear credential state", e)
        } finally {
            onSignOutComplete()
        }
    }
}
