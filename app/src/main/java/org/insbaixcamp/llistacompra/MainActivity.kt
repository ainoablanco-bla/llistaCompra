
package org.insbaixcamp.llistacompra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import org.insbaixcamp.llistacompra.screens.ListsScreen
import org.insbaixcamp.llistacompra.screens.LoginScreen
import org.insbaixcamp.llistacompra.screens.RegisterScreen
import org.insbaixcamp.llistacompra.screens.DetallLlistaScreen
import org.insbaixcamp.llistacompra.screens.CategoriesScreen
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

    // Pantalla que es mostra
    var pantalla by remember { mutableStateOf("llistes") }

    // Dades de la llista seleccionada
    var idLlista by remember { mutableStateOf("") }
    var nomLlista by remember { mutableStateOf("") }

    if (loggedIn) {

        when (pantalla) {

            "llistes" -> {
                ListsScreen()
            }

            "detall" -> {
                DetallLlistaScreen(
                    idLlista = idLlista,
                    nomLlista = nomLlista,
                    onTornar = {
                        pantalla = "llistes"
                    },
                    onAnarCategories = {
                        pantalla = "categories"
                    }
                )
            }

            "categories" -> {
                CategoriesScreen(
                    llistaId = idLlista,
                    onTornar = {
                        pantalla = "detall"
                    }
                )
            }
        }

    } else {

        if (showRegister) {
            RegisterScreen {
                loggedIn = true
            }
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

@Composable
fun AppPreview() {
    LlistaCompraTheme {
        App()
    }
}
