
package org.insbaixcamp.llistacompra.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.insbaixcamp.llistacompra.model.Llista
import org.insbaixcamp.llistacompra.model.Producte
import org.insbaixcamp.llistacompra.model.Categoria

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    // Crear una llista
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
                // Crear les categories inicials
                crearCategoriesInicials(document.id) { correcte, error ->
                    if (correcte) {
                        resultat(true, null)
                    } else {
                        resultat(false, error)
                    }
                }
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }

    // Crear categories automàticament si encara no n'hi ha
    fun crearCategoriesInicials(
        idLlista: String,
        resultat: ((Boolean, String?) -> Unit)? = null
    ) {
        val categoriesRef = db.collection("Llista")
            .document(idLlista)
            .collection("categories")

        categoriesRef.get()
            .addOnSuccessListener { snapshot ->

                // Si ja hi ha categories, no les dupliquem
                if (!snapshot.isEmpty) {
                    resultat?.invoke(true, null)
                    return@addOnSuccessListener
                }

                val categoriesInicials = listOf(
                    "Fruita i verdura",
                    "Carn i peix",
                    "Làctics",
                    "Pa i cereals",
                    "Begudes",
                    "Congelats",
                    "Neteja",
                    "Higiene personal",
                    "Altres"
                )

                val lot = db.batch()

                for (nomCategoria in categoriesInicials) {
                    val document = categoriesRef.document()

                    val categoria = Categoria(
                        id = document.id,
                        nom = nomCategoria
                    )

                    lot.set(document, categoria)
                }

                lot.commit()
                    .addOnSuccessListener {
                        resultat?.invoke(true, null)
                    }
                    .addOnFailureListener { error ->
                        resultat?.invoke(false, error.message)
                    }
            }
            .addOnFailureListener { error ->
                resultat?.invoke(false, error.message)
            }
    }

    // Escoltar les llistes d'un usuari
    fun escoltarLlistes(
        usuariId: String,
        onUpdate: (List<Llista>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return db.collection("Llista")
            .whereArrayContains("membres", usuariId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(
                        error.message ?: "Error al carregar les llistes"
                    )
                    return@addSnapshotListener
                }

                val llistes = snapshot?.documents?.mapNotNull {
                    it.toObject(Llista::class.java)
                } ?: emptyList()

                onUpdate(llistes)
            }
    }

    // Afegir un producte
    fun afegirProducte(
        idLlista: String,
        nomProducte: String
    ) {
        val document = db.collection("Llista")
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

    // Escoltar els productes d'una llista
    fun escoltarProductes(
        idLlista: String,
        onUpdate: (List<Producte>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return db.collection("Llista")
            .document(idLlista)
            .collection("productes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(
                        error.message ?: "Error al carregar els productes"
                    )
                    return@addSnapshotListener
                }

                val productes = snapshot?.documents?.mapNotNull {
                    it.toObject(Producte::class.java)
                } ?: emptyList()

                onUpdate(productes)
            }
    }

    // Afegir una categoria manualment
    fun afegirCategoria(
        idLlista: String,
        nom: String,
        resultat: (Boolean, String?) -> Unit
    ) {
        val document = db.collection("Llista")
            .document(idLlista)
            .collection("categories")
            .document()

        val categoria = Categoria(
            id = document.id,
            nom = nom.trim()
        )

        document.set(categoria)
            .addOnSuccessListener {
                resultat(true, null)
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }

    // Escoltar les categories d'una llista
    fun escoltarCategories(
        idLlista: String,
        onUpdate: (List<Categoria>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return db.collection("Llista")
            .document(idLlista)
            .collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(
                        error.message ?: "Error al carregar les categories"
                    )
                    return@addSnapshotListener
                }

                val categories = snapshot?.documents?.mapNotNull {
                    it.toObject(Categoria::class.java)
                } ?: emptyList()

                onUpdate(categories)
            }
    }

    // Editar una categoria
    fun editarCategoria(
        idLlista: String,
        categoriaId: String,
        nouNom: String,
        resultat: (Boolean, String?) -> Unit
    ) {
        db.collection("Llista")
            .document(idLlista)
            .collection("categories")
            .document(categoriaId)
            .update("nom", nouNom.trim())
            .addOnSuccessListener {
                resultat(true, null)
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }

    // Eliminar una categoria
    fun eliminarCategoria(
        idLlista: String,
        categoriaId: String,
        resultat: (Boolean, String?) -> Unit
    ) {
        db.collection("Llista")
            .document(idLlista)
            .collection("categories")
            .document(categoriaId)
            .delete()
            .addOnSuccessListener {
                resultat(true, null)
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }
}
