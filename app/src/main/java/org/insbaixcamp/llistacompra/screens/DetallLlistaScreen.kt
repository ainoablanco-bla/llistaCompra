package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.insbaixcamp.llistacompra.data.FirebaseRepository
import org.insbaixcamp.llistacompra.model.Producte

@Composable
fun DetallLlistaScreen(
    idLlista: String,
    nomLlista: String,
    onTornar: () -> Unit
) {

    val repository = remember { FirebaseRepository() }

    var mostrarDialog by remember {
        mutableStateOf(false)
    }

    var nomProducte by remember {
        mutableStateOf("")
    }

    var productes by remember {
        mutableStateOf<List<Producte>>(emptyList())
    }

    var errorProductes by remember {
        mutableStateOf<String?>(null)
    }

    // Escuchar los productos de esta lista
    DisposableEffect(idLlista) {

        val listener = repository.escoltarProductes(
            idLlista = idLlista,

            onUpdate = {
                productes = it
                errorProductes = null
            },

            onError = {
                errorProductes = it
            }
        )

        onDispose {
            listener.remove()
        }
    }

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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = nomLlista,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        errorProductes?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        if (productes.isEmpty() && errorProductes == null) {

            Text("Encara no hi ha productes")

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        productes.forEach { producte ->

            Text(
                text = producte.nom
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

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
                                nomProducte = nomProducte.trim()
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