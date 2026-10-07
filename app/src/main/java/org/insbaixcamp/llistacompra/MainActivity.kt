package org.insbaixcamp.llistacompra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import org.insbaixcamp.llistacompra.screens.ListsScreen
import org.insbaixcamp.llistacompra.ui.theme.LlistaCompraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LlistaCompraTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {

    var loggedIn by remember { mutableStateOf(false) }
    var showRegister by remember { mutableStateOf(false) }

    if (loggedIn) {

        // Usuario ha iniciado sesión
        ListsScreen()

    } else {

        if (showRegister) {

            RegisterScreen(
                onRegisterSuccess = {
                    loggedIn = true
                }
            )

        } else {

            LoginScreen(
                onLoginSuccess = {
                    loggedIn = true
                },
                onRegisterClick = {
                    showRegister = true
                }
            )
        }
    }
}