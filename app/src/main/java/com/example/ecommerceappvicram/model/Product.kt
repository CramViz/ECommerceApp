package com.example.ecommerceappvicram.model

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    var isFavorite: Boolean = false  // <-- ajoute ceci ici directement
)
