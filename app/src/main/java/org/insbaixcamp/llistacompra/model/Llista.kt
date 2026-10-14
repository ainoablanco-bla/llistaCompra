package org.insbaixcamp.llistacompra.model

data class Llista(
    val id: String = "",
    val nom: String = "",
    val propietariId: String = "",
    val membres: List<String> = emptyList()
)

