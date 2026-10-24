package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChatMessage
import com.example.data.model.EmergencyService
import com.example.data.model.UserProfile
import com.example.ui.theme.ResolvitosSlate
import com.example.ui.theme.ResolvitosYellow
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCoral
import com.example.ui.theme.StatusEmerald
import java.util.Locale

val BrightYellow = Color(0xFFFFEA00)

/**
 * Official Logo Badge for "Resolvitos Taxi Deliverys - Encuentra lo que quieras"
 * Replaces the "Un 2x3" badge in all headers, splash screens, and footers.
 */
@Composable
fun ResolvitosLogoBadge(
    modifier: Modifier = Modifier,
    boxSize: Dp = 42.dp,
    showTaglineRight: Boolean = false,
    darkText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .clip(RoundedCornerShape(boxSize * 0.24f))
                .background(BrightYellow)
                .border(1.dp, ResolvitosSlate.copy(alpha = 0.12f), RoundedCornerShape(boxSize * 0.24f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ResolvitosSlate,
                        modifier = Modifier.size(boxSize * 0.28f)
                    )
                    Text(
                        text = "Resolvitos",
                        color = ResolvitosSlate,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (boxSize.value * 0.19f).sp,
                        lineHeight = (boxSize.value * 0.20f).sp,
                        maxLines = 1
                    )
                }
                Text(
                    text = "TAXI DELIVERYS",
                    color = ResolvitosSlate,
                    fontWeight = FontWeight.Black,
                    fontSize = (boxSize.value * 0.13f).sp,
                    lineHeight = (boxSize.value * 0.14f).sp,
                    maxLines = 1
                )
            }
        }

        if (showTaglineRight) {
            Column {
                Text(
                    text = "Resolvitos",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    lineHeight = 15.sp,
                    color = if (darkText) ResolvitosSlate else Color.White
                )
                Text(
                    text = "Encuentra lo que Quieras",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    color = if (darkText) ResolvitosSlate.copy(alpha = 0.85f) else BrightYellow
                )
            }
        }
    }
}

/**
 * Full-screen yellow splash screen matching the opening of Video 1, Video 2, and Video 3.
 */
@Composable
fun ResolvitosSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightYellow),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(168.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(BrightYellow)
                    .border(3.dp, ResolvitosSlate, RoundedCornerShape(36.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ResolvitosSlate,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = "Resolvitos",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = ResolvitosSlate
                        )
                    }
                    Text(
                        text = "TAXI DELIVERYS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = ResolvitosSlate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✦ Encuentra lo que quieras ✦",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = ResolvitosSlate
                    )
                }
            }
            Text(
                text = "¡En Resolvitos lo tienes!",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ResolvitosSlate
            )
        }
    }
}

/**
 * SUICHE DE APLICACIÓN (Multi-Role Switcher inside Settings / Configuraciones)
 * Allows switching seamlessly between Usuario (Cliente), Conductor (Centro de Comandas),
 * Administrador de Negocios (Business Hub), and Superadmin CPanel.
 */
@Composable
fun SettingsRoleSwitcherCard(
    activeRole: String,
    onSelectRole: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, BrightYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrightYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = ResolvitosSlate
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Suiche de Aplicación (Multi-Rol)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResolvitosSlate
                    )
                    Text(
                        text = "Cambia al instante entre Usuario, Conductor o Administrador de Negocios",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Switch 1: Modo Usuario (Cliente)
            RoleSwitchRowItem(
                title = "App Usuario (Cliente)",
                subtitle = "Resolvitos • Encuentra lo que quieras",
                icon = Icons.Default.Person,
                checked = activeRole == "client",
                onToggle = { onSelectRole("client") },
                tag = "role_tab_client"
            )

            // Switch 2: Modo Conductor (Video 2)
            RoleSwitchRowItem(
                title = "App Conductor (Piloto)",
                subtitle = "Resolvitos • Centro de Comandas y Radar",
                icon = Icons.Default.DirectionsCar,
                checked = activeRole == "driver",
                onToggle = {
                    if (activeRole == "driver") onSelectRole("client") else onSelectRole("driver")
                },
                tag = "role_tab_driver"
            )

            // Switch 3: Modo Administrador de Negocios (Video 3)
            RoleSwitchRowItem(
                title = "Administrador de Negocios",
                subtitle = "Resolvitos Negocios • Business Hub",
                icon = Icons.Default.Storefront,
                checked = activeRole == "business",
                onToggle = {
                    if (activeRole == "business") onSelectRole("client") else onSelectRole("business")
                },
                tag = "role_tab_business"
            )
        }
    }
}

