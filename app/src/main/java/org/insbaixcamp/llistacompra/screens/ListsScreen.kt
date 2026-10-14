
package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import org.insbaixcamp.llistacompra.data.FirebaseRepository

@Composable
fun ListsScreen() {

    val repository = remember { FirebaseRepository() }
    val usuariId = FirebaseAuth.getInstance().currentUser?.uid

    var nomLlista by remember { mutableStateOf("") }
    var llistaSeleccionada by remember {
        mutableStateOf<String?>(null)
    }
    var mostrarDialog by remember { mutableStateOf(false) }
    var guardant by remember { mutableStateOf(false) }
    var llistes by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorLlistes by remember { mutableStateOf<String?>(null) }
    var errorCreacio by remember { mutableStateOf<String?>(null) }

    // Escuchar las listas guardadas en Firebase
    DisposableEffect(usuariId) {

        val listener = if (usuariId != null) {
            repository.escoltarLlistes(
                usuariId = usuariId,
                onUpdate = {
                    llistes = it
                    errorLlistes = null
                },
                onError = {
                    errorLlistes = it
                }
            )
        } else {
            null
        }

        onDispose {
            listener?.remove()
        }
    }

    if (llistaSeleccionada != null) {
        DetallLlistaScreen(
            nomLlista = llistaSeleccionada!!,
            onTornar = {
                llistaSeleccionada = null
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Les meves llistes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                mostrarDialog = true
                errorCreacio = null
            },
            enabled = usuariId != null
        ) {
            Text("+ Nova llista")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (usuariId == null) {
            Text("No hi ha cap usuari autenticat")
        }

        errorLlistes?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Mostrar las listas
        if (llistes.isEmpty() && errorLlistes == null) {
            Text("Encara no tens cap llista")
        }

        llistes.forEach { nom ->
            TextButton(
                onClick = {
                    llistaSeleccionada = nom
                }
            ) {
                Text(nom)
            }
        }
    }

    // Ventana para crear una lista
    if (mostrarDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!guardant) mostrarDialog = false
            },

            title = {
                Text("Crear nova llista")
            },

            text = {
                Column {
                    OutlinedTextField(
                        value = nomLlista,
                        onValueChange = {
                            nomLlista = it
                        },
                        label = {
                            Text("Nom de la llista")
                        }
                    )

                    errorCreacio?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {
                Button(
                    enabled = !guardant && nomLlista.isNotBlank(),
                    onClick = {

                        if (usuariId != null) {
                            guardant = true
                            errorCreacio = null

                            repository.crearLlista(
                                nom = nomLlista.trim(),
                                propietariId = usuariId
                            ) { correcte, error ->

                                guardant = false

                                if (correcte) {
                                    mostrarDialog = false
                                    nomLlista = ""
                                } else {
                                    errorCreacio = error
                                        ?: "Error al crear la llista"
                                }
                            }
                        }
                    }
                ) {
                    Text(if (guardant) "Guardant..." else "Crear")
                }
            },

            dismissButton = {
                TextButton(
                    enabled = !guardant,
                    onClick = {
                        mostrarDialog = false
                    }
                ) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}
