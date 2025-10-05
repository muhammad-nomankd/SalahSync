package com.durranitech.salahsync.presentation.authentication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.durranitech.salahsync.R
import com.durranitech.salahsync.presentation.roleselection.RoleSelectionScreen
import com.durranitech.salahsync.ui.theme.Dark_Green
import com.durranitech.salahsync.ui.theme.Light_Green
import com.durranitech.salahsync.ui.theme.green_text

@Composable
fun MainAuthScreen(selectedScreen: String, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Light_Green, Dark_Green)
                )
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
            color = green_text
        )
        Text(
            "Prayer Time Management for Mosques",
            style = MaterialTheme.typography.labelMedium,
            color = green_text
        )

        when (selectedScreen) {
            "RoleSelection" -> RoleSelectionScreen(onRoleSelect = {})
        }


    }
}