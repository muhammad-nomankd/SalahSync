package com.durranitech.salahsync.presentation.authentication.screens

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.R
import kotlinx.coroutines.delay

private val SplashBg        = Color(0xFFF0FDF4)
private val GreenDeep       = Color(0xFF065F46)
private val GreenPrimary    = Color(0xFF1B8C5E)
private val GreenMid        = Color(0xFF059669)
private val GreenLight      = Color(0xFF26A874)
private val GreenPale       = Color(0xFF86EFAC)
private val GreenGlow       = Color(0xFF34D399)
val ImamAccent              = Color(0xFF3FC28A)

@Composable
fun SplashScreen() {
    var iconVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var bottomVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100); iconVisible = true
        delay(300); textVisible = true
        delay(200); bottomVisible = true
    }

    val iconScale by animateFloatAsState(
        targetValue   = if (iconVisible) 1f else 0.5f,
        animationSpec = tween(600, easing = EaseOutBack),
        label         = "icon_scale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue   = if (iconVisible) 1f else 0f,
        animationSpec = tween(400),
        label         = "icon_alpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue   = if (textVisible) 1f else 0f,
        animationSpec = tween(500),
        label         = "text_alpha"
    )
    val textOffset by animateFloatAsState(
        targetValue   = if (textVisible) 0f else 20f,
        animationSpec = tween(500, easing = EaseOut),
        label         = "text_offset"
    )

    val bottomAlpha by animateFloatAsState(
        targetValue   = if (bottomVisible) 1f else 0f,
        animationSpec = tween(500),
        label         = "bottom_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 0.7f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SplashBg, Color(0xFFDCFCE7), SplashBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .blur(80.dp)
                .background(GreenGlow.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .blur(60.dp)
                .background(GreenPrimary.copy(alpha = 0.12f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(iconScale)
                    .alpha(iconAlpha)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(glowScale)
                        .background(
                            GreenGlow.copy(alpha = glowAlpha * 0.25f),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            GreenPrimary.copy(alpha = 0.12f),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color.White, Color(0xFFF0FDF4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter           = painterResource(R.drawable.main_icon),
                        contentDescription = "App Logo",
                        modifier          = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(top = textOffset.dp)
            ) {
                Text(
                    text          = "SalahSync",
                    style         = MaterialTheme.typography.headlineLarge,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = GreenDeep,
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text      = "Prayer time management for masajid",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = GreenMid,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PulsingDots()
            Text(
                text          = "Empowering masajid · Strengthening our Ummah",
                style         = MaterialTheme.typography.labelSmall,
                color         = GreenPrimary.copy(alpha = 0.6f),
                textAlign     = TextAlign.Center,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
private fun PulsingDots() {
    val transition = rememberInfiniteTransition(label = "loading_dots")

    val scales = (0..2).map { i ->
        val scale by transition.animateFloat(
            initialValue  = 0.6f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(
                animation  = tween(600, delayMillis = i * 150, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_scale_$i"
        )
        scale
    }

    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        scales.forEachIndexed { i, scale ->
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(if (scale > 0.8f) GreenPrimary else GreenPale)
            )
        }
    }
}