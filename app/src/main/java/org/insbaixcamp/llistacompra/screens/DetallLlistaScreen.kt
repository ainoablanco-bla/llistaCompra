
package org.insbaixcamp.llistacompra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetallLlistaScreen(
    nomLlista: String,
    onTornar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(onClick = onTornar) {
            Text("← Tornar")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = nomLlista,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            // Aquí añadiremos los productos
        }) {
            Text("+ Afegir producte")
        }
    }
}
