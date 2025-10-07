package com.durranitech.salahsync.presentation.roleselection.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.R
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.ui.theme.Light_Green_For_Card
import com.durranitech.salahsync.ui.theme.Text_Light_Green


@Composable
fun RoleSelectionScreen(
    onRoleSelect: (UserRole) -> Unit,paddingValues: PaddingValues
) {

    // MainAuth ui

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painterResource(R.drawable.main_icon),
                    contentDescription = "Main Icon",
                    Modifier.height(96.dp)
                )
            }
        }
        Text(
            "SalahSync",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Text_Light_Green
        )
        Text(
            "Prayer Time Management for Mosques",
            style = MaterialTheme.typography.labelMedium,
            color = Text_Light_Green
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Section
            Text(
                text = "Choose Your Role", fontSize = 24.sp, color = Color(0xFF065F46), // emerald-800
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select your position in the mosque community",
                fontSize = 14.sp,
                color = Color(0xFF059669), // emerald-600
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Imam Card
            RoleCard(
                title = "Imam",
                description = "Prayer leader & mosque administrator",
                iconColor = Color(0xFFFFFFFF),
                gradient = Brush.horizontalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF0D9488))
                ),
                onClick = { onRoleSelect(UserRole.IMAM) })

            Spacer(modifier = Modifier.height(16.dp))

            // Muqtadi Card
            RoleCard(
                title = "Muqtadi",
                description = "Congregation member",
                iconColor = Color(0xFFFFFFFF),
                gradient = Brush.horizontalGradient(
                    listOf(Color(0xFF3B82F6), Color(0xFF4F46E5))
                ),
                onClick = { onRoleSelect(UserRole.MUQTADI) })

            // Info Section
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Light_Green_For_Card
                ), shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Imam: ")
                        }
                        append("Manager prayer times, announcements, and mosque activities")
                    }, color = Color(0xFF047857), fontSize = 12.sp, textAlign = TextAlign.Center)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Muqtadi: ")
                            }
                            append("View prayer times, announcements, and participate in community")
                        },
                        color = Color(0xFF047857),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(fontWeight = FontWeight.Bold),
                            ) {
                                append("New to SalahSync?\n")
                            }
                            append("Choose your role above, then sign up to create your account or use the demo account to explore.")
                        },
                        color = Color(0xFF047857),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 50.dp), contentAlignment = Alignment.BottomCenter){
            Text(
                "Empowering Masajid, strengthening our Ummah",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color =Text_Light_Green.copy(0.6f) ,
                letterSpacing = 0.2.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun RoleCard(
    title: String, description: String, iconColor: Color, gradient: Brush, onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.05f else 1f, label = "scaleAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradient)
            .clickable(
                onClick = onClick,
                onClickLabel = "Select $title role",
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = if (title == "Imam") Icons.Default.Person else Icons.Outlined.People,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/*
@Preview
@Composable
private fun RolePreview() {
    RoleSelectionScreen(onRoleSelect = { })
}
*/
