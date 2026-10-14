
package org.insbaixcamp.llistacompra.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.insbaixcamp.llistacompra.model.Llista

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    // Crear una lista nueva
    fun crearLlista(
        nom: String,
        propietariId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val document = db.collection("Llista").document()

        val llista = Llista(
            id = document.id,
            nom = nom,
            propietariId = propietariId,
            membres = listOf(propietariId)
        )

        document.set(llista)
            .addOnSuccessListener {
                Log.d("FIREBASE", "Llista creada correctament")
                onResult(true, null)
            }
            .addOnFailureListener { error ->
                Log.e("FIREBASE", "Error al crear la llista", error)
                onResult(false, error.message)
            }
    }

    // Obtener las listas del usuario
    fun escoltarLlistes(
        usuariId: String,
        onUpdate: (List<String>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return db.collection("Llista")
            .whereArrayContains("membres", usuariId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error.message ?: "Error al carregar llistes")
                    return@addSnapshotListener
                }

                val llistes = snapshot?.documents
                    ?.mapNotNull { it.getString("nom") }
                    ?: emptyList()

                onUpdate(llistes)
            }
    }
}
