package com.durranitech.salahsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.navigation.root.MainRoute
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalahSyncTheme {
                Scaffold { paddingValues ->
                   MainRoute(
                       paddingValues
                   )
                }

            }
        }
    }
}