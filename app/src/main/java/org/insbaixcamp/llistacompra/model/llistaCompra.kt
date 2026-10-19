package org.insbaixcamp.llistacompra.model

data class llistaCompra(
    val id: String = "",
    val nom: String = "",
    val propietariId: String = "",
    val membres: List<String> = emptyList()
)