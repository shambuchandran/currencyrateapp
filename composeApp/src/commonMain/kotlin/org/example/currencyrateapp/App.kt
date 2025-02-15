package org.example.currencyrateapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import di.initializeKoin
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screen.HomeScreen
import ui.theme.darkScheme
import ui.theme.lightScheme

@Composable
@Preview
fun App() {
//    val colors = if(!isSystemInDarkTheme()) lightScheme else darkScheme
//    MaterialTheme(colorScheme = colors) {
//
//    }

    initializeKoin()

    MaterialTheme {
        Navigator(HomeScreen())
    }
}