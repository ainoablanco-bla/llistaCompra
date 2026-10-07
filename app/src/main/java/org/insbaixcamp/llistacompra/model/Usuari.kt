package org.insbaixcamp.llistacompra.model

data class ShoppingList(
    val id: String = "",
    val nom: String = "",
    val propietariId: String = "",
    val membres: List<String> = emptyList()
)