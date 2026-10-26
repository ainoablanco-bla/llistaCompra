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

    var producteEditar by remember {
        mutableStateOf<Producte?>(null)
    }

    var nouNomProducte by remember {
        mutableStateOf("")
    }

    var producteEliminar by remember {
        mutableStateOf<Producte?>(null)
    }
    // Escoltar els productes de Firebase
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
        }

        // Mostrar els productes
        productes.forEach { producte ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Checkbox(
                    checked = producte.completat,
                    onCheckedChange = { marcat ->

                        repository.canviarCompletat(
                            idLlista = idLlista,
                            idProducte = producte.id,
                            completat = marcat
                        )
                    }
                )

                Text(
                    text = producte.nom,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .weight(1f)
                )

                TextButton(
                    onClick = {
                        producteEditar = producte
                        nouNomProducte = producte.nom
                    }
                ) {
                    Text("Editar")
                }

                TextButton(
                    onClick = {
                        producteEliminar = producte
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }


        // DIÀLEG AFEGIR PRODUCTE
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


        // IMPORTANT: AQUEST IF VA FORA DE L'ANTERIOR
        if (producteEditar != null) {

            AlertDialog(
                onDismissRequest = {
                    producteEditar = null
                },

                title = {
                    Text("Editar producte")
                },

                text = {
                    OutlinedTextField(
                        value = nouNomProducte,
                        onValueChange = {
                            nouNomProducte = it
                        },
                        label = {
                            Text("Nom del producte")
                        }
                    )
                },

                confirmButton = {
                    Button(
                        onClick = {

                            if (nouNomProducte.isNotBlank()) {

                                repository.editarProducte(
                                    idLlista = idLlista,
                                    idProducte = producteEditar!!.id,
                                    nouNom = nouNomProducte.trim()
                                )

                                producteEditar = null
                                nouNomProducte = ""
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            producteEditar = null
                        }
                    ) {
                        Text("Cancel·lar")
                    }
                }
            )
        }

        if (producteEliminar != null) {

            AlertDialog(
                onDismissRequest = {
                    producteEliminar = null
                },

                title = {
                    Text("Eliminar producte")
                },

                text = {
                    Text(
                        "Segur que vols eliminar ${producteEliminar!!.nom}?"
                    )
                },

                confirmButton = {

                    Button(
                        onClick = {

                            repository.eliminarProducte(
                                idLlista = idLlista,
                                idProducte = producteEliminar!!.id
                            )

                            producteEliminar = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            producteEliminar = null
                        }
                    ) {
                        Text("Cancel·lar")
                    }
                }
            )
        }

    }
} // FINAL DE DetallLlistaScreen