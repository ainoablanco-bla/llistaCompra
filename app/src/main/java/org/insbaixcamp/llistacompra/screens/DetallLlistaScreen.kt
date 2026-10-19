package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.insbaixcamp.llistacompra.data.FirebaseRepository

@Composable
fun DetallLlistaScreen(
    idLlista: String,
    nomLlista: String,
    onTornar: () -> Unit
) {

    val repository = FirebaseRepository()

    var mostrarDialog by remember { mutableStateOf(false) }
    var nomProducte by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onTornar
        ) {
            Text("← Tornar")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = nomLlista,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                mostrarDialog = true
            }
        ) {
            Text("+ Afegir producte")
        }
    }

    if (mostrarDialog) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialog = false
            },

            title = {
                Text("Afegir producte")
            },

            text = {
                OutlinedTextField(
                    value = nomProducte,
                    onValueChange = {
                        nomProducte = it
                    },
                    label = {
                        Text("Nom del producte")
                    }
                )
            },

            confirmButton = {
                Button(
                    onClick = {

                        if (nomProducte.isNotBlank()) {

                            repository.afegirProducte(
                                idLlista = idLlista,
                                nomProducte = nomProducte
                            )

                            nomProducte = ""
                            mostrarDialog = false
                        }
                    }
                ) {
                    Text("Afegir")
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