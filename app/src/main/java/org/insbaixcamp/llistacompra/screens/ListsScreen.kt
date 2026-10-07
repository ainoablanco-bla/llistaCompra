package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListsScreen() {

    var nomLlista by remember {
        mutableStateOf("")
    }

    var mostrarDialog by remember {
        mutableStateOf(false)
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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                mostrarDialog = true
            }
        ) {
            Text("+ Nova llista")
        }
    }

    if (mostrarDialog) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialog = false
            },

            title = {
                Text("Crear nova llista")
            },

            text = {

                OutlinedTextField(
                    value = nomLlista,

                    onValueChange = {
                        nomLlista = it
                    },

                    label = {
                        Text("Nom de la llista")
                    }
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (nomLlista.isNotBlank()) {

                            // Más adelante aquí guardaremos en Firebase

                            mostrarDialog = false

                            nomLlista = ""
                        }
                    }
                ) {

                    Text("Crear")
                }
            },

            dismissButton = {

                TextButton(
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