package com.durranitech.salahsync.presentation.roleselection.screens

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.R
import com.durranitech.salahsync.domain.model.UserRole
import kotlinx.coroutines.delay

// ─── Design tokens ────────────────────────────────────────────────────────────
private val GreenDeep       = Color(0xFF065F46)
private val GreenPrimary    = Color(0xFF1B8C5E)
private val GreenLight      = Color(0xFF26A874)
private val GreenSurface    = Color(0xFFE8F5EF)
private val GreenGlow       = Color(0xFF34D399)
private val IndigoPrimary   = Color(0xFF3730A3)
private val IndigoLight     = Color(0xFF6366F1)
private val IndigoSurface   = Color(0xFFEEF2FF)
private val IndigoGlow      = Color(0xFF818CF8)

@Composable
fun RoleSelectionScreen(
    onRoleSelect: (UserRole) -> Unit,
    paddingValues: PaddingValues
) {
    // Staggered entrance
    var heroVisible  by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }
    var infoVisible  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        heroVisible  = true
        delay(180); cardsVisible = true
        delay(160); infoVisible  = true
    }

    val heroAlpha by animateFloatAsState(
        targetValue = if (heroVisible) 1f else 0f,
        animationSpec = tween(500), label = "hero_alpha"
    )
    val heroScale by animateFloatAsState(
        targetValue = if (heroVisible) 1f else 0.88f,
        animationSpec = tween(600, easing = EaseOutBack), label = "hero_scale"
    )
    val cardsAlpha by animateFloatAsState(
        targetValue = if (cardsVisible) 1f else 0f,
        animationSpec = tween(500), label = "cards_alpha"
    )
    val cardsOffset by animateFloatAsState(
        targetValue = if (cardsVisible) 0f else 20f,
        animationSpec = tween(500, easing = EaseOutCubic), label = "cards_offset"
    )
    val infoAlpha by animateFloatAsState(
        targetValue = if (infoVisible) 1f else 0f,
        animationSpec = tween(400), label = "info_alpha"
    )

    // Ambient pulse
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val glowA by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ga"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background ambient blobs
        Box(
            modifier = Modifier.size(280.dp).align(Alignment.TopEnd)
                .blur(80.dp).background(GreenGlow.copy(alpha = glowA * 0.28f), CircleShape)
        )
        Box(
            modifier = Modifier.size(220.dp).align(Alignment.BottomStart)
                .blur(70.dp).background(IndigoGlow.copy(alpha = glowA * 0.22f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Hero section ──────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(heroAlpha).scale(heroScale)
            ) {
                // App icon with layered glow rings
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(110.dp)
                            .background(GreenGlow.copy(alpha = glowA * 0.2f), CircleShape)
                    )
                    Box(
                        modifier = Modifier.size(84.dp)
                            .background(GreenPrimary.copy(alpha = 0.1f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(listOf(Color.White, Color(0xFFF0FDF4)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.main_icon),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "SalahSync",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenDeep,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Prayer time management for masajid",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenPrimary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                // Divider with dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = GreenPrimary.copy(alpha = 0.2f))
                    Box(Modifier.size(4.dp).background(GreenLight, CircleShape))
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = GreenPrimary.copy(alpha = 0.2f))
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Role cards ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .alpha(cardsAlpha)
                    .padding(top = cardsOffset.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section header
                Text(
                    "Choose your role",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Select your position in the mosque community to get started.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(4.dp))

                // Imam card
                RoleCard(
                    title       = "Imam",
                    description = "Prayer leader & mosque administrator",
                    features    = listOf("Manage prayer times", "Post announcements", "Manage members"),
                    gradient    = listOf(GreenPrimary, GreenLight, Color(0xFF3FC28A)),
                    glowColor   = GreenGlow,
                    icon        = Icons.Outlined.Person,
                    onClick     = { onRoleSelect(UserRole.IMAM) }
                )

                // Muqtadi card
                RoleCard(
                    title       = "Muqtadi",
                    description = "Congregation member & worshipper",
                    features    = listOf("View prayer times", "Receive announcements", "Join a masjid"),
                    gradient    = listOf(IndigoPrimary, IndigoLight, Color(0xFF818CF8)),
                    glowColor   = IndigoGlow,
                    icon        = Icons.Outlined.Groups,
                    onClick     = { onRoleSelect(UserRole.MUQTADI) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Info box ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .alpha(infoAlpha)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("New to SalahSync?\n")
                        }
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            fontSize = 12.sp
                        )) {
                            append("Choose your role, then sign up or explore with a demo account to get started.")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Empowering masajid, strengthening our Ummah",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 28.dp)
            )
        }
    }
}

// ─── Role Card ────────────────────────────────────────────────────────────────

@Composable
private fun RoleCard(
    title: String,
    description: String,
    features: List<String>,
    gradient: List<Color>,
    glowColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(100), label = "press_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    val cardGlow by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "card_glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(gradient))
            .clickable(
                onClick = {
                    pressed = true
                    onClick()
                },
                onClickLabel = "Select $title role"
            )
            .padding(18.dp)
    ) {
        // Decorative circle top-right
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )
        // Smaller circle bottom-left
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-15).dp, y = 15.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold)
                    Text(description, color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ChevronRight, null, tint = Color.White,
                        modifier = Modifier.size(18.dp))
                }
            }

            // Thin separator
            HorizontalDivider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.25f))

            // Feature pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                features.forEach { feature ->
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            feature,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
