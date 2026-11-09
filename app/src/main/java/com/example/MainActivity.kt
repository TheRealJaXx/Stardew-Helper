package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WalnutScreen
import com.example.ui.WalnutViewModel
import com.example.ui.theme.StardewHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StardewHelperTheme {
                val viewModel: WalnutViewModel = viewModel()
                WalnutScreen(viewModel = viewModel)
            }
        }
    }
}
