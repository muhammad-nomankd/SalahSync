package com.durranitech.salahsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.durranitech.salahsync.ui.theme.SalahSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalahSyncTheme {
                Scaffold { paddingValues ->
                    Text("Hello Noma n")
                }

            }
        }
    }
}