package org.insbaixcamp.llistacompra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import org.insbaixcamp.llistacompra.data.FirebaseRepository
import org.insbaixcamp.llistacompra.screens.ListsScreen
import org.insbaixcamp.llistacompra.screens.LoginScreen
import org.insbaixcamp.llistacompra.screens.RegisterScreen
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
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var showRegister by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        }
    }

    if (currentUser != null) {
        val user = currentUser!!
        LaunchedEffect(user.uid) {
            val repository = FirebaseRepository()
            repository.guardarUsuari(
                uid = user.uid,
                email = user.email ?: ""
            )
        }

        ListsScreen(
            onLogout = {
                currentUser = null
            }
        )
    } else {
        if (showRegister) {
            RegisterScreen(
                onRegisterSuccess = {
                    currentUser = FirebaseAuth.getInstance().currentUser
                }
            )
        } else {
            LoginScreen(
                onLoginSuccess = {
                    currentUser = FirebaseAuth.getInstance().currentUser
                },
                onRegisterClick = {
                    showRegister = true
                }
            )
        }
    }
}






