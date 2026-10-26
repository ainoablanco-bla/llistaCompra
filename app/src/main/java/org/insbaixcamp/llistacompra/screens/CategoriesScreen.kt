
package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.insbaixcamp.llistacompra.data.FirebaseRepository
import org.insbaixcamp.llistacompra.model.Categoria

@Composable
fun CategoriesScreen(
    llistaId: String,
    onTornar: () -> Unit
) {
    val repository = remember { FirebaseRepository() }

    LaunchedEffect(llistaId) {
        repository.crearCategoriesInicials(llistaId)
    }

    var categories by remember {
        mutableStateOf<List<Categoria>>(emptyList())
    }

    var nom by remember {
        mutableStateOf("")
    }

    var categoriaEditant by remember {
        mutableStateOf<Categoria?>(null)
    }

    var mostrarDialog by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // Escoltar les categories de la llista seleccionada
    DisposableEffect(llistaId) {
        val listener = repository.escoltarCategories(
            idLlista = llistaId,
            onUpdate = {
                categories = it
                error = null
            },
            onError = {
                error = it
            }
        )

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(
            onClick = onTornar
        ) {
            Text("← Tornar")
        }

        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Mostrar les categories
        if (categories.isEmpty() && error == null) {
            Text("Encara no hi ha categories")
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = categories,
                key = { it.id }
            ) { categoria ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = categoria.nom,
                        modifier = Modifier.weight(1f)
                    )

                    // Editar una categoria
                    TextButton(
                        onClick = {
                            categoriaEditant = categoria
                            nom = categoria.nom
                            error = null
                            mostrarDialog = true
                        }
                    ) {
                        Text("Editar")
                    }

                    // Eliminar una categoria
                    TextButton(
                        onClick = {
                            repository.eliminarCategoria(
                                idLlista = llistaId,
                                categoriaId = categoria.id
                            ) { correcte, missatgeError ->

                                if (!correcte) {
                                    error = missatgeError
                                        ?: "Error eliminant la categoria"
                                }
                            }
                        }
                    ) {
                        Text("Eliminar")
                    }
                }

                HorizontalDivider()
            }
        }

        // Crear una categoria nova
        Button(
            onClick = {
                nom = ""
                categoriaEditant = null
                error = null
                mostrarDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Afegir categoria")
        }
    }

    // Formulari per crear o editar
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialog = false
            },
            title = {
                Text(
                    if (categoriaEditant == null) {
                        "Nova categoria"
                    } else {
                        "Editar categoria"
                    }
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nom,
                        onValueChange = {
                            nom = it
                        },
                        label = {
                            Text("Nom de la categoria")
                        },
                        singleLine = true
                    )

                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nom.isBlank()) {
                            error = "Has d'escriure un nom"
                        } else {
                            val categoria = categoriaEditant

                            if (categoria == null) {
                                // Crear categoria
                                repository.afegirCategoria(
                                    idLlista = llistaId,
                                    nom = nom.trim()
                                ) { correcte, missatgeError ->

                                    if (correcte) {
                                        mostrarDialog = false
                                        error = null
                                    } else {
                                        error = missatgeError
                                            ?: "Error creant la categoria"
                                    }
                                }
                            } else {
                                // Actualitzar categoria
                                repository.editarCategoria(
                                    idLlista = llistaId,
                                    categoriaId = categoria.id,
                                    nouNom = nom.trim()
                                ) { correcte, missatgeError ->

                                    if (correcte) {
                                        mostrarDialog = false
                                        error = null
                                    } else {
                                        error = missatgeError
                                            ?: "Error editant la categoria"
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialog = false
                        error = null
                    }
                ) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}
