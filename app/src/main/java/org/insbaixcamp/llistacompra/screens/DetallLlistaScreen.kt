package org.insbaixcamp.llistacompra.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.insbaixcamp.llistacompra.data.FirebaseRepository
import org.insbaixcamp.llistacompra.model.Categoria
import org.insbaixcamp.llistacompra.model.Producte

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallLlistaScreen(
    idLlista: String,
    nomLlista: String,
    onTornar: () -> Unit,
    onAnarCategories: () -> Unit
) {
    val repository = remember { FirebaseRepository() }

    var mostrarDialog by remember { mutableStateOf(false) }
    var nomProducte by remember { mutableStateOf("") }
    var productes by remember { mutableStateOf<List<Producte>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var errorProductes by remember { mutableStateOf<String?>(null) }
    var producteEditar by remember { mutableStateOf<Producte?>(null) }
    var nouNomProducte by remember { mutableStateOf("") }
    var producteEliminar by remember { mutableStateOf<Producte?>(null) }
    var mostrarCompartir by remember { mutableStateOf(false) }
    var emailCompartir by remember { mutableStateOf("") }
    var missatgeCompartir by remember { mutableStateOf<String?>(null) }

    DisposableEffect(idLlista) {
        val listener = repository.escoltarProductes(
            idLlista = idLlista,
            onUpdate = {
                productes = it
                errorProductes = null
            },
            onError = { errorProductes = it }
        )
        onDispose { listener.remove() }
    }

    DisposableEffect(idLlista) {
        val listenerCategories = repository.escoltarCategories(
            idLlista = idLlista,
            onUpdate = { categories = it },
            onError = {}
        )
        onDispose { listenerCategories.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = nomLlista,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onTornar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tornar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAnarCategories) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Categories",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        mostrarCompartir = true
                        missatgeCompartir = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir llista",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Afegir producte") },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Error Alert
                AnimatedVisibility(visible = errorProductes != null) {
                    errorProductes?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                if (productes.isEmpty() && errorProductes == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "La llista està buida",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Afegeix el primer producte premint el botó inferior",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display products grouped by categories if available
                        val productsCategorized = mutableSetOf<String>()

                        categories.forEach { categoria ->
                            val productesCategoria = productes.filter { it.categoriaId == categoria.id }
                            if (productesCategoria.isNotEmpty()) {
                                productsCategorized.addAll(productesCategoria.map { it.id })
                                item(key = "cat_${categoria.id}") {
                                    Column {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = categoria.nom.uppercase(),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            productesCategoria.forEach { producte ->
                                                ProductItemRow(
                                                    producte = producte,
                                                    onCheckedChange = { marcat ->
                                                        repository.canviarCompletat(
                                                            idLlista = idLlista,
                                                            idProducte = producte.id,
                                                            completat = marcat
                                                        )
                                                    },
                                                    onEdit = {
                                                        producteEditar = producte
                                                        nouNomProducte = producte.nom
                                                    },
                                                    onDelete = { producteEliminar = producte }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Products without category or remaining
                        val remainingProducts = productes.filter { it.id !in productsCategorized }
                        if (remainingProducts.isNotEmpty()) {
                            item(key = "cat_other") {
                                Column {
                                    if (categories.isNotEmpty() && productsCategorized.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "ALTRES",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        remainingProducts.forEach { producte ->
                                            ProductItemRow(
                                                producte = producte,
                                                onCheckedChange = { marcat ->
                                                    repository.canviarCompletat(
                                                        idLlista = idLlista,
                                                        idProducte = producte.id,
                                                        completat = marcat
                                                    )
                                                },
                                                onEdit = {
                                                    producteEditar = producte
                                                    nouNomProducte = producte.nom
                                                },
                                                onDelete = { producteEliminar = producte }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Afegir Producte
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Afegir producte") },
            text = {
                OutlinedTextField(
                    value = nomProducte,
                    onValueChange = { nomProducte = it },
                    label = { Text("Nom del producte") },
                    placeholder = { Text("Ex: Llet, Pa, Pomes...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
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
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    // Dialog Editar Producte
    if (producteEditar != null) {
        AlertDialog(
            onDismissRequest = { producteEditar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Editar producte") },
            text = {
                OutlinedTextField(
                    value = nouNomProducte,
                    onValueChange = { nouNomProducte = it },
                    label = { Text("Nom del producte") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
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
                TextButton(onClick = { producteEditar = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    // Dialog Eliminar Producte
    if (producteEliminar != null) {
        AlertDialog(
            onDismissRequest = { producteEliminar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Eliminar producte") },
            text = { Text("Segur que vols eliminar \"${producteEliminar!!.nom}\"?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
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
                TextButton(onClick = { producteEliminar = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    // Dialog Compartir Llista
    if (mostrarCompartir) {
        AlertDialog(
            onDismissRequest = { mostrarCompartir = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Compartir llista") },
            text = {
                Column {
                    Text(
                        text = "Introdueix el correu de l'usuari amb qui vols compartir la llista:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = emailCompartir,
                        onValueChange = { emailCompartir = it },
                        label = { Text("Correu de l'usuari") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    missatgeCompartir?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (msg.contains("correctament")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        if (emailCompartir.isNotBlank()) {
                            repository.compartirLlista(
                                idLlista = idLlista,
                                email = emailCompartir.trim()
                            ) { correcte, error ->
                                if (correcte) {
                                    missatgeCompartir = "Llista compartida correctament"
                                    emailCompartir = ""
                                } else {
                                    missatgeCompartir = error ?: "Error al compartir"
                                }
                            }
                        }
                    }
                ) {
                    Text("Compartir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCompartir = false }) {
                    Text("Tancar")
                }
            }
        )
    }
}

@Composable
fun ProductItemRow(
    producte: Producte,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (producte.completat) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (producte.completat) 0.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = producte.completat,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = producte.nom,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (producte.completat) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (producte.completat) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
