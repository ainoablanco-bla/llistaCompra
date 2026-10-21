package org.insbaixcamp.llistacompra.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.insbaixcamp.llistacompra.model.Llista
import org.insbaixcamp.llistacompra.model.Producte

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun crearLlista(
        nom: String,
        propietariId: String,
        resultat: (Boolean, String?) -> Unit
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
                resultat(true, null)
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }

    fun escoltarLlistes(
        usuariId: String,
        onUpdate: (List<Llista>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return db.collection("Llista")
            .whereArrayContains("membres", usuariId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error.message ?: "Error al carregar les llistes")
                    return@addSnapshotListener
                }

                val llistes = snapshot?.documents?.mapNotNull {
                    it.toObject(Llista::class.java)
                } ?: emptyList()

                onUpdate(llistes)
            }
    }

    fun afegirProducte(
        idLlista: String,
        nomProducte: String
    ) {

        val document = db
            .collection("Llista")
            .document(idLlista)
            .collection("productes")
            .document()

        val producte = Producte(
            id = document.id,
            nom = nomProducte,
            completat = false
        )

        document.set(producte)
    }

    fun escoltarProductes(
        idLlista: String,
        onUpdate: (List<Producte>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return db
            .collection("Llista")
            .document(idLlista)
            .collection("productes")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error.message ?: "Error al carregar els productes")
                    return@addSnapshotListener
                }

                val productes = snapshot?.documents?.mapNotNull {
                    it.toObject(Producte::class.java)
                } ?: emptyList()

                onUpdate(productes)
            }
    }
}