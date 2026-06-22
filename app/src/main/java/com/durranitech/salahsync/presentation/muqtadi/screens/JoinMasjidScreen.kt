package com.durranitech.salahsync.presentation.muqtadi.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.ui.theme.SalahSyncTheme

private val BluePrimary = Color(0xFF1E40AF)
private val BlueLight = Color(0xFF3B82F6)
private val BlueSurface = Color(0xFFEFF6FF)
private val GreenPrimary = Color(0xFF1B8C5E)

@Composable
fun JoinMasjidScreen(
    onJoinMasjid: () -> Unit,
    onScanQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Icon ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(BlueSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mosque,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = BlueLight
                )
            }

            Spacer(Modifier.height(22.dp))

            // ── Title ───────────────────────────────────────────────────────
            Text(
                text = "No masjid joined yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Join a masjid to see prayer times and\nannouncements from your community.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Primary: Scan QR ────────────────────────────────────────────
            Button(
                onClick = onScanQr,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Scan QR code",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── "or" divider ────────────────────────────────────────────────
            OrDivider()

            Spacer(Modifier.height(12.dp))

            // ── Secondary: Browse list ───────────────────────────────────────
            OutlinedButton(
                onClick = onJoinMasjid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                // ✅ OutlinedButtonDefaults.colors() — correct API

                border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Find a masjid",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            // ✅ NO second OutlinedButton below — that was the duplicate causing the issue
        }
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
        Text(
            text = "or",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun JoinMasjidScreenPreview() {
    SalahSyncTheme {
        JoinMasjidScreen(onJoinMasjid = {}, onScanQr = {})
    }
}