@Composable
private fun RoleSwitchRowItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) Color(0xFFFFFBEB) else Color(0xFFF8FAFC))
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (checked) BrightYellow else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ResolvitosSlate,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ResolvitosSlate
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ResolvitosSlate,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

/**
 * Modal "Mis Puntos y Premios - PROGRAMA DE LEALTAD RESOLVITOS" (Video 1, 00:58)
 */
@Composable
fun MyPointsAndPrizesModal(
    points: Long,
    onOpenRewardsCatalog: () -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏅 Mis Puntos y\nPremios",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "PROGRAMA DE LEALTAD RESOLVITOS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                        }
                    }
                }

                // Gold card "NIVEL ORO"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "NIVEL ORO 🎉",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = "TUS PUNTOS ACUMULADOS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$points",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PUNTOS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = "$1 gastado = 1 punto acumulado",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🎁 Tus puntos acumulan beneficios",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "Pagas directo al conductor o comercio. Con cada compra y viaje acumulas puntos para cumplir misiones de cada negocio y desbloquear premios.",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        onOpenRewardsCatalog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightYellow,
                        contentColor = ResolvitosSlate
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "✨ Ver Mis Puntos y Recompensas",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Modal "Editar Perfil - PERSONALIZA TU INFORMACIÓN" (Video 1, 00:50 - 00:54)
 */
