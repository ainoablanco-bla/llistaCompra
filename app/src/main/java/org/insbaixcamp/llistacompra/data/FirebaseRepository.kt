package org.insbaixcamp.llistacompra.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.insbaixcamp.llistacompra.model.Categoria
import org.insbaixcamp.llistacompra.model.Llista
import org.insbaixcamp.llistacompra.model.Producte
import org.insbaixcamp.llistacompra.utils.obtenirCategoria

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()


    // -------------------------
    // USUARIS
    // -------------------------

    fun guardarUsuari(
        uid: String,
        email: String
    ) {

        val dades = hashMapOf(
            "uid" to uid,
            "email" to email
        )

        db.collection("Usuaris")
            .document(uid)
            .set(dades)
    }


    // -------------------------
    // LLISTES
    // -------------------------

    fun crearLlista(
        nom: String,
        propietariId: String,
        resultat: (Boolean, String?) -> Unit
    ) {

        val document = db
            .collection("Llista")
            .document()

        val llista = Llista(
            id = document.id,
            nom = nom,
            propietariId = propietariId,
            membres = listOf(propietariId)
        )

        document.set(llista)
            .addOnSuccessListener {

                crearCategoriesInicials(
                    idLlista = document.id
                ) { correcte, error ->

                    if (correcte) {
                        resultat(true, null)
                    } else {
                        resultat(false, error)
                    }
                }
            }
            .addOnFailureListener { error ->

                resultat(
                    false,
                    error.message
                )
            }
    }


    fun escoltarLlistes(
        usuariId: String,
        onUpdate: (List<Llista>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return db
            .collection("Llista")
            .whereArrayContains(
                "membres",
                usuariId
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    onError(
                        error.message
                            ?: "Error al carregar les llistes"
                    )

                    return@addSnapshotListener
                }

                val llistes =
                    snapshot
                        ?.documents
                        ?.mapNotNull {

                            it.toObject(
                                Llista::class.java
                            )
                        }
                        ?: emptyList()

                onUpdate(llistes)
            }
    }


    fun eliminarLlista(
        idLlista: String,
        resultat: (Boolean, String?) -> Unit
    ) {
        db.collection("Llista")
            .document(idLlista)
            .delete()
            .addOnSuccessListener {
                resultat(true, null)
            }
            .addOnFailureListener { error ->
                resultat(false, error.message)
            }
    }


    // -------------------------
    // COMPARTIR LLISTA
    // -------------------------

    fun compartirLlista(
        idLlista: String,
        email: String,
        resultat: (Boolean, String?) -> Unit
    ) {

        db.collection("Usuaris")
            .whereEqualTo(
                "email",
                email.trim()
            )
            .get()
            .addOnSuccessListener { snapshot ->

                // No existeix cap usuari amb aquest correu
                if (snapshot.isEmpty) {

                    resultat(
                        false,
                        "No existeix cap usuari amb aquest correu"
                    )

                    return@addOnSuccessListener
                }

                val usuari =
                    snapshot.documents.first()

                val uid =
                    usuari.getString("uid")

                if (uid == null) {

                    resultat(
                        false,
                        "No s'ha pogut trobar l'usuari"
                    )

                    return@addOnSuccessListener
                }

                // Afegim l'usuari a membres
                db.collection("Llista")
                    .document(idLlista)
                    .update(
                        "membres",
                        FieldValue.arrayUnion(uid)
                    )
                    .addOnSuccessListener {

                        resultat(
                            true,
                            null
                        )
                    }
                    .addOnFailureListener { error ->

                        resultat(
                            false,
                            error.message
                        )
                    }
            }
            .addOnFailureListener { error ->

                resultat(
                    false,
                    error.message
                )
            }
    }


    // -------------------------
    // CATEGORIES
    // -------------------------

    fun crearCategoriesInicials(
        idLlista: String,
        resultat: ((Boolean, String?) -> Unit)? = null
    ) {

        val categoriesRef = db
            .collection("Llista")
            .document(idLlista)
            .collection("categories")

        categoriesRef.get()
            .addOnSuccessListener { snapshot ->

                // Si ja existeixen categories, no les tornem a crear
                if (!snapshot.isEmpty) {

                    resultat?.invoke(
                        true,
                        null
                    )

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

                    val document =
                        categoriesRef.document()

                    val categoria = Categoria(
                        id = document.id,
                        nom = nomCategoria
                    )

                    lot.set(
                        document,
                        categoria
                    )
                }

                lot.commit()
                    .addOnSuccessListener {

                        resultat?.invoke(
                            true,
                            null
                        )
                    }
                    .addOnFailureListener { error ->

                        resultat?.invoke(
                            false,
                            error.message
                        )
                    }
            }
            .addOnFailureListener { error ->

                resultat?.invoke(
                    false,
                    error.message
                )
            }
    }


    fun afegirCategoria(
        idLlista: String,
        nom: String,
        resultat: (Boolean, String?) -> Unit
    ) {

        val document = db
            .collection("Llista")
            .document(idLlista)
            .collection("categories")
            .document()

        val categoria = Categoria(
            id = document.id,
            nom = nom.trim()
        )

        document.set(categoria)
            .addOnSuccessListener {

                resultat(
                    true,
                    null
                )
            }
            .addOnFailureListener { error ->

                resultat(
                    false,
                    error.message
                )
            }
    }


    fun escoltarCategories(
        idLlista: String,
        onUpdate: (List<Categoria>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return db
            .collection("Llista")
            .document(idLlista)
            .collection("categories")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    onError(
                        error.message
                            ?: "Error al carregar les categories"
                    )

                    return@addSnapshotListener
                }

                val categories =
                    snapshot
                        ?.documents
                        ?.mapNotNull {

                            it.toObject(
                                Categoria::class.java
                            )
                        }
                        ?: emptyList()

                onUpdate(categories)
            }
    }


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
            .update(
                "nom",
                nouNom.trim()
            )
            .addOnSuccessListener {

                resultat(
                    true,
                    null
                )
            }
            .addOnFailureListener { error ->

                resultat(
                    false,
                    error.message
                )
            }
    }


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

                resultat(
                    true,
                    null
                )
            }
            .addOnFailureListener { error ->

                resultat(
                    false,
                    error.message
                )
            }
    }


    // -------------------------
    // PRODUCTES
    // -------------------------

    fun afegirProducte(
        idLlista: String,
        nomProducte: String
    ) {

        // Mirem automàticament quina categoria correspon
        val nomCategoria =
            obtenirCategoria(nomProducte)

        val categoriesRef = db
            .collection("Llista")
            .document(idLlista)
            .collection("categories")

        categoriesRef
            .whereEqualTo(
                "nom",
                nomCategoria
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val categoriaId =
                    snapshot
                        .documents
                        .firstOrNull()
                        ?.id
                        ?: ""

                val document = db
                    .collection("Llista")
                    .document(idLlista)
                    .collection("productes")
                    .document()

                val producte = Producte(
                    id = document.id,
                    nom = nomProducte,
                    completat = false,
                    categoriaId = categoriaId
                )

                document.set(producte)
            }
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

                    onError(
                        error.message
                            ?: "Error al carregar els productes"
                    )

                    return@addSnapshotListener
                }

                val productes =
                    snapshot
                        ?.documents
                        ?.mapNotNull {

                            it.toObject(
                                Producte::class.java
                            )
                        }
                        ?: emptyList()

                onUpdate(productes)
            }
    }


    fun canviarCompletat(
        idLlista: String,
        idProducte: String,
        completat: Boolean
    ) {

        db.collection("Llista")
            .document(idLlista)
            .collection("productes")
            .document(idProducte)
            .update(
                "completat",
                completat
            )
    }


    fun editarProducte(
        idLlista: String,
        idProducte: String,
        nouNom: String
    ) {

        db.collection("Llista")
            .document(idLlista)
            .collection("productes")
            .document(idProducte)
            .update(
                "nom",
                nouNom
            )
    }


    fun eliminarProducte(
        idLlista: String,
        idProducte: String
    ) {

        db.collection("Llista")
            .document(idLlista)
            .collection("productes")
            .document(idProducte)
            .delete()
    }
}