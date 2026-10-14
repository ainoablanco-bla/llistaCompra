
package org.insbaixcamp.llistacompra.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.insbaixcamp.llistacompra.model.Llista

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun crearLlista(nom: String, propietariId: String) {

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
            }
            .addOnFailureListener { error ->
                Log.e("FIREBASE", "Error al crear la llista", error)
            }
    }
}
