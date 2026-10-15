package com.soulinc.stockhpcomposestarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.soulinc.stockhpcomposestarter.presentation.StockListScreen
import com.soulinc.stockhpcomposestarter.ui.theme.StockHpComposeStarterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HomeContainerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockHpComposeStarterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StockListScreen(
                        vm = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