@Composable
fun EditProfileModal(
    profile: UserProfile?,
    onSave: (String, String, String, String, String, String) -> Unit,
    onDeleteAccount: () -> Unit,
    onDismiss: () -> Unit
) {
    var fullName by remember(profile) { mutableStateOf(profile?.displayName ?: "Louis Naranjo") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "4127786837") }
    var cedulaType by remember(profile) { mutableStateOf(profile?.cedulaType ?: "V") }
    var cedulaNumber by remember(profile) { mutableStateOf(profile?.cedulaNumber ?: "26177935") }
    var birthDate by remember(profile) { mutableStateOf(profile?.birthDate ?: "17/10/1997") }
    var gender by remember(profile) { mutableStateOf(profile?.gender ?: "MASCULINO") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ResolvitosSlate)
                    }
                    Column {
                        Text(
                            text = "Editar Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResolvitosSlate
                        )
                        Text(
                            text = "PERSONALIZA TU INFORMACIÓN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                Text(
                    text = "👤 INFORMACIÓN BÁSICA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8)
                )

                Text("NOMBRE COMPLETO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("NÚMERO DE CELULAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
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
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
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
                Text("10 dígitos sin el cero inicial", fontSize = 10.sp, color = Color(0xFF94A3B8))

                Text("CÉDULA DE IDENTIDAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp)
                    ) {
                        listOf("V", "E").forEach { type ->
                            val selected = cedulaType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) BrightYellow else Color.Transparent)
                                    .clickable { cedulaType = type }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ResolvitosSlate
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = cedulaNumber,
                        onValueChange = { cedulaNumber = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("📅 FECHA DE CUMPLEAÑOS 🎂", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("GÉNERO (PARA PERSONALIZACIÓN)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("MASCULINO", "FEMENINO").forEach { g ->
                        val selected = gender == g
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Color.White else Color.Transparent)
                                .border(
                                    width = if (selected) 1.dp else 0.dp,
                                    color = if (selected) Color(0xFFE2E8F0) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { gender = g }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = g,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (selected) Color(0xFFD97706) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Text("📷 INFORMACIÓN VISUAL • FOTO DE PERFIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ResolvitosLogoBadge(boxSize = 68.dp, showTaglineRight = false)
                        Text(
                            text = "RECOMENDADO: 400X400PX (JPG/PNG)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeleteAccount() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = StatusCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ELIMINAR CUENTA",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = StatusCoral
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("CANCELAR", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }

                    Button(
                        onClick = {
                            onSave(fullName, phone, cedulaType, cedulaNumber, birthDate, gender)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "GUARDAR\nCAMBIOS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Filter modal "Filtros - SECTOR DE NEGOCIO" matching Video 1 (00:21)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BusinessSectorFilterDialog(
    selectedSector: String,
    onApplySector: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sectors = listOf(
        "Restaurantes",
        "Panaderías & Pastelerías",
        "Supermercados",
        "Ferreterías",
        "Motorepuestos",
        "Autopartes",
        "Concesionarios",
        "Farmacias",
        "Bebidas & Licores",
        "Tecnología",
        "Hogar & Muebles",
        "Moda & Calzado",
        "Salud y Belleza",
        "Mascotas & Veterinaria"
    )
    var currentSelection by remember(selectedSector) { mutableStateOf(selectedSector) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎚️ Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResolvitosSlate
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = "SECTOR DE NEGOCIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sectors.forEach { sector ->
                        val isSelected = currentSelection == sector
                        Surface(
                            color = if (isSelected) BrightYellow else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ResolvitosSlate else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.clickable {
                                currentSelection = if (isSelected) "Todos" else sector
                            }
                        ) {
                            Text(
                                text = sector,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable {
                                currentSelection = "Todos"
                                onApplySector("Todos")
                                onDismiss()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🗑️ Limpiar", fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        }
                    }

                    Button(
                        onClick = {
                            onApplySector(currentSelection)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightYellow,
                            contentColor = ResolvitosSlate
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                    ) {
                        Text("Aplicar Filtros", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun BcvCalculatorDialog(
    bcvRate: Double,
    onRefreshOfficialRate: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var usdInput by remember { mutableStateOf("10.00") }
    var bsInput by remember(bcvRate) {
        mutableStateOf(String.format(Locale.US, "%.2f", 10.0 * bcvRate))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = ResolvitosSlate)
        },
        title = {
            Text(
                text = "Calculadora Oficial BCV",
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        Column {
                            Text(
                                text = "Tasa Oficial BCV (Venezuela)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "1 USD = ${String.format(Locale.US, "%.2f", bcvRate)} Bs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ResolvitosSlate
                            )
                        }
                        IconButton(onClick = onRefreshOfficialRate) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Actualizar desde BCV",
                                tint = ResolvitosSlate
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = usdInput,
                    onValueChange = { newUsd ->
                        usdInput = newUsd
                        val parsed = newUsd.toDoubleOrNull() ?: 0.0
                        bsInput = String.format(Locale.US, "%.2f", parsed * bcvRate)
                    },
                    label = { Text("Monto en Dólares ($ USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bcv_usd_input")
                )

                OutlinedTextField(
                    value = bsInput,
                    onValueChange = { newBs ->
                        bsInput = newBs
                        val parsed = newBs.toDoubleOrNull() ?: 0.0
                        val usd = if (bcvRate > 0) parsed / bcvRate else 0.0
                        usdInput = String.format(Locale.US, "%.2f", usd)
                    },
                    label = { Text("Monto en Bolívares (Bs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bcv_bs_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightYellow,
                    contentColor = ResolvitosSlate
                )
            ) {
                Text("Listo", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun EmergencyDirectoryDialog(
    services: List<EmergencyService>,
    onDismiss: () -> Unit
) {
    var activeSosAlert by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(StatusCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Asistencia 24 Horas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ResolvitosSlate
                            )
                            Text(
                                text = "Ambulancias, Bomberos, Seguridad y Grúas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                if (activeSosAlert != null) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = activeSosAlert!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusCoral,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(services, key = { it.id }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ResolvitosSlate
                                    )
                                    Text(
                                        text = "${item.category} • ${item.scope} • Tel: ${item.phone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF475569)
                                    )
                                }
                                Button(
                                    onClick = {
                                        activeSosAlert = "🚨 Alerta S.O.S. y coordenadas GPS enviadas a ${item.name} (${item.phone})"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusCoral,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("sos_call_${item.id}")
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Llamar", fontWeight = FontWeight.Bold)
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
fun AdminPinGateDialog(
    onVerifyPin: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = ResolvitosSlate)
        },
        title = {
            Text("Acceso CPanel Superadmin", fontWeight = FontWeight.ExtraBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ingresa el PIN de seguridad de Superadministrador (Por defecto: 2026):",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        pinInput = it
                        hasError = false
                    },
                    label = { Text("PIN de Seguridad") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    isError = hasError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )
                if (hasError) {
                    Text(
                        text = "PIN incorrecto. Intenta con 2026.",
                        color = StatusCoral,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onVerifyPin(pinInput.trim())
                    if (!ok) hasError = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightYellow,
                    contentColor = ResolvitosSlate
                ),
                modifier = Modifier.testTag("admin_pin_confirm_button")
            ) {
                Text("Desbloquear CPanel", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun LegalTermsMarkdownDialog(
    activeRole: String,
    termsClient: String,
    termsDriver: String,
    termsBusiness: String,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(activeRole.takeIf { it in listOf("client", "driver", "business") } ?: "client") }
    var confirmDelete by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 580.dp)
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
                    Text(
                        text = "Términos, Privacidad y Cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResolvitosSlate
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "client" to "Clientes",
                        "driver" to "Conductores",
                        "business" to "Comercios"
                    ).forEach { (key, label) ->
                        val isSelected = selectedTab == key
                        Surface(
                            color = if (isSelected) BrightYellow else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.clickable { selectedTab = key }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ResolvitosSlate,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    val textToShow = when (selectedTab) {
                        "driver" -> termsDriver
                        "business" -> termsBusiness
                        else -> termsClient
                    }
                    Text(
                        text = textToShow.trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = ResolvitosSlate,
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Text("Cerrar Sesión")
                    }

                    if (!confirmDelete) {
                        Button(
                            onClick = { confirmDelete = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusCoral,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("delete_account_trigger_button")
                        ) {
                            Text("Eliminar Cuenta")
                        }
                    } else {
                        Button(
                            onClick = onDeleteAccount,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusCoral,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("confirm_delete_account_button")
                        ) {
                            Text("Confirmar Borrado Total")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnifiedChatAndVoipDialog(
    title: String,
    messages: List<ChatMessage>,
    isVoipActive: Boolean,
    onSendMessage: (String, String) -> Unit,
    onStartVoipCall: () -> Unit,
    onEndVoipCall: () -> Unit,
    onDismiss: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dark Header matching Video 4 (01:02 - 01:05)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ResolvitosSlate)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                                    .clip(CircleShape)
                                    .background(BrightYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title.take(1).uppercase().ifBlank { "R" },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = ResolvitosSlate
                                )
                            }
                            Column {
                                Text(
                                    text = title.ifBlank { "Chat con el Cliente" },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(StatusEmerald)
                                    )
                                    Text(
                                        text = if (isVoipActive) "LLAMADA VOIP CONECTADA" else "EN LÍNEA • SERVICIO ACTIVO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusEmerald,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { if (isVoipActive) onEndVoipCall() else onStartVoipCall() },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isVoipActive) StatusCoral else StatusEmerald)
                                    .testTag("voip_call_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isVoipActive) Icons.Default.CallEnd else Icons.Default.Call,
                                    contentDescription = "Llamada VoIP",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar chat", tint = Color.White)
                            }
                        }
                    }

                    // Route Banner: 🟢 PUNTO A ───🛵─── 🟡 DESTINO
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🟢 PUNTO A",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusEmerald
                            )
                            Text(
                                text = "────── 🛵 ──────",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "🟡 DESTINO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BrightYellow
                            )
                        }
                    }
                }

                // Messages List + Safety Notice
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "🛡️ Norma de Seguridad Resolvitos",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = ResolvitosSlate
                                )
                                Text(
                                    text = "Coordina aquí tu punto de recogida exacto o detalles del servicio. Nunca compartas claves ni códigos por chat.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(messages, key = { it.id }) { msg ->
                        val isDriverMsg = msg.senderRole == "driver"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isDriverMsg) Arrangement.Start else Arrangement.End
                        ) {
                            Surface(
                                color = if (isDriverMsg) Color.White else ResolvitosSlate,
                                shape = RoundedCornerShape(16.dp),
                                border = if (isDriverMsg) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = "${msg.senderName} • ${if (isDriverMsg) "PILOTO" else "CLIENTE"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = if (isDriverMsg) Color(0xFF64748B) else BrightYellow
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 13.sp,
                                        color = if (isDriverMsg) ResolvitosSlate else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Input Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSendMessage("🎤 Nota de voz (0:06s): Estoy en la entrada principal", "audio") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nota de Voz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onSendMessage("📷 Foto de punto de referencia enviada", "image") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enviar Foto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Escribe un mensaje...") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input")
                        )
                        Button(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    onSendMessage(messageInput.trim(), "text")
                                    messageInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrightYellow,
                                contentColor = ResolvitosSlate
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                        }
                    }
                }
            }
        }
    }
}
