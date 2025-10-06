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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.R
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen
import com.durranitech.salahsync.ui.theme.Text_Light_Green

@Composable
fun MainAuthScreen(selectedScreen: String, paddingValues: PaddingValues) {
    val verticalScrol = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrol)
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

        when (selectedScreen) {
            "RoleSelection" -> RoleSelectionScreen(onRoleSelect = {})
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

@Preview
@Composable
private fun MainSignInScreenPrev() {
    MainAuthScreen("RoleSelection", PaddingValues(16.dp))
}