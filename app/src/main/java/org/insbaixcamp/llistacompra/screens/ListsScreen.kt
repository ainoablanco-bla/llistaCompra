package org.insbaixcamp.llistacompra.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import org.insbaixcamp.llistacompra.data.FirebaseRepository
import org.insbaixcamp.llistacompra.model.Categoria
import org.insbaixcamp.llistacompra.model.Llista
import org.insbaixcamp.llistacompra.utils.obtenirEmojiCategoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    onLogout: (() -> Unit)? = null
) {
    val repository = remember { FirebaseRepository() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val usuariId = currentUser?.uid

    var nomLlista by remember { mutableStateOf("") }
    var llistaSeleccionada by remember { mutableStateOf<Llista?>(null) }
    var llistaEliminar by remember { mutableStateOf<Llista?>(null) }
    var mostrarCategories by remember { mutableStateOf(false) }
    var mostrarDialog by remember { mutableStateOf(false) }
    var guardant by remember { mutableStateOf(false) }
    var llistes by remember { mutableStateOf<List<Llista>>(emptyList()) }
    var errorLlistes by remember { mutableStateOf<String?>(null) }
    var errorCreacio by remember { mutableStateOf<String?>(null) }

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
        } else null

        onDispose { listener?.remove() }
    }

    // Detail / Categories Navigation
    if (llistaSeleccionada != null) {
        val llista = llistaSeleccionada!!

        if (mostrarCategories) {
            CategoriesScreen(
                llistaId = llista.id,
                nomLlista = llista.nom,
                onTornar = { mostrarCategories = false }
            )
        } else {
            DetallLlistaScreen(
                idLlista = llista.id,
                nomLlista = llista.nom,
                onTornar = {
                    llistaSeleccionada = null
                    mostrarCategories = false
                },
                onAnarCategories = { mostrarCategories = true }
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Les meves llistes 📝",
                            style = MaterialTheme.typography.titleLarge
                        )
                        currentUser?.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (onLogout != null) {
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onLogout()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Tancar sessió"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (usuariId != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        mostrarDialog = true
                        errorCreacio = null
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nova llista 🛍️") },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                AnimatedVisibility(visible = errorLlistes != null) {
                    errorLlistes?.let { err ->
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
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (usuariId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hi ha cap usuari autenticat",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else if (llistes.isEmpty() && errorLlistes == null) {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🛒",
                                    fontSize = 36.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Encara no tens cap llista",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Crea la teva primera llista de la compra premint + Nova llista",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(llistes, key = { it.id }) { llista ->
                            Card(
                                onClick = {
                                    llistaSeleccionada = llista
                                    mostrarCategories = false
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "🛒",
                                                fontSize = 20.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = llista.nom,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (llista.membres.size > 1) {
                                            Text(
                                                text = "👥 Compartida (${llista.membres.size} persones)",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    // Delete list button
                                    IconButton(
                                        onClick = { llistaEliminar = llista }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar llista",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create New List Dialog
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!guardant) mostrarDialog = false
            },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Nova llista 🛍️",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nomLlista,
                        onValueChange = { nomLlista = it },
                        label = { Text("Nom de la llista") },
                        placeholder = { Text("Ex: Supermercat, Fruiteria...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    errorCreacio?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !guardant && nomLlista.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
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
                                    errorCreacio = error ?: "Error al crear la llista"
                                }
                            }
                        }
                    }
                ) {
                    if (guardant) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Crear")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !guardant,
                    onClick = { mostrarDialog = false }
                ) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    // Confirm Delete Entire List Dialog
    if (llistaEliminar != null) {
        AlertDialog(
            onDismissRequest = { llistaEliminar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Eliminar llista 🗑️") },
            text = {
                Text(
                    text = "Segur que vols eliminar la llista \"${llistaEliminar!!.nom}\"? Aquesta acció esborrarà tots els seus productes i categories.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        val idAEliminar = llistaEliminar!!.id
                        llistaEliminar = null
                        repository.eliminarLlista(idAEliminar) { _, _ -> }
                    }
                ) {
                    Text("Eliminar llista")
                }
            },
            dismissButton = {
                TextButton(onClick = { llistaEliminar = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    llistaId: String,
    nomLlista: String = "",
    onTornar: () -> Unit
) {
    val repository = remember { FirebaseRepository() }
    var categories by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var errorCategories by remember { mutableStateOf<String?>(null) }
    var mostrarDialog by remember { mutableStateOf(false) }
    var nomCategoria by remember { mutableStateOf("") }
    var categoriaEditar by remember { mutableStateOf<Categoria?>(null) }
    var nouNomCategoria by remember { mutableStateOf("") }
    var categoriaEliminar by remember { mutableStateOf<Categoria?>(null) }

    DisposableEffect(llistaId) {
        val listener = repository.escoltarCategories(
            idLlista = llistaId,
            onUpdate = {
                categories = it
                errorCategories = null
            },
            onError = { errorCategories = it }
        )
        onDispose { listener.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories 🏷️") },
                navigationIcon = {
                    IconButton(onClick = onTornar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tornar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialog = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Afegir categoria",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                errorCategories?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (categories.isEmpty() && errorCategories == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hi ha cap categoria creada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories, key = { it.id }) { cat ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = obtenirEmojiCategoria(cat.nom),
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = cat.nom,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            categoriaEditar = cat
                                            nouNomCategoria = cat.nom
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { categoriaEliminar = cat }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
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

    // Dialog Afegir Categoria
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Nova categoria 🏷️") },
            text = {
                OutlinedTextField(
                    value = nomCategoria,
                    onValueChange = { nomCategoria = it },
                    label = { Text("Nom de la categoria") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        if (nomCategoria.isNotBlank()) {
                            repository.afegirCategoria(
                                idLlista = llistaId,
                                nom = nomCategoria.trim()
                            ) { success, _ ->
                                if (success) {
                                    nomCategoria = ""
                                    mostrarDialog = false
                                }
                            }
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

    // Dialog Editar Categoria
    if (categoriaEditar != null) {
        AlertDialog(
            onDismissRequest = { categoriaEditar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Editar categoria") },
            text = {
                OutlinedTextField(
                    value = nouNomCategoria,
                    onValueChange = { nouNomCategoria = it },
                    label = { Text("Nom de la categoria") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        if (nouNomCategoria.isNotBlank()) {
                            repository.editarCategoria(
                                idLlista = llistaId,
                                categoriaId = categoriaEditar!!.id,
                                nouNom = nouNomCategoria.trim()
                            ) { success, _ ->
                                if (success) {
                                    categoriaEditar = null
                                    nouNomCategoria = ""
                                }
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoriaEditar = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    // Dialog Eliminar Categoria
    if (categoriaEliminar != null) {
        AlertDialog(
            onDismissRequest = { categoriaEliminar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Eliminar categoria") },
            text = { Text("Vols eliminar la categoria \"${categoriaEliminar!!.nom}\"?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        repository.eliminarCategoria(
                            idLlista = llistaId,
                            categoriaId = categoriaEliminar!!.id
                        ) { success, _ ->
                            if (success) {
                                categoriaEliminar = null
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoriaEliminar = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}
