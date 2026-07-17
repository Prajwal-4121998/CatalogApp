package com.example.catalogapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.navigation.CatalogNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CatalogTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> }
                CatalogNavHost()
            }
        }
    }
}